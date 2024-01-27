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

package io.github.zeront4e.dstb.scheduler.jobs;

import com.google.gson.JsonObject;
import io.github.zeront4e.dstb.epi.DeyeCommandsUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class DeyeCommandExecutorJob implements Runnable {
    public interface JsonDataReadCallback {
        void onJsonDataRead(JsonObject jsonObject);
    }

    private final long contactIntervalMilliseconds;

    private final JsonDataReadCallback jsonDataReadCallback;

    private long lastContactTimestamp = 0;

    private final AtomicBoolean contactInProgress = new AtomicBoolean(false);

    @Getter
    private JsonObject lastReadJsonData = null;

    public DeyeCommandExecutorJob(long contactIntervalMilliseconds, JsonDataReadCallback jsonDataReadCallback) {
        this.contactIntervalMilliseconds = contactIntervalMilliseconds;
        this.jsonDataReadCallback = jsonDataReadCallback;
    }

    @Override
    public void run() {
        try {
            log.info("Data extraction job was called.");

            if (!contactInProgress.get()) {
                log.error("No data extraction in progress. Try to read data.");

                boolean contactIntervalReached = System.currentTimeMillis() - lastContactTimestamp >=
                        contactIntervalMilliseconds;

                if (contactIntervalReached) {
                    contactInProgress.set(true);

                    CompletableFuture<JsonObject> completableFuture = DeyeCommandsUtil.extractCurrentDeyeJsonData();

                    completableFuture.whenComplete((jsonObject, throwable) -> {
                        if (throwable == null) {
                            lastReadJsonData = jsonObject;

                            try {
                                jsonDataReadCallback.onJsonDataRead(jsonObject);
                            }
                            catch (Exception exception) {
                                log.error("Unable to process read JSON data.", exception);
                            }

                            lastContactTimestamp = System.currentTimeMillis();
                        }
                        else {
                            log.error("Unable to read data from Deye device. Cause: {}",
                                    throwable.getMessage());
                        }

                        contactInProgress.set(false);
                    });
                }
            }
            else {
                log.info("Data extraction in progress. Wait...");
            }
        }
        catch (Exception exception) {
            log.error("An unexpected exception occurred.", exception);
        }
    }
}
