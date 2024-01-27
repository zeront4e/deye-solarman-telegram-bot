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

package io.github.zeront4e.dstb.telegram.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public interface BotCommand {
    String getCommandName();

    void onCommandRead(List<String> commandArguments, Update update, TelegramBot telegramBot) throws Exception;
}
