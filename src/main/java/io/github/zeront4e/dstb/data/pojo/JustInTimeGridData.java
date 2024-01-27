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
 * Grid related data.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class JustInTimeGridData {
    @SerializedName(value = "grid_charge_start_voltage")
    private double gridChargeStartVolt;

    @SerializedName(value = "grid_charge_start_soc")
    private double gridChargeStartSocPercentage;

    @SerializedName(value = "grid_charge_current")
    private double gridChargeCurrentAmpere;

    @SerializedName(value = "grid_max_output_pwr")
    private double gridMaxOutputPowerWatt;

    @SerializedName(value = "grid_freq_selection")
    private double gridFrequencySelectionHertz;

    @SerializedName(value = "grid_high_voltage")
    private double gridHighVoltageVolt;

    @SerializedName(value = "grid_low_voltage")
    private double gridLowVoltageVolt;

    @SerializedName(value = "grid_high_frequency")
    private double gridHighFrequencyHertz;

    @SerializedName(value = "grid_low_frequency")
    private double gridLowFrequencyHertz;

    //Should be recorded.
    @SerializedName(value = "grid_total_power")
    private double gridTotalPowerWatt;
}
