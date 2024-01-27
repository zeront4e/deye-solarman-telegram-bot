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
 * Battery related data.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class JustInTimeBatteryData {
    @SerializedName(value = "batt_capacity")
    private double batteryCapacityAmpereHours;

    @SerializedName(value = "max_charge_amps")
    private double batteryMaxChargeAmpere;

    @SerializedName(value = "max_discharge_amps")
    private double batteryMaxDischargeAmpere;

    @SerializedName(value = "battery_charging_eff")
    private double batteryChargeEfficiencyPercentage;

    @SerializedName(value = "battery_resistance")
    private double batteryResistanceMilliOhm;

    @SerializedName(value = "battery_shutdown_capacity")
    private double batteryShutdownCapacityPercentage;

    @SerializedName(value = "battery_recovery_capacity")
    private double batteryRecoveryCapacityPercentage;

    @SerializedName(value = "battery_low_capacity")
    private double batteryLowCapacityPercentage;

    @SerializedName(value = "battery_shutdown_voltage")
    private double batteryShutdownVoltageVolt;

    @SerializedName(value = "battery_restart_voltage")
    private double batteryRestartVoltageVolt;

    @SerializedName(value = "battery_low_voltage")
    private double batteryLowVoltageVolt;

    //Should be recorded.
    @SerializedName(value = "battery_temperature")
    private double batteryTemperatureCelsius; //battery_temperature in "Batt" tab in GUI

    //Should be recorded.
    @SerializedName(value = "battery_voltage")
    private double batteryVoltageVolt; //battery_voltage in "Batt" tab in GUI

    //Should be recorded.
    @SerializedName(value = "battery_soc")
    private double batterySocPercentage; //battery_soc in "Batt" tab in GUI

    @SerializedName(value = "battery_out_power")
    private double batteryOutPowerWatt; //battery_out_power in "Batt" tab in GUI

    @SerializedName(value = "battery_out_current")
    private double batteryOutCurrentAmpere; //battery_out_current in "Batt" tab in GUI

    @SerializedName(value = "battery_corrected_ah")
    private double batteryCorrectedAmpereHours;
}