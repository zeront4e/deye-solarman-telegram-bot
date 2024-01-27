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

@DatabaseTable(tableName = "basic-measuring-point")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DbBasicMeasuringPoint {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private long timestamp;

    @DatabaseField(canBeNull = false)
    private long deviceTimestamp;

    //Note that ALL following variables should have the SAME NAME as the variables in the corresponding POJO-class!

    //-----------------------------
    //Store PV related data.
    //-----------------------------

    @DatabaseField
    private double pv1PowerWatt;  //pv1_in_power in "Solar" tab in GUI

    @DatabaseField
    private double pv1Volt; //pv1_volt in "Solar" tab in GUI

    @DatabaseField
    private double pv1CurrentAmpere; //pv1_current in "Solar" tab in GUI

    @DatabaseField
    private double pv2PowerWatt; //pv2_in_power in "Solar" tab in GUI

    @DatabaseField
    private double pv2Volt; //pv2_volt in "Solar" tab in GUI

    @DatabaseField
    private double pv2CurrentAmpere; //pv2_current in "Solar" tab in GUI

    //-----------------------------
    //Store battery related data.
    //-----------------------------

    @DatabaseField
    private double batteryTemperatureCelsius; //battery_temperature in "Batt" tab in GUI

    @DatabaseField
    private double batterySocPercentage; //battery_soc in "Batt" tab in GUI

    @DatabaseField
    private double batteryOutPowerWatt; //battery_out_power in "Batt" tab in GUI

    @DatabaseField
    private double batteryVoltageVolt; //battery_voltage in "Batt" tab in GUI

    @DatabaseField
    private double batteryOutCurrentAmpere; //battery_out_current in "Batt" tab in GUI

    //-----------------------------
    //Store grid related data.
    //-----------------------------

    @DatabaseField
    private double gridTotalPowerWatt;

    //-----------------------------
    //Store load related data.
    //-----------------------------

    @DatabaseField
    private double loadPhaseAVolt; //load_phase_A_volt in "Solar" tab in GUI

    @DatabaseField
    private double loadPhaseBVolt; //load_phase_B_volt in "Solar" tab in GUI

    @DatabaseField
    private double loadPhaseCVolt; //load_phase_C_volt in "Solar" tab in GUI


    @DatabaseField
    private double loadPhaseACurrent; //load_phase_A_current in "Solar" tab in GUI

    @DatabaseField
    private double loadPhaseBCurrent; //load_phase_B_current in "Solar" tab in GUI

    @DatabaseField
    private double loadPhaseCCurrent; //load_phase_C_current in "Solar" tab in GUI


    @DatabaseField
    private double loadPhaseAPowerWatt; //load_phase_A_power in "Solar" tab in GUI

    @DatabaseField
    private double loadPhaseBPowerWatt; //load_phase_B_power in "Solar" tab in GUI

    @DatabaseField
    private double loadPhaseCPowerWatt; //load_phase_C_power in "Solar" tab in GUI


    @DatabaseField
    private double loadTotalPowerWatt; //load_total_power in "Load" tab in GUI
}
