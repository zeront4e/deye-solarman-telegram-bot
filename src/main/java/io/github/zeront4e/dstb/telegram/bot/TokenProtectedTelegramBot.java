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

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import io.github.zeront4e.dstb.Constants;
import io.github.zeront4e.dstb.data.DbTelegramUsersManager;
import io.github.zeront4e.dstb.data.dao.DbTelegramUser;
import io.github.zeront4e.dstb.util.UniqueIdUtil;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class TokenProtectedTelegramBot {
    private static final String COMMAND_ACCESS_SET_TOKEN = "access_set_token";

    private final TelegramBot telegramBot;

    private final String userAccessToken;

    private final DbTelegramUsersManager dbTelegramUsersManager;

    private final Map<String, BotCommand> commandBotCommandMap = new HashMap<>();

    public TokenProtectedTelegramBot(String apiKey, String userAccessToken,
                                     DbTelegramUsersManager dbTelegramUsersManager) {
        telegramBot = new TelegramBot(apiKey);

        this.userAccessToken = userAccessToken;

        this.dbTelegramUsersManager = dbTelegramUsersManager;

        UpdatesListener updatesListener = updateList -> {
            for(Update update : updateList) {
                try {
                    processUpdate(update);
                }
                catch (Exception exception) {
                    log.error("Unable to process bot update.", exception);
                }
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        };

        ExceptionHandler exceptionHandler = telegramException -> {
            if (telegramException.response() != null) {
                log.error("Unexpected exception from Telegram. Error code: \"{}\" Description: \"{}\"",
                        telegramException.response().errorCode(), telegramException.response().description());
            }
            else {
                log.error("Unexpected exception while contacting Telegram.", telegramException);
            }
        };

        telegramBot.setUpdatesListener(updatesListener,exceptionHandler);
    }

    public BotCommand addSecuredBotCommand(BotCommand botCommand) {
        return commandBotCommandMap.put(botCommand.getCommandName(), botCommand);
    }

    public BotCommand removeSecuredBotCommand(BotCommand botCommand) {
        return commandBotCommandMap.remove(botCommand.getCommandName());
    }

    private void processUpdate(Update update) {
        DbTelegramUser dbTelegramUser = getHumanAuthorizedUserOrNull(update);

        if(dbTelegramUser == null)
            return;

        //Try to parse command.

        String messageText = update.message().text();

        if(messageText == null) {
            telegramBot.execute(new SendMessage(update.message().chat().id(), "I received an empty " +
                    "message-text... Please try again. " + Constants.Emojis.ILL));

            return;
        }

        if(!messageText.startsWith("/")) {
            telegramBot.execute(new SendMessage(update.message().chat().id(), "Please send me a command! " +
                    "Otherwise I won't understand you. " + Constants.Emojis.SWEAT));
        }
        else {
            //Parse command and related arguments.

            String[] commandParts = messageText.split(" ");

            String command;

            List<String> commandArguments;

            if(commandParts.length == 1) {
                command = messageText;

                commandArguments = Collections.emptyList();
            }
            else {
                command = commandParts[0];

                commandArguments = new ArrayList<>(commandParts.length - 1);

                commandArguments.addAll(Arrays.asList(commandParts).subList(1, commandParts.length));
            }

            command = command.substring(1); //Remove command indicator "/".

            //Execute command or respond with generic message.

            //Ignore the access-token-command.
            if(command.equals(COMMAND_ACCESS_SET_TOKEN)) {
                telegramBot.execute(new SendMessage(update.message().chat().id(), "You are already " +
                        "authorized. No need to perform another authentication. " + Constants.Emojis.SMILE));

                return;
            }

            BotCommand botCommand = commandBotCommandMap.get(command);

            if(botCommand == null) {
                telegramBot.execute(new SendMessage(update.message().chat().id(), "I'm sorry, but I don't know " +
                        "the given command. " + Constants.Emojis.THINKING + " Please send another one."));
            }
            else {
                try {
                    botCommand.onCommandRead(commandArguments, update, telegramBot);
                }
                catch (Exception exception) {
                    String errorId = UniqueIdUtil.createUniqueId();

                    log.error("Unable to execute bot-command \"{}\".", command);
                    log.error("Exception for error ID \"" + errorId + "\":", exception);

                    telegramBot.execute(new SendMessage(update.message().chat().id(), "I was unable to " +
                            "execute the command (error ID \"" + errorId + "\"). Please try again. " +
                            Constants.Emojis.ILL));
                }
            }
        }
    }

    private DbTelegramUser getHumanAuthorizedUserOrNull(Update update) {
        //Only private chats are allowed.

        if(update.message().chat().type() != Chat.Type.Private) {
            log.warn("Received message with unexpected chat type \"{}\". Only private chats are allowed!",
                    update.message().chat().type().name());

            telegramBot.execute(new SendMessage(update.message().chat().id(), "Hello! I only support private " +
                    "chats. Please contact me in a direct way. " + Constants.Emojis.SWEAT));

            return null;
        }

        //Only "humans" are allowed.

        User chatUser = update.message().from();

        if(chatUser.isBot()) {
            log.warn("Received message from bot. Only humans are allowed!");

            telegramBot.execute(new SendMessage(update.message().chat().id(), "Sorry. I only support chats " +
                    "with humans. " + Constants.Emojis.SAD));

            return null;
        }

        return getExistingAuthorizedUserOrNull(chatUser, update);
    }

    private DbTelegramUser getExistingAuthorizedUserOrNull(User chatUser, Update update) {
        //Check if the user already exists.

        DbTelegramUser existingDbTelegramUser;

        try {
            existingDbTelegramUser = dbTelegramUsersManager.getDbTelegramUserOrNull(chatUser.id());
        }
        catch (Exception exception) {
            log.error("Unable to query data for existing Telegram users.", exception);

            telegramBot.execute(new SendMessage(update.message().chat().id(), "I was unable to " +
                    "find out if I know you... Please try again. " + Constants.Emojis.ILL));

            return null;
        }

        boolean authorizationMismatch = existingDbTelegramUser == null || !existingDbTelegramUser.isAuthorized();

        if(!authorizationMismatch)
            return existingDbTelegramUser;

        //The user is unknown or unauthorized. Check if the user requests an authorization.

        String messageText = update.message().text();

        if(messageText.startsWith("/" + COMMAND_ACCESS_SET_TOKEN + " ")) {
            String[] messageParts = messageText.split(" ", -1);

            String accessToken = messageParts[1];

            boolean tokenIsValid = !userAccessToken.isBlank() && accessToken.equals(userAccessToken);

            if(tokenIsValid) {
                if(existingDbTelegramUser == null) {
                    createNewDbTelegramUserOrFail(chatUser, update);
                }
                else {
                    authorizeExistingDbTelegramUserOrFail(existingDbTelegramUser, chatUser, update);
                }
            }
            else {
                telegramBot.execute(new SendMessage(update.message().chat().id(), "Your sent token is " +
                        "invalid. Please try again. " + Constants.Emojis.SAD));
            }
        }
        else {
            //Received invalid command/message. Send welcome message.

            telegramBot.execute(new SendMessage(update.message().chat().id(), "It seems like we don't know " +
                    "each other yet. Please send me your access-token (command \"/" + COMMAND_ACCESS_SET_TOKEN +
                    " YOUR_TOKEN\"). " + Constants.Emojis.SMILE));
        }

        return null;
    }

    private void createNewDbTelegramUserOrFail(User chatUser, Update update) {
        DbTelegramUser newDbTelegramUser = new DbTelegramUser();

        newDbTelegramUser.setUserId(chatUser.id());
        newDbTelegramUser.setAuthorized(true);
        newDbTelegramUser.setAuthorizationTimestamp(System.currentTimeMillis());

        try {
            dbTelegramUsersManager.createTelegramUser(newDbTelegramUser);

            log.info("Created new Telegram user with user-ID \"{}\".",
                    newDbTelegramUser.getUserId());

            telegramBot.execute(new SendMessage(update.message().chat().id(), "Hello " +
                    chatUser.firstName() + "! " + Constants.Emojis.WAVING_HAND + " Now you are " +
                    "authorized! " + Constants.Emojis.HUG));
        }
        catch (Exception exception) {
            log.error("Unable to create new Telegram user.", exception);

            telegramBot.execute(new SendMessage(update.message().chat().id(), "I was unable to " +
                    "give you access... Please try again. " + Constants.Emojis.ILL));
        }
    }

    private void authorizeExistingDbTelegramUserOrFail(DbTelegramUser existingDbTelegramUser, User chatUser,
                                                       Update update) {
        existingDbTelegramUser.setAuthorized(true);
        existingDbTelegramUser.setAuthorizationTimestamp(System.currentTimeMillis());

        try {
            dbTelegramUsersManager.updateTelegramUser(existingDbTelegramUser);

            log.info("Authorized existing Telegram user with user-ID \"{}\".",
                    existingDbTelegramUser.getUserId());

            telegramBot.execute(new SendMessage(update.message().chat().id(), "Welcome back " +
                    chatUser.firstName() + "! "  + Constants.Emojis.WAVING_HAND + " Now you are " +
                    "authorized! " + Constants.Emojis.HUG));
        }
        catch (Exception exception) {
            log.error("Unable to update existing Telegram user.", exception);

            telegramBot.execute(new SendMessage(update.message().chat().id(), "I was unable to " +
                    "give you access... Please try again. " + Constants.Emojis.ILL));
        }
    }
}
