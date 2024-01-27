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

import org.aeonbits.owner.Config;

@Config.Sources({ "file:app.properties" })
public interface AppConfig extends Config {
    /**
     * The database prefix to use when the database file is stored (e.g. "data.h2" causes the creation of the file
     * "data.h2.mv.db").
     * @return The database prefix to use when the database file is stored.
     */
    @DefaultValue("data.h2")
    String databaseFilePrefix();

    /**
     * The temporary directory where the fetched JSON data, from the Deye data-logger, is stored.
     * @return The temporary directory where the fetched JSON data is stored.
     */
    @DefaultValue("deye-tmp-data")
    String deyeTmpDataDirectoryPath();

    /**
     * The IP address of the Deye data-logger.
     * @return The IP address of the Deye data-logger.
     */
    @DefaultValue("192.168.42.10")
    String deyeLoggerIp();

    /**
     * The serial number of the Deye data-logger.
     * @return The serial number of the Deye data-logger.
     */
    @DefaultValue("enter-serial-number-in-config")
    String deyeLoggerSerialNumber();

    /**
     * The minimum interval between a successful fetch and a fetch-try.
     * @return The interval between a successful fetch and a fetch-try.
     */
    @DefaultValue("60")
    int deyeLoggerFetchIntervalSeconds();

    /**
     * The minimum interval between a failed fetch and a fetch-retry.
     * @return The interval between a failed fetch and a fetch-retry
     */
    @DefaultValue("10")
    int schedulerLoggerFetchTryIntervalSeconds();

    /**
     * The hour when the daily statistics should be stored.
     * @return Hour to store the daily statistics.
     */
    @DefaultValue("23")
    int schedulerLoggerDailyMeasurementsSaveHour();

    /**
     * The minute when the daily statistics should be stored.
     * @return Minute to store the daily statistics.
     */
    @DefaultValue("50")
    int schedulerLoggerDailyMeasurementsSaveMinute();

    /**
     * The API key for the created Telegram bot (the key is issued by the BotFather bot).
     * See: <a href="https://telegram.me/BotFather">https://telegram.me/BotFather</a>
     * @return The created Telegram bot API key.
     */
    @DefaultValue("") //Leave empty to prevent the bot creation by default!
    String telegramUserInteractionBotApiKey();

    /**
     * The access token, required to interact with the Telegram bot.
     * @return The access token, required to interact with the Telegram bot.
     */
    @DefaultValue("") //Leave empty to automatically block all access by default!
    String telegramUserInteractionBotAccessToken();

    /**
     * The amount of data points to process, while generating diagrams. The amount of available data points depend on
     * the configured value of the property "deyeLoggerFetchIntervalSeconds".
     * @return The amount of data points to process, while generating diagrams.
     */
    @DefaultValue("30")
    int statisticsMaxMeasuringPointsPerHour();

    /**
     * The URL to fetch weather-data from (currently "open-meteo.com", with a daily forecast for coordinates).
     * @return The URL to fetch weather-data from.
     */
    @DefaultValue("https://api.open-meteo.com/v1/forecast?latitude=52.5243&longitude=13.4105&hourly=temperature_2m,relative_humidity_2m,precipitation_probability,precipitation,cloud_cover,visibility&daily=weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,sunshine_duration,precipitation_sum,rain_sum,showers_sum,snowfall_sum,precipitation_hours,precipitation_probability_max&timezone=Europe%2FBerlin&forecast_days=1")
    String weatherApiOpenMeteoJsonDataGetUrl();

    /**
     * The interval between fetch-requests to the weather API.
     * @return The interval between fetch-requests to the weather API.
     */
    @DefaultValue("3")
    int weatherApiRefreshIntervalMinutes();
}
