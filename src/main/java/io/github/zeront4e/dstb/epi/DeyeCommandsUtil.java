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

package io.github.zeront4e.dstb.epi;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.dgs_development.code.epi.CommandLineExecutor;
import eu.dgs_development.code.epi.handlers.text.TextProcessCallback;
import eu.dgs_development.code.epi.handlers.text.TextProcessHandler;
import io.github.zeront4e.dstb.AppConfig;
import lombok.extern.log4j.Log4j2;
import org.aeonbits.owner.ConfigFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class DeyeCommandsUtil {
    private static final AppConfig APP_CONFIG = ConfigFactory.create(AppConfig.class);

    public static CompletableFuture<JsonObject> extractCurrentDeyeJsonData() {
        CompletableFuture<JsonObject> completableFuture = new CompletableFuture<>();

        try {
            //Create files first.

            File tmpDeyeDataDirectory = new File(APP_CONFIG.deyeTmpDataDirectoryPath());

            if(!tmpDeyeDataDirectory.isDirectory() && !tmpDeyeDataDirectory.mkdirs())
                throw new Exception("Unable to create deye-data working directory. Path: " +
                        tmpDeyeDataDirectory.getAbsolutePath());

            String deyeJsonFileName = "data.json";

            File deyeJsonFile = new File(tmpDeyeDataDirectory.getAbsolutePath() + "/" + deyeJsonFileName);

            //Execute actual command.

            CommandLineExecutor.CommandLineType commandLineType;

            boolean osIsWindows = System.getProperty("os.name").toLowerCase().contains("win");

            if (osIsWindows) {
                commandLineType = CommandLineExecutor.CommandLineType.WINDOWS_CMD;
            }
            else {
                commandLineType = CommandLineExecutor.CommandLineType.UNIX_SH;
            }

            log.info("Set command line type to \"{}\".", commandLineType.name());

            List<String> arguments = List.of("--json", "--out", deyeJsonFileName, APP_CONFIG.deyeLoggerIp(),
                    APP_CONFIG.deyeLoggerSerialNumber());

            TextProcessHandler textProcessHandler = new TextProcessHandler() {
                @Override
                public void onInitialized(TextProcessCallback textProcessCallback) {
                    //Ignore...
                }

                @Override
                public void onStdLineRead(TextProcessCallback textProcessCallback, String readLine) {
                    log.info(readLine);
                }

                @Override
                public void onErrorLineRead(TextProcessCallback textProcessCallback, String readLine) {
                    log.error(readLine);
                }

                @Override
                public void onProcessExited(int exitCode) {
                    if(!deyeJsonFile.isFile()) {
                        completableFuture.completeExceptionally(new Exception("Unable to find Deye JSON file."));
                    }
                    else {
                        try {
                            JsonElement jsonElement = JsonParser.parseReader(new FileReader(deyeJsonFile));

                            if(!jsonElement.isJsonObject()) {
                                completableFuture.completeExceptionally(new Exception("Unable to parse created Deye " +
                                        "JSON file. Expected JSON object."));
                            }

                            Files.delete(deyeJsonFile.toPath());

                            completableFuture.complete(jsonElement.getAsJsonObject());
                        }
                        catch (Exception exception) {
                            completableFuture.completeExceptionally(exception);
                        }
                    }
                }

                @Override
                public void onIOException(IOException ioException) {
                    log.error("Unable to perform \"deye-read\".", ioException);
                }
            };

            CommandLineExecutor.executeCommand(commandLineType, tmpDeyeDataDirectory, "deye-read", arguments,
                    textProcessHandler);
        }
        catch (Exception exception) {
            completableFuture.completeExceptionally(exception);
        }

        return completableFuture;
    }
}
