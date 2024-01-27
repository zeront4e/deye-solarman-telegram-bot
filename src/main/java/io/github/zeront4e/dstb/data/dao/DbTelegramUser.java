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

package io.github.zeront4e.dstb.data.dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@DatabaseTable(tableName = "telegram-user")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DbTelegramUser {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, unique = true)
    private long userId;

    @DatabaseField(canBeNull = false, defaultValue = "false")
    private boolean authorized;

    //Maybe necessary in the future.
    @DatabaseField(canBeNull = false, defaultValue = "false")
    private boolean admin;

    @DatabaseField
    private long authorizationTimestamp;
}
