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

package io.github.zeront4e.dstb.data;

import com.j256.ormlite.dao.Dao;
import io.github.zeront4e.dstb.AppConfig;
import io.github.zeront4e.dstb.data.dao.DbDailyMeasuringPoint;
import io.github.zeront4e.dstb.util.InverterTimestampUtil;
import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class DbDailyMeasuringPointsManager {
    private static final AppConfig APP_CONFIG = ConfigFactory.create(AppConfig.class);

    private static final SimpleDateFormat DATE_SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private final AtomicBoolean dailyDataWasStored = new AtomicBoolean(false);

    private final Dao<DbDailyMeasuringPoint, Long> dbDailyMeasuringPointDao;

    public DbDailyMeasuringPointsManager(Dao<DbDailyMeasuringPoint, Long> dbDailyMeasuringPointDao) {
        this.dbDailyMeasuringPointDao = dbDailyMeasuringPointDao;
    }

    public DbDailyMeasuringPoint getDbDailyMeasuringPointOrNull(Date date) throws SQLException {
        String dateString = DATE_SIMPLE_DATE_FORMAT.format(date);

        List<DbDailyMeasuringPoint> dbDailyMeasuringPoints = dbDailyMeasuringPointDao.queryBuilder()
                .where()
                .eq("dateString", dateString)
                .query();

        if(dbDailyMeasuringPoints.isEmpty())
            return null;

        return dbDailyMeasuringPoints.get(0);
    }

    public List<DbDailyMeasuringPoint> getDbDailyMeasuringPoints(Date startDate, Date endDate) throws SQLException {
        return dbDailyMeasuringPointDao.queryBuilder()
                .where()
                .between("timestamp", startDate.getTime(), endDate.getTime())
                .query();
    }

    public List<DbDailyMeasuringPoint> getDbDailyMeasuringPoints() throws SQLException {
        return dbDailyMeasuringPointDao.queryForAll();
    }

    public void tryToStoreDailyMeasuringPoint(DeyeDataContainer deyeDataContainer) {
        //Check if the daily measurement should be stored.

        LocalTime localTime = LocalTime.now();

        boolean timeElapsed = localTime.getHour() >= APP_CONFIG.schedulerLoggerDailyMeasurementsSaveHour() &&
                localTime.getMinute() >= APP_CONFIG.schedulerLoggerDailyMeasurementsSaveMinute();

        if(timeElapsed) {
            if(!dailyDataWasStored.get()) {
                try {
                    DbDailyMeasuringPoint dbDailyMeasuringPoint =
                            getDbDailyMeasuringPointOrNull(Date.from(Instant.now()));

                    if(dbDailyMeasuringPoint == null) {
                        log.info("Try to store daily measuring point.");

                        dbDailyMeasuringPoint = new DbDailyMeasuringPoint();

                        //Set time values.

                        String dateString = DATE_SIMPLE_DATE_FORMAT.format(Date.from(Instant.now()));

                        dbDailyMeasuringPoint.setDateString(dateString);

                        dbDailyMeasuringPoint.setTimestamp(System.currentTimeMillis());

                        String inverterTimeString = deyeDataContainer.getJustInTimeInverterData()
                                .getInverterTimeString();

                        long inverterTimestamp = InverterTimestampUtil.parseStringToMilliseconds(inverterTimeString);

                        dbDailyMeasuringPoint.setDeviceTimestamp(inverterTimestamp);

                        //Set inverter data.

                        dbDailyMeasuringPoint.setInverterTodayActivePowerKilowattHours(deyeDataContainer
                                .getJustInTimeInverterData().getInverterTodayActivePowerKilowattHours());

                        dbDailyMeasuringPoint.setInverterTodayGridConnectivityMinutes(deyeDataContainer
                                .getJustInTimeInverterData().getInverterTodayGridConnectivityMinutes());

                        dbDailyMeasuringPoint.setInverterTodayBatteryChargeKilowattHours(deyeDataContainer
                                .getJustInTimeInverterData().getInverterTodayBatteryChargeKilowattHours());

                        dbDailyMeasuringPoint.setInverterTodayBatteryDischargeKilowattHours(deyeDataContainer.
                                getJustInTimeInverterData().getInverterTodayBatteryDischargeKilowattHours());

                        dbDailyMeasuringPoint.setInverterTodayBoughtFromGridKilowattHours(deyeDataContainer.
                                getJustInTimeInverterData().getInverterTodayBoughtFromGridKilowattHours());

                        dbDailyMeasuringPoint.setInverterTodaySoldToGridKilowattHours(deyeDataContainer
                                .getJustInTimeInverterData().getInverterTodaySoldToGridKilowattHours());

                        dbDailyMeasuringPoint.setInverterTodayToLoadKilowattHours(deyeDataContainer
                                .getJustInTimeInverterData().getInverterTodayToLoadKilowattHours());

                        dbDailyMeasuringPoint.setInverterTodayFromPvKilowattHours(deyeDataContainer
                                .getJustInTimeInverterData().getInverterSolarTodayFromPvKilowattHours());

                        dbDailyMeasuringPoint.setInverterTodayFromPvS1KilowattHours(deyeDataContainer
                                .getJustInTimeInverterData().getInverterSolarTodayFromPvS1KilowattHours());

                        dbDailyMeasuringPoint.setInverterTodayFromPvS2KilowattHours(deyeDataContainer
                                .getJustInTimeInverterData().getInverterSolarTodayFromPvS2KilowattHours());

                        //Store measuring point.

                        dbDailyMeasuringPointDao.create(dbDailyMeasuringPoint);

                        log.info("Stored daily measuring point.");
                    }
                    else {
                        log.info("There is already an existing daily measuring-point. Set to stored.");
                    }

                    dailyDataWasStored.set(true);
                }
                catch (Exception exception) {
                    log.error("Unable to store daily measuring point.", exception);
                }
            }
        }
        else {
            dailyDataWasStored.set(false);
        }
    }
}