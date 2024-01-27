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

import io.github.zeront4e.dstb.Constants;
import io.github.zeront4e.dstb.charts.XYChartUtil;
import io.github.zeront4e.dstb.data.DbDailyMeasuringPointsManager;
import io.github.zeront4e.dstb.data.dao.DbDailyMeasuringPoint;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class DailyMeasurementDataChartsUtil {
    public static byte[] generateDaysKwhChartOrNull(DbDailyMeasuringPointsManager dbDailyMeasuringPointsManager,
                                                    LocalDateTime startLocalDateTime,
                                                    LocalDateTime stopLocalDateTime) throws SQLException, IOException {
        List<DbDailyMeasuringPoint> dbDailyMeasuringPoints = dbDailyMeasuringPointsManager
                .getDbDailyMeasuringPoints(Timestamp.valueOf(startLocalDateTime), Timestamp.valueOf(stopLocalDateTime));

        //Only generate chart, if data is available.
        if(dbDailyMeasuringPoints.isEmpty())
            return null;

        //Create measuring-points.

        List<Integer> pvXDaysList = new ArrayList<>();
        List<Double> pvYWattList = new ArrayList<>();

        List<Integer> gridXDaysList = new ArrayList<>();
        List<Double> gridYWattList = new ArrayList<>();

        List<Integer> batteryXDaysList = new ArrayList<>();
        List<Double> batteryYWattList = new ArrayList<>();

        List<Integer> houseXDaysList = new ArrayList<>();
        List<Double> houseYWattList = new ArrayList<>();

        for(DbDailyMeasuringPoint tmpDbDailyMeasuringPoint : dbDailyMeasuringPoints) {
            LocalDateTime localDateTime = Instant.ofEpochMilli(tmpDbDailyMeasuringPoint.getTimestamp())
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            //Add PV watt value.

            pvXDaysList.add(localDateTime.getDayOfMonth());
            pvYWattList.add(tmpDbDailyMeasuringPoint.getInverterTodayFromPvKilowattHours());

            //Add grid watt value.

            gridXDaysList.add(localDateTime.getDayOfMonth());
            gridYWattList.add(tmpDbDailyMeasuringPoint.getInverterTodayBoughtFromGridKilowattHours());

            //Add battery watt value.

            batteryXDaysList.add(localDateTime.getDayOfMonth());
            batteryYWattList.add(tmpDbDailyMeasuringPoint.getInverterTodayBatteryDischargeKilowattHours());

            //Add house watt value.

            houseXDaysList.add(localDateTime.getDayOfMonth());
            houseYWattList.add(tmpDbDailyMeasuringPoint.getInverterTodayToLoadKilowattHours());
        }

        //Create chart-data-series.

        XYChartUtil.DataSeries pvDataSeries = new XYChartUtil.DataSeries("PV panels generation", pvXDaysList,
                pvYWattList, ColorUtil.parseHexString(Constants.HexColors.GREEN));

        XYChartUtil.DataSeries gridDataSeries = new XYChartUtil.DataSeries("Grid consumption", gridXDaysList,
                gridYWattList, ColorUtil.parseHexString(Constants.HexColors.RED));

        XYChartUtil.DataSeries batteryDataSeries = new XYChartUtil.DataSeries("Battery usage",
                batteryXDaysList, batteryYWattList, ColorUtil.parseHexString(Constants.HexColors.ORANGE));

        XYChartUtil.DataSeries houseDataSeries = new XYChartUtil.DataSeries("House consumption",
                houseXDaysList, houseYWattList, ColorUtil.parseHexString(Constants.HexColors.BLUE));

        List<XYChartUtil.DataSeries> dataSeriesList = List.of(pvDataSeries, gridDataSeries, batteryDataSeries,
                houseDataSeries);

        //Render chart.

        return XYChartUtil.generateXYChartImage("kWh consumption (" + dbDailyMeasuringPoints.size() + " days from " +
                        startLocalDateTime.getDayOfMonth() + "." + startLocalDateTime.getMonthValue() + "." +
                        startLocalDateTime.getYear() + ")", "Day", "kWh", dataSeriesList, 6);
    }

    public static byte[] generateMonthsKwhChartOrNull(DbDailyMeasuringPointsManager dbDailyMeasuringPointsManager,
                                                    LocalDateTime startLocalDateTime,
                                                    LocalDateTime stopLocalDateTime) throws SQLException, IOException {
        List<DbDailyMeasuringPoint> dbDailyMeasuringPoints = dbDailyMeasuringPointsManager
                .getDbDailyMeasuringPoints(Timestamp.valueOf(startLocalDateTime), Timestamp.valueOf(stopLocalDateTime));

        //Only generate chart, if data is available.
        if(dbDailyMeasuringPoints.isEmpty())
            return null;

        //Create measuring-points.

        List<Integer> pvXMonthsList = new ArrayList<>();
        List<Double> pvYWattList = new ArrayList<>();

        List<Integer> gridXMonthsList = new ArrayList<>();
        List<Double> gridYWattList = new ArrayList<>();

        List<Integer> batteryXMonthsList = new ArrayList<>();
        List<Double> batteryYWattList = new ArrayList<>();

        List<Integer> houseXMonthsList = new ArrayList<>();
        List<Double> houseYWattList = new ArrayList<>();

        double monthPvWattSum = 0;

        double monthGridWattSum = 0;

        double monthBatteryWattSum = 0;

        double monthHouseWattSum = 0;

        int currentMonth = -1;

        for(DbDailyMeasuringPoint tmpDbDailyMeasuringPoint : dbDailyMeasuringPoints) {
            LocalDateTime localDateTime = Instant.ofEpochMilli(tmpDbDailyMeasuringPoint.getTimestamp())
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            if(currentMonth == -1) {
                currentMonth = localDateTime.getMonthValue();
            }
            else {
                if(currentMonth != localDateTime.getMonthValue()) {
                    //Add data for the completed month.

                    pvXMonthsList.add(currentMonth);
                    gridXMonthsList.add(currentMonth);
                    batteryXMonthsList.add(currentMonth);
                    houseXMonthsList.add(currentMonth);

                    pvYWattList.add(monthPvWattSum);
                    gridYWattList.add(monthGridWattSum);
                    batteryYWattList.add(monthBatteryWattSum);
                    houseYWattList.add(monthHouseWattSum);

                    currentMonth = localDateTime.getMonthValue();

                    monthPvWattSum = 0;
                    monthGridWattSum = 0;
                    monthBatteryWattSum = 0;
                    monthHouseWattSum = 0;
                }
            }

            //Add PV watt value.

            monthPvWattSum += tmpDbDailyMeasuringPoint.getInverterTodayFromPvKilowattHours();

            //Add grid watt value.

            monthGridWattSum += tmpDbDailyMeasuringPoint.getInverterTodayBoughtFromGridKilowattHours();

            //Add battery watt value.

            monthBatteryWattSum += tmpDbDailyMeasuringPoint.getInverterTodayBatteryDischargeKilowattHours();

            //Add house watt value.

            monthHouseWattSum += tmpDbDailyMeasuringPoint.getInverterTodayToLoadKilowattHours();
        }

        //Add data for the last completed month.

        pvXMonthsList.add(currentMonth);
        gridXMonthsList.add(currentMonth);
        batteryXMonthsList.add(currentMonth);
        houseXMonthsList.add(currentMonth);

        pvYWattList.add(monthPvWattSum);
        gridYWattList.add(monthGridWattSum);
        batteryYWattList.add(monthBatteryWattSum);
        houseYWattList.add(monthHouseWattSum);

        //Create chart-data-series.

        XYChartUtil.DataSeries pvDataSeries = new XYChartUtil.DataSeries("PV panels generation",
                pvXMonthsList, pvYWattList, ColorUtil.parseHexString(Constants.HexColors.GREEN));

        XYChartUtil.DataSeries gridDataSeries = new XYChartUtil.DataSeries("Grid consumption",
                gridXMonthsList, gridYWattList, ColorUtil.parseHexString(Constants.HexColors.RED));

        XYChartUtil.DataSeries batteryDataSeries = new XYChartUtil.DataSeries("Battery usage",
                batteryXMonthsList, batteryYWattList, ColorUtil.parseHexString(Constants.HexColors.ORANGE));

        XYChartUtil.DataSeries houseDataSeries = new XYChartUtil.DataSeries("House consumption",
                houseXMonthsList, houseYWattList, ColorUtil.parseHexString(Constants.HexColors.BLUE));

        List<XYChartUtil.DataSeries> dataSeriesList = List.of(pvDataSeries, gridDataSeries, batteryDataSeries,
                houseDataSeries);

        //Render chart.

        return XYChartUtil.generateXYChartImage("kWh consumption (" + dbDailyMeasuringPoints.size() + " days " +
                "from " + startLocalDateTime.getDayOfMonth() + "." + startLocalDateTime.getMonthValue() + "." +
                startLocalDateTime.getYear() + ")", "Month", "kWh", dataSeriesList, 10);
    }
}
