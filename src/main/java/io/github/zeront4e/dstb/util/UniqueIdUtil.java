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

import java.util.concurrent.atomic.AtomicLong;

public class UniqueIdUtil {
    private static final AtomicLong ID_SUFFIX = new AtomicLong(0);

    public static String createUniqueId() {
        return System.currentTimeMillis() + "" + ID_SUFFIX.getAndIncrement();
    }
}
