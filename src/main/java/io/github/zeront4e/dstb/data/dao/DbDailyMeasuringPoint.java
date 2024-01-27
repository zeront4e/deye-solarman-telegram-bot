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

@DatabaseTable(tableName = "daily-measuring-point")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DbDailyMeasuringPoint {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, unique = true)
    private String dateString;

    @DatabaseField(canBeNull = false)
    private long timestamp;

    @DatabaseField(canBeNull = false)
    private long deviceTimestamp;

    @DatabaseField
    private double inverterTodayActivePowerKilowattHours; //active_power_today

    @DatabaseField
    private double inverterTodayGridConnectivityMinutes; //grid_connection_today

    @DatabaseField
    private double inverterTodayBatteryChargeKilowattHours; //battery_charge_today

    @DatabaseField
    private double inverterTodayBatteryDischargeKilowattHours; //battery_discharge_today

    @DatabaseField
    private double inverterTodayBoughtFromGridKilowattHours; //today_bought_from_grid

    @DatabaseField
    private double inverterTodaySoldToGridKilowattHours; //today_sold_to_grid

    @DatabaseField
    private double inverterTodayToLoadKilowattHours; //today_to_load

    @DatabaseField
    private double inverterTodayFromPvKilowattHours; //today_from_pv

    @DatabaseField
    private double inverterTodayFromPvS1KilowattHours; //today_from_pv_s1

    @DatabaseField
    private double inverterTodayFromPvS2KilowattHours; //today_from_pv_s2
}
