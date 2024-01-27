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
import io.github.zeront4e.dstb.Constants;
import io.github.zeront4e.dstb.data.DeyeDataContainer;
import io.github.zeront4e.dstb.telegram.bot.BotCommand;
import io.github.zeront4e.dstb.util.StringFormatUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class StatusCurrentBotCommand implements BotCommand {
    private final AtomicReference<DeyeDataContainer> deyeDataContainer;

    public StatusCurrentBotCommand(AtomicReference<DeyeDataContainer> deyeDataContainer) {
        this.deyeDataContainer = deyeDataContainer;
    }

    @Override
    public String getCommandName() {
        return "status_current";
    }

    @Override
    public void onCommandRead(List<String> commandArguments, Update update, TelegramBot telegramBot) {
        DeyeDataContainer readDataContainer = deyeDataContainer.get();

        if(readDataContainer == null) {
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "I'm sorry, but I don't " +
                    "have any data yet. " + Constants.Emojis.SAD + " Please try again later.");

            telegramBot.execute(sendMessage);

            return;
        }

        double pvKw = (readDataContainer.getJustInTimePvData().getPv1InPowerWatt() +
                readDataContainer.getJustInTimePvData().getPv2InPowerWatt()) / 1000;

        double batteryPercentage = readDataContainer.getJustInTimeBatteryData().getBatterySocPercentage();
        double batteryPercentageKwh = readDataContainer.getJustInTimeBatteryData().getBatterySocPercentage() / 100 * 12;

        double gridPowerInKw = readDataContainer.getJustInTimeGridData().getGridTotalPowerWatt() / 1000;

        double housePowerInKw = readDataContainer.getJustInTimeLoadData().getLoadTotalPowerWatt() / 1000;

        String responseStringBuilder = "Current status (timestamp " +
                readDataContainer.getJustInTimeInverterData().getInverterTimeString() + "):" +
        "\n" + Constants.Emojis.SUN + " PV kW: " +
                StringFormatUtil.formatDouble(pvKw) +
        "\n" + Constants.Emojis.BATTERY + " Battery percentage: " +
                StringFormatUtil.formatDouble(batteryPercentage) +
        "\n" + Constants.Emojis.BATTERY + " Battery percentage kWh: " +
                StringFormatUtil.formatDouble(batteryPercentageKwh) +
        "\n" + Constants.Emojis.EURO + " Grid power kW: " +
                StringFormatUtil.formatDouble(gridPowerInKw) +
        "\n" + Constants.Emojis.HOUSE + " House power kW: " +
                StringFormatUtil.formatDouble(housePowerInKw);

        telegramBot.execute(new SendMessage(update.message().chat().id(), responseStringBuilder));
    }
}
