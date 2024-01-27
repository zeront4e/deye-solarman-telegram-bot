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

package io.github.zeront4e.dstb.data.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Inverter related data (including statistics about today and over the total runtime).
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class JustInTimeInverterData {
    @SerializedName(value = "inverter_time")
    private String inverterTimeString;

    @SerializedName(value = "active_power_today")
    private double inverterTodayActivePowerKilowattHours;

    @SerializedName(value = "grid_connection_today")
    private double inverterTodayGridConnectivityMinutes;


    @SerializedName(value = "battery_charge_today")
    private double inverterTodayBatteryChargeKilowattHours;

    @SerializedName(value = "battery_discharge_today")
    private double inverterTodayBatteryDischargeKilowattHours;


    @SerializedName(value = "today_bought_from_grid")
    private double inverterTodayBoughtFromGridKilowattHours;

    @SerializedName(value = "today_sold_to_grid")
    private double inverterTodaySoldToGridKilowattHours;


    @SerializedName(value = "today_to_load")
    private double inverterTodayToLoadKilowattHours; //today_to_load in "Load" tab in GUI


    @SerializedName(value = "battery_charge_total")
    private double inverterTotalBatteryChargeKilowattHours;

    @SerializedName(value = "battery_discharge_total")
    private double inverterTotalBatteryDischargeKilowattHours;


    @SerializedName(value = "total_bought_from_grid")
    private double inverterTotalBoughtFromGridKilowattHours;

    @SerializedName(value = "total_sold_to_grid")
    private double inverterTotalSoldToGridKilowattHours;

    @SerializedName(value = "total_to_load")
    private double inverterTotalToLoadKilowattHours; //total_to_load in "Load" tab in GUI


    @SerializedName(value = "today_from_pv")
    private double inverterSolarTodayFromPvKilowattHours; //today_from_pv in "Solar" tab in GUI

    @SerializedName(value = "today_from_pv_s1")
    private double inverterSolarTodayFromPvS1KilowattHours;

    @SerializedName(value = "today_from_pv_s2")
    private double inverterSolarTodayFromPvS2KilowattHours;

    @SerializedName(value = "total_from_pv")
    private double inverterSolarTotalFromPvKilowattHours; //total_from_pv in "Solar" tab in GUI
}