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

import java.util.Locale;

public class StringFormatUtil {
    public static String formatDouble(double doubleToFormat) {
        String formattedString = String.format(Locale.ENGLISH, "%.2f", doubleToFormat);

        char[] chars = formattedString.toCharArray();

        int lastCharIndex = -1;

        for(int tmpIndex = chars.length - 1; tmpIndex >= 0; tmpIndex--) {
            char tmpChar = chars[tmpIndex];

            if(tmpChar == '.') {
                lastCharIndex = tmpIndex - 1;
                break;
            }
            else if(tmpChar != '0') {
                lastCharIndex = tmpIndex;
                break;
            }
        }

        if(lastCharIndex != -1)
            return formattedString.substring(0, lastCharIndex + 1);

        return formattedString;
    }
}
