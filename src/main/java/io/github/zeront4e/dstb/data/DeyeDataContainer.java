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

package io.github.zeront4e.dstb.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.zeront4e.dstb.data.pojo.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Getter
@Log4j2
public class DeyeDataContainer {
      private static final Gson GSON = new Gson();

      private final JustInTimeInverterData justInTimeInverterData;

      private final JustInTimeBatteryData justInTimeBatteryData;

      private final JustInTimeGridData justInTimeGridData;

      private final JustInTimeLoadData justInTimeLoadData;

      private final JustInTimePvData justInTimePvData;

      public DeyeDataContainer(JsonObject deyeJsonObject) {
            JsonObject unifiedDeyeJsonObject = createUnifiedDataJsonObject(deyeJsonObject);

            justInTimeInverterData = GSON.fromJson(unifiedDeyeJsonObject, JustInTimeInverterData.class);

            justInTimeBatteryData = GSON.fromJson(unifiedDeyeJsonObject, JustInTimeBatteryData.class);

            justInTimeGridData = GSON.fromJson(unifiedDeyeJsonObject, JustInTimeGridData.class);

            justInTimeLoadData = GSON.fromJson(unifiedDeyeJsonObject, JustInTimeLoadData.class);

            justInTimePvData = GSON.fromJson(unifiedDeyeJsonObject, JustInTimePvData.class);
      }

      private JsonObject createUnifiedDataJsonObject(JsonObject deyeJsonObject) {
            JsonArray dataJsonArray = deyeJsonObject.getAsJsonArray("data");

            JsonObject targetJsonObject = new JsonObject();

            Map<String, JsonElement> targetPropertyJsonElementMap = targetJsonObject.asMap();

            dataJsonArray.forEach(tmpJsonElement -> {
                  if(tmpJsonElement.isJsonObject()) {
                        JsonObject tmpJsonObject = tmpJsonElement.getAsJsonObject();

                        tmpJsonObject.asMap().forEach((tmpObjectKey, tmpObjectJsonElement) -> {
                              if(!tmpObjectJsonElement.isJsonObject()) {
                                    log.warn("Expected JSON object for property \"{}\".", tmpObjectKey);
                              }
                              else {
                                    JsonObject tmpObjectJsonObject = tmpObjectJsonElement.getAsJsonObject();

                                    JsonElement valueJsonElement = tmpObjectJsonObject.get("value");

                                    JsonElement previousJsonElement = targetPropertyJsonElementMap.put(tmpObjectKey,
                                            valueJsonElement);

                                    if(previousJsonElement != null)
                                          log.warn("The existing property \"{}\" was overwritten with a new " +
                                                          "property!", tmpObjectKey);
                              }
                        });
                  }
            });

            return targetJsonObject;
      }

}
