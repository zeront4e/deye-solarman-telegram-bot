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
 * Panel related data.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class JustInTimePvData {
    //Should be recorded.
    @SerializedName(value = "pv1_volt")
    private double pv1VoltageVolt; //pv1_volt in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "pv1_current")
    private double pv1CurrentAmpere; //pv1_current in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "pv1_in_power")
    private double pv1InPowerWatt; //pv1_in_power in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "pv2_volt")
    private double pv2VoltageVolt; //pv2_volt in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "pv2_current")
    private double pv2CurrentAmpere; //pv2_current in "Solar" tab in GUI

    //Should be recorded.
    @SerializedName(value = "pv2_in_power")
    private double pv2InPowerWatt; //pv2_in_power in "Solar" tab in GUI
}