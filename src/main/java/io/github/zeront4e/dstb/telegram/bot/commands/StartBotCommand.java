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
import io.github.zeront4e.dstb.telegram.bot.BotCommand;

import java.util.List;

public class StartBotCommand implements BotCommand {
    @Override
    public String getCommandName() {
        return "start";
    }

    @Override
    public void onCommandRead(List<String> commandArguments, Update update, TelegramBot telegramBot) {
        telegramBot.execute(new SendMessage(update.message().chat().id(), "Hello " +
                update.message().from().firstName() + "! " + Constants.Emojis.WAVING_HAND +
                " Please type or select a predefined command. " + Constants.Emojis.SMILE));
    }
}
