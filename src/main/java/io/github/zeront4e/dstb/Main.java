/*
Copyright 2024 zeront4e (https://github.com/zeront4e)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.github.zeront4e.dstb;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.github.zeront4e.dstb.data.*;
import io.github.zeront4e.dstb.data.dao.DbBasicMeasuringPoint;
import io.github.zeront4e.dstb.data.dao.DbDailyMeasuringPoint;
import io.github.zeront4e.dstb.data.dao.DbTelegramUser;
import io.github.zeront4e.dstb.scheduler.jobs.DeyeCommandExecutorJob;
import io.github.zeront4e.dstb.telegram.bot.TokenProtectedTelegramBot;
import io.github.zeront4e.dstb.telegram.bot.commands.*;
import io.github.zeront4e.dstb.util.CachedJsonClient;
import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class Main {
    public static void main(String[] args) throws Exception {
        Configurator.setRootLevel(Level.INFO); //Workaround.

        AppConfig appConfig = ConfigFactory.create(AppConfig.class);

        //Setup database.

        log.info("Try to perform database setup.");

        JdbcPooledConnectionSource connectionSource = setupDatabaseConnectionSource(appConfig);

        //Create tables.

        TableUtils.createTableIfNotExists(connectionSource, DbTelegramUser.class);

        TableUtils.createTableIfNotExists(connectionSource, DbBasicMeasuringPoint.class);

        TableUtils.createTableIfNotExists(connectionSource, DbDailyMeasuringPoint.class);

        //Create DAO classes.

        Dao<DbTelegramUser, Long> dbTelegramUserDao =
                DaoManager.createDao(connectionSource, DbTelegramUser.class);

        Dao<DbBasicMeasuringPoint, Long> dbBasicMeasuringPointDao =
                DaoManager.createDao(connectionSource, DbBasicMeasuringPoint.class);

        Dao<DbDailyMeasuringPoint, Long> dbDailyMeasuringPointDao =
                DaoManager.createDao(connectionSource, DbDailyMeasuringPoint.class);

        boolean generateFakeData = Arrays.stream(args).anyMatch("generate-fake-data"::equalsIgnoreCase);

        if(generateFakeData) {
            log.info("Try to clear data tables to generate new fake-data.");

            TableUtils.clearTable(connectionSource, DbBasicMeasuringPoint.class);
            TableUtils.clearTable(connectionSource, DbDailyMeasuringPoint.class);

            log.info("Try to populate data tables with new fake-data.");

            generateFakeData(dbBasicMeasuringPointDao, dbDailyMeasuringPointDao);
        }

        //Setup DAO managers.

        DbTelegramUsersManager dbTelegramUsersManager = new DbTelegramUsersManager(dbTelegramUserDao);

        DbBasicMeasuringPointsManager dbBasicMeasuringPointsManager =
                new DbBasicMeasuringPointsManager(dbBasicMeasuringPointDao);

        DbDailyMeasuringPointsManager dbDailyMeasuringPointsManager =
                new DbDailyMeasuringPointsManager(dbDailyMeasuringPointDao);

        //Setup data extraction logic.

        log.info("Try to start data-extraction scheduler.");

        AtomicReference<DeyeDataContainer> deyeDataContainerReference = new AtomicReference<>(null);

        DeyeCommandExecutorJob.JsonDataReadCallback jsonDataReadCallback = jsonObject -> {
            log.info("Read Deye JSON data.");

            //Create unified data container.
            deyeDataContainerReference.set(new DeyeDataContainer(jsonObject));

            //Try to store daily measurement, if the store-time is reached.
            dbDailyMeasuringPointsManager.tryToStoreDailyMeasuringPoint(deyeDataContainerReference.get());

            //Try to store new basic measurement.
            dbBasicMeasuringPointsManager.tryToStoreBasicMeasuringPoint(deyeDataContainerReference.get());
        };

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        setupDataLoggerDataExtractionJob(appConfig, scheduledExecutorService, jsonDataReadCallback);

        //Setup weather client.

        CachedJsonClient cachedJsonClient = new CachedJsonClient(appConfig.weatherApiOpenMeteoJsonDataGetUrl(),
                appConfig.weatherApiRefreshIntervalMinutes());

        //Setup Telegram bot.

        if(appConfig.telegramUserInteractionBotApiKey().isBlank()) {
            log.warn("Unable to setup user-interaction Telegram bot, because no API key was configured.");
        }
        else {
            log.info("Try to setup user-interaction Telegram bot.");

            TokenProtectedTelegramBot userTelegramBot =
                    new TokenProtectedTelegramBot(appConfig.telegramUserInteractionBotApiKey(),
                            appConfig.telegramUserInteractionBotAccessToken(), dbTelegramUsersManager);

            userTelegramBot.addSecuredBotCommand(new StartBotCommand());

            userTelegramBot.addSecuredBotCommand(new WattTodayStatisticsBotCommand(dbBasicMeasuringPointsManager,
                    appConfig.statisticsMaxMeasuringPointsPerHour()));

            userTelegramBot.addSecuredBotCommand(new WattYesterdayStatisticsBotCommand(dbBasicMeasuringPointsManager,
                    appConfig.statisticsMaxMeasuringPointsPerHour()));

            userTelegramBot.addSecuredBotCommand(new WattWeekStatisticsBotCommand(dbDailyMeasuringPointsManager));

            userTelegramBot.addSecuredBotCommand(new WattMonthStatisticsBotCommand(dbDailyMeasuringPointsManager));

            userTelegramBot.addSecuredBotCommand(new WattYearStatisticsBotCommand(dbDailyMeasuringPointsManager));

            userTelegramBot.addSecuredBotCommand(new WeatherPvTodayStatisticsBotCommand(cachedJsonClient,
                    dbBasicMeasuringPointsManager, appConfig.statisticsMaxMeasuringPointsPerHour()));

            userTelegramBot.addSecuredBotCommand(new StatusCurrentBotCommand(deyeDataContainerReference));

            userTelegramBot.addSecuredBotCommand(new StatusDayBotCommand(deyeDataContainerReference));
        }
    }

    private static void setupDataLoggerDataExtractionJob(AppConfig appConfig,
                                                         ScheduledExecutorService scheduledExecutorService,
                                                         DeyeCommandExecutorJob.JsonDataReadCallback jsonDataReadCallback) {
        long loggerContactIntervalMilliseconds = appConfig.deyeLoggerFetchIntervalSeconds() * 1000L;

        DeyeCommandExecutorJob deyeCommandExecutorJob = new DeyeCommandExecutorJob(loggerContactIntervalMilliseconds,
                jsonDataReadCallback);

        scheduledExecutorService.scheduleAtFixedRate(deyeCommandExecutorJob,
                appConfig.schedulerLoggerFetchTryIntervalSeconds(),
                appConfig.schedulerLoggerFetchTryIntervalSeconds(),
                TimeUnit.SECONDS);
    }

    private static JdbcPooledConnectionSource setupDatabaseConnectionSource(AppConfig appConfig) throws SQLException {
        //Create connection source.

        File databaseFile = new File(appConfig.databaseFilePrefix());

        JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource("jdbc:h2:" +
                databaseFile.getAbsolutePath());

        //Add shutdown-hook.

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connectionSource.close();
            }
            catch (Exception exception) {
                log.error("Unable to close connection source.", exception);
            }
        }));

        return connectionSource;
    }

    private static void generateFakeData(Dao<DbBasicMeasuringPoint, Long> dbBasicMeasuringPointDao,
                                         Dao<DbDailyMeasuringPoint, Long> dbDailyMeasuringPointDao) {
        log.info("Try to generate fake data.");

        List<DbBasicMeasuringPoint> dbBasicMeasuringPoints = new ArrayList<>();

        dbBasicMeasuringPoints.addAll(FakeDbBasicMeasuringPointsDataUtil.createFakeMeasuringPoints(50,
                2023, 11, 1, 30, 7, 2000, 3000));

        dbBasicMeasuringPoints.addAll(FakeDbBasicMeasuringPointsDataUtil.createFakeMeasuringPoints(50,
                2023, 12, 1, 31, 7, 2000, 3000));

        dbBasicMeasuringPoints.forEach(tmpEntry -> {
            try {
                dbBasicMeasuringPointDao.create(tmpEntry);
            }
            catch (Exception exception) {
                log.error("Unable to insert basic measuring point.", exception);
            }
        });

        List<DbDailyMeasuringPoint> dbDailyMeasuringPoints = new ArrayList<>();

        for(int tmpMonth = 1; tmpMonth <= 12; tmpMonth++) {
            dbDailyMeasuringPoints.addAll(FakeDbDailyMeasuringPointsDataUtil.createFakeMeasuringPoints(2023,
                    tmpMonth, 1, 27, 2000, 3000));
        }

        dbDailyMeasuringPoints.forEach(tmpEntry -> {
            try {
                dbDailyMeasuringPointDao.create(tmpEntry);
            }
            catch (Exception exception) {
                log.error("Unable to insert daily measuring point.", exception);
            }
        });

        log.info("Generated fake data.");
    }
}
