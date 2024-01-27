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

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import io.github.zeront4e.dstb.Constants;
import io.github.zeront4e.dstb.data.DbDailyMeasuringPointsManager;
import io.github.zeront4e.dstb.telegram.bot.BotCommand;
import io.github.zeront4e.dstb.util.DailyMeasurementDataChartsUtil;

import java.time.LocalDateTime;
import java.util.List;

public class WattWeekStatisticsBotCommand implements BotCommand {
    private final DbDailyMeasuringPointsManager dbDailyMeasuringPointsManager;

    public WattWeekStatisticsBotCommand(DbDailyMeasuringPointsManager dbDailyMeasuringPointsManager) {
        this.dbDailyMeasuringPointsManager = dbDailyMeasuringPointsManager;
    }

    @Override
    public String getCommandName() {
        return "watt_week";
    }

    @Override
    public void onCommandRead(List<String> commandArguments, Update update, TelegramBot telegramBot) throws Exception {
        //Read values for the last seven days (if present).

        int currentDay = LocalDateTime.now().getDayOfMonth();

        int startDay = currentDay - 7;

        //Always start at least with the first day.
        if(startDay <= 0)
            startDay = 1;

        LocalDateTime startLocalDateTime = LocalDateTime.now()
                .withHour(0).withMinute(0).withSecond(0)
                .withDayOfMonth(startDay);

        LocalDateTime stopLocalDateTime;

        if(currentDay > 1) {
            //Pick the previous day, to keep the range of seven days.
            stopLocalDateTime = LocalDateTime.now()
                    .withHour(0).withMinute(0).withSecond(0)
                    .minusDays(1);
        }
        else {
            //Pick the current day (use all available data).
            stopLocalDateTime = LocalDateTime.now();
        }

        byte[] imageBytes = DailyMeasurementDataChartsUtil.generateDaysKwhChartOrNull(dbDailyMeasuringPointsManager,
                startLocalDateTime, stopLocalDateTime);

        //Only generate chart, if data is available.
        if(imageBytes == null) {
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "I'm sorry, but I don't " +
                    "have enough data for the last seven days. " + Constants.Emojis.SAD + " Please try again later.");

            telegramBot.execute(sendMessage);

            return;
        }

        //Send generated chart.

        SendPhoto sendPhoto = new SendPhoto(update.message().chat().id(), imageBytes);

        sendPhoto.caption("Here is the kWh consumption data from the last seven days. " + Constants.Emojis.STATISTICS);

        telegramBot.execute(sendPhoto);
    }
}
