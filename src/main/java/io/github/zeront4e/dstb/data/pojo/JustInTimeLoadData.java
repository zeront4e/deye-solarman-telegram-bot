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
 * Load related data of the "house".
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class JustInTimeLoadData {
    //Should be recorded.
    @SerializedName(value = "load_phase_A_volt")
    private double loadPhaseAVolt; //load_phase_A_volt in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "load_phase_B_volt")
    private double loadPhaseBVolt; //load_phase_B_volt in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "load_phase_C_volt")
    private double loadPhaseCVolt; //load_phase_C_volt in "Solar" tab in GUI


    //Should be recorded.
    @SerializedName(value = "load_phase_A_current")
    private double loadPhaseACurrent; //load_phase_A_current in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "load_phase_B_current")
    private double loadPhaseBCurrent; //load_phase_B_current in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "load_phase_C_current")
    private double loadPhaseCCurrent; //load_phase_C_current in "Solar" tab in GUI


    //Should be recorded.
    @SerializedName(value = "load_phase_A_power")
    private double loadPhaseAPowerWatt; //load_phase_A_power in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "load_phase_B_power")
    private double loadPhaseBPowerWatt; //load_phase_B_power in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "load_phase_C_power")
    private double loadPhaseCPowerWatt; //load_phase_C_power in "Solar" tab in GUI


    //Should be recorded.
    @SerializedName(value = "load_total_power")
    private double loadTotalPowerWatt; //load_total_power in "Load" tab in GUI
}