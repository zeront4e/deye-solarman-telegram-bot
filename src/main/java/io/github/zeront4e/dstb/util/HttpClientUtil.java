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

package io.github.zeront4e.dstb.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class HttpClientUtil {
    public static JsonObject getJsonFromUrl(String jsonDataUrl) throws IOException {
        String jsonDataString = getStringFromUrl(jsonDataUrl);

        return JsonParser.parseString(jsonDataString).getAsJsonObject();
    }

    public static String getStringFromUrl(String stringUrl) throws IOException {
        return readGetRequestData(URI.create(stringUrl).toURL());
    }

    private static String readGetRequestData(URL url) throws IOException {
        try(InputStream inputStream = url.openStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();

            int readChar;

            while((readChar = bufferedReader.read()) != -1) {
                stringBuilder.append((char) readChar);
            }

            return stringBuilder.toString();
        }
    }
}
