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

package io.github.zeront4e.dstb.telegram.bot.commands;

import com.google.gson.JsonObject;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import io.github.zeront4e.dstb.Constants;
import io.github.zeront4e.dstb.telegram.bot.BotCommand;
import io.github.zeront4e.dstb.util.CachedJsonClient;
import io.github.zeront4e.dstb.data.DbBasicMeasuringPointsManager;
import io.github.zeront4e.dstb.util.BasicMeasurementDataChartsUtil;
import io.github.zeront4e.dstb.util.OpenMeteoWeatherDataChartsUtil;
import io.github.zeront4e.dstb.util.StringFormatUtil;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
public class WeatherPvTodayStatisticsBotCommand implements BotCommand {
    private final CachedJsonClient cachedJsonClient;

    private final DbBasicMeasuringPointsManager dbBasicMeasuringPointsManager;

    private final int maxMeasuringPointsPerHour;

    public WeatherPvTodayStatisticsBotCommand(CachedJsonClient cachedJsonClient,
                                              DbBasicMeasuringPointsManager dbBasicMeasuringPointsManager,
                                              int maxMeasuringPointsPerHour) {
        this.cachedJsonClient = cachedJsonClient;

        this.dbBasicMeasuringPointsManager = dbBasicMeasuringPointsManager;

        this.maxMeasuringPointsPerHour = maxMeasuringPointsPerHour;
    }

    @Override
    public String getCommandName() {
        return "weather_pv_today";
    }

    @Override
    public void onCommandRead(List<String> commandArguments, Update update, TelegramBot telegramBot) throws Exception {
        //Read values from today.

        LocalDateTime nowLocalDateTime = LocalDateTime.now();

        byte[] pvImageBytes = BasicMeasurementDataChartsUtil.generateDayHoursWattChartOrNull(
                dbBasicMeasuringPointsManager, nowLocalDateTime, maxMeasuringPointsPerHour, true,
                false, false, false);

        //Only generate chart, if data is available.
        if(pvImageBytes == null) {
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "I'm sorry, but I don't " +
                    "have any data for today. " + Constants.Emojis.SAD + " Please try again later.");

            telegramBot.execute(sendMessage);

            return;
        }

        //Send generated chart.

        SendPhoto sendPhoto = new SendPhoto(update.message().chat().id(), pvImageBytes);

        sendPhoto.caption("Here are the PV statistics for today. " + Constants.Emojis.STATISTICS);

        telegramBot.execute(sendPhoto);

        try {
            JsonObject weatherDataJsonObject = cachedJsonClient.getWeatherDataForTodayOrFail();

            LocalDateTime localDateTime = LocalDateTime.now();

            byte[] weatherImageBytes = OpenMeteoWeatherDataChartsUtil.generateWeatherChart(localDateTime,
                    weatherDataJsonObject);

            sendPhoto = new SendPhoto(update.message().chat().id(), weatherImageBytes);

            sendPhoto.caption("Here are the weather-statistics for today. " + Constants.Emojis.CLOUD_WITH_SUN);

            telegramBot.execute(sendPhoto);

            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "Current forecast:\n" +
                    createOpenMeteoWeatherStatisticsString(weatherDataJsonObject));

            telegramBot.execute(sendMessage);
        }
        catch (Exception exception) {
            log.error("Unable to generate weather statistics.", exception);

            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "I'm sorry, but I'm unable " +
                    "to send you the weather statistics. " + Constants.Emojis.SAD + " Cause: " +
                    exception.getMessage());

            telegramBot.execute(sendMessage);
        }
    }

    private String createOpenMeteoWeatherStatisticsString(JsonObject weatherDataJsonObject) {
        StringBuilder dailyStatistics = new StringBuilder();

        JsonObject dailyJsonObject = weatherDataJsonObject.getAsJsonObject("daily");

        dailyStatistics.append(Constants.Emojis.TEMPERATURE + " Min temperature (°C): ")
                .append(StringFormatUtil.formatDouble(dailyJsonObject.getAsJsonArray("temperature_2m_min")
                        .get(0).getAsDouble()));

        dailyStatistics.append("\n" + Constants.Emojis.TEMPERATURE + " Max temperature (°C): ")
                .append(StringFormatUtil.formatDouble(dailyJsonObject.getAsJsonArray("temperature_2m_max")
                        .get(0).getAsDouble()));

        dailyStatistics.append("\n" + Constants.Emojis.SUN + " Sunrise: ")
                .append(dailyJsonObject.getAsJsonArray("sunrise").get(0).getAsString());

        dailyStatistics.append("\n" + Constants.Emojis.SUN + " Sunset: ")
                .append(dailyJsonObject.getAsJsonArray("sunset").get(0).getAsString());

        double sunshineDuration = dailyJsonObject.getAsJsonArray("sunshine_duration").get(0)
                .getAsDouble();

        sunshineDuration = sunshineDuration / 3600; //Convert to hours.

        dailyStatistics.append("\n" + Constants.Emojis.SUN + " Sunshine duration (hours): ")
                .append(StringFormatUtil.formatDouble(sunshineDuration));

        dailyStatistics.append("\n" + Constants.Emojis.CLOUD_WITH_RAIN + " Rain (mm): ")
                .append(StringFormatUtil.formatDouble(dailyJsonObject.getAsJsonArray("rain_sum")
                        .get(0).getAsDouble()));

        dailyStatistics.append("\n" + Constants.Emojis.CLOUD_WITH_SNOW + " Showers (mm): ")
                .append(StringFormatUtil.formatDouble(dailyJsonObject.getAsJsonArray("showers_sum")
                        .get(0).getAsDouble()));

        dailyStatistics.append("\n" + Constants.Emojis.CLOUD_WITH_SNOW + " Snowfall (cm): ")
                .append(StringFormatUtil.formatDouble(dailyJsonObject.getAsJsonArray("snowfall_sum")
                        .get(0).getAsDouble()));

        dailyStatistics.append("\n" + Constants.Emojis.UMBRELLA + " Precipitation hours: ")
                .append(StringFormatUtil.formatDouble(dailyJsonObject.getAsJsonArray("precipitation_hours")
                        .get(0).getAsDouble()));

        return dailyStatistics.toString();
    }
}
