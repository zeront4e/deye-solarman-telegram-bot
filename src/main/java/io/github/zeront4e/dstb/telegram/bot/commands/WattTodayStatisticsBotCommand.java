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
import io.github.zeront4e.dstb.data.DbBasicMeasuringPointsManager;
import io.github.zeront4e.dstb.telegram.bot.BotCommand;
import io.github.zeront4e.dstb.util.BasicMeasurementDataChartsUtil;

import java.time.LocalDateTime;
import java.util.List;

public class WattTodayStatisticsBotCommand implements BotCommand {
    private final DbBasicMeasuringPointsManager dbBasicMeasuringPointsManager;

    private final int maxMeasuringPointsPerHour;

    public WattTodayStatisticsBotCommand(DbBasicMeasuringPointsManager dbBasicMeasuringPointsManager,
                                         int maxMeasuringPointsPerHour) {
        this.dbBasicMeasuringPointsManager = dbBasicMeasuringPointsManager;

        this.maxMeasuringPointsPerHour = maxMeasuringPointsPerHour;
    }

    @Override
    public String getCommandName() {
        return "watt_today";
    }

    @Override
    public void onCommandRead(List<String> commandArguments, Update update, TelegramBot telegramBot) throws Exception {
        //Read values from today.

        LocalDateTime nowLocalDateTime = LocalDateTime.now();

        byte[] imageBytes = BasicMeasurementDataChartsUtil.generateDayHoursWattChartOrNull(
                dbBasicMeasuringPointsManager, nowLocalDateTime, maxMeasuringPointsPerHour);

        //Only generate chart, if data is available.
        if(imageBytes == null) {
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "I'm sorry, but I don't " +
                    "have any data for today. " + Constants.Emojis.SAD + " Please try again later.");

            telegramBot.execute(sendMessage);

            return;
        }

        //Send generated chart.

        SendPhoto sendPhoto = new SendPhoto(update.message().chat().id(), imageBytes);

        sendPhoto.caption("Here is the Watt data from today. " + Constants.Emojis.STATISTICS);

        telegramBot.execute(sendPhoto);
    }
}
