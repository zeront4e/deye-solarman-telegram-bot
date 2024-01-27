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
import io.github.zeront4e.dstb.data.DbBasicMeasuringPointsManager;
import io.github.zeront4e.dstb.data.dao.DbBasicMeasuringPoint;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicMeasurementDataChartsUtil {
    public static byte[] generateDayHoursWattChartOrNull(DbBasicMeasuringPointsManager dbBasicMeasuringPointsManager,
                                                         LocalDateTime localDateTime, int measurementsPerHourLimit) throws SQLException, IOException {
        return generateDayHoursWattChartOrNull(dbBasicMeasuringPointsManager, localDateTime, measurementsPerHourLimit,
                true, true, true, true);
    }

    public static byte[] generateDayHoursWattChartOrNull(DbBasicMeasuringPointsManager dbBasicMeasuringPointsManager,
                                                         LocalDateTime localDateTime, int measurementsPerHourLimit,
                                                         boolean includePv, boolean includeGrid, boolean includeBattery,
                                                         boolean includeHouse) throws SQLException, IOException {
        LocalDateTime startLocalDateTime = localDateTime.withHour(0).withMinute(0);
        LocalDateTime stopLocalDateTime = localDateTime.withHour(23).withMinute(59);

        List<DbBasicMeasuringPoint> dbBasicMeasuringPoints = dbBasicMeasuringPointsManager
                .getDbBasicMeasuringPoints(Timestamp.valueOf(startLocalDateTime), Timestamp.valueOf(stopLocalDateTime));

        //Only generate chart, if data is available.
        if(dbBasicMeasuringPoints.isEmpty())
            return null;

        //Create measuring-points.

        List<Double> pvXHoursList = new ArrayList<>();
        List<Double> pvYWattList = new ArrayList<>();

        List<Double> gridXHoursList = new ArrayList<>();
        List<Double> gridYWattList = new ArrayList<>();

        List<Double> batteryXHoursList = new ArrayList<>();
        List<Double> batteryYWattList = new ArrayList<>();

        List<Double> houseXHoursList = new ArrayList<>();
        List<Double> houseYWattList = new ArrayList<>();

        Map<Integer, List<DbBasicMeasuringPoint>> hourMeasuringPointsMap = new HashMap<>();

        for(DbBasicMeasuringPoint tmpDbBasicMeasuringPoint : dbBasicMeasuringPoints) {
            LocalDateTime tmpLocalDateTime = Instant.ofEpochMilli(tmpDbBasicMeasuringPoint.getTimestamp())
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            List<DbBasicMeasuringPoint> basicMeasuringPoints = hourMeasuringPointsMap
                    .computeIfAbsent(tmpLocalDateTime.getHour(), hour -> new ArrayList<>());

            basicMeasuringPoints.add(tmpDbBasicMeasuringPoint);
        }

        for(int tmpHour = 0; tmpHour < 24; tmpHour++) {
            List<DbBasicMeasuringPoint> hourDbBasicMeasuringPoints = hourMeasuringPointsMap.get(tmpHour);

            if(hourDbBasicMeasuringPoints != null && !hourDbBasicMeasuringPoints.isEmpty()) {
                if(hourDbBasicMeasuringPoints.size() > measurementsPerHourLimit) {
                    //Add only certain measuring-points.

                    double calculatedSteps = ((double) hourDbBasicMeasuringPoints.size()) / measurementsPerHourLimit;

                    int actualSteps;

                    if(calculatedSteps % 1 == 0) {
                        actualSteps = (int) calculatedSteps;
                    }
                    else {
                        actualSteps = ((int) calculatedSteps) + 1;
                    }

                    for(int tmpStepIndex = actualSteps; tmpStepIndex < hourDbBasicMeasuringPoints.size(); tmpStepIndex += actualSteps) {
                        DbBasicMeasuringPoint tmpDbBasicMeasuringPoint = hourDbBasicMeasuringPoints.get(tmpStepIndex);

                        LocalDateTime tmpLocalDateTime = Instant.ofEpochMilli(tmpDbBasicMeasuringPoint.getTimestamp())
                                .atZone(ZoneId.systemDefault()).toLocalDateTime();

                        double hours = tmpLocalDateTime.getHour() + (tmpLocalDateTime.getMinute() / 60.0);

                        //Add PV watt value.

                        if(includePv) {
                            pvXHoursList.add(hours);

                            pvYWattList.add(tmpDbBasicMeasuringPoint.getPv1PowerWatt() +
                                    tmpDbBasicMeasuringPoint.getPv2PowerWatt());
                        }

                        //Add grid watt value.

                        if(includeGrid) {
                            gridXHoursList.add(hours);

                            gridYWattList.add(tmpDbBasicMeasuringPoint.getGridTotalPowerWatt());
                        }

                        //Add battery watt value.

                        if(includeBattery) {
                            batteryXHoursList.add(hours);

                            batteryYWattList.add(tmpDbBasicMeasuringPoint.getBatteryOutPowerWatt());
                        }

                        //Add house watt value.

                        if(includeHouse) {
                            houseXHoursList.add(hours);

                            houseYWattList.add(tmpDbBasicMeasuringPoint.getLoadTotalPowerWatt());
                        }
                    }
                }
                else {
                    //Add all measuring-points.

                    for(DbBasicMeasuringPoint tmpDbBasicMeasuringPoint : hourDbBasicMeasuringPoints) {
                        LocalDateTime tmpLocalDateTime = Instant.ofEpochMilli(tmpDbBasicMeasuringPoint.getTimestamp())
                                .atZone(ZoneId.systemDefault()).toLocalDateTime();

                        double hours = tmpLocalDateTime.getHour() + (tmpLocalDateTime.getMinute() / 60.0);

                        //Add PV watt value.

                        if(includePv) {
                            pvXHoursList.add(hours);

                            pvYWattList.add(tmpDbBasicMeasuringPoint.getPv1PowerWatt() +
                                    tmpDbBasicMeasuringPoint.getPv2PowerWatt());
                        }

                        //Add grid watt value.

                        if(includeGrid) {
                            gridXHoursList.add(hours);

                            gridYWattList.add(tmpDbBasicMeasuringPoint.getGridTotalPowerWatt());
                        }

                        //Add battery watt value.

                        if(includeBattery) {
                            batteryXHoursList.add(hours);

                            batteryYWattList.add(tmpDbBasicMeasuringPoint.getBatteryOutPowerWatt());
                        }

                        //Add house watt value.

                        if(includeHouse) {
                            houseXHoursList.add(hours);

                            houseYWattList.add(tmpDbBasicMeasuringPoint.getLoadTotalPowerWatt());
                        }
                    }
                }
            }
        }

        //Create chart-data-series.

        List<XYChartUtil.DataSeries> dataSeriesList = new ArrayList<>();

        if(includePv) {
            XYChartUtil.DataSeries pvDataSeries = new XYChartUtil.DataSeries("PV Panels", pvXHoursList,
                    pvYWattList, ColorUtil.parseHexString(Constants.HexColors.GREEN));

            dataSeriesList.add(pvDataSeries);
        }

        if(includeGrid) {
            XYChartUtil.DataSeries gridDataSeries = new XYChartUtil.DataSeries("Grid", gridXHoursList,
                    gridYWattList, ColorUtil.parseHexString(Constants.HexColors.RED));

            dataSeriesList.add(gridDataSeries);
        }

        if(includeBattery) {
            XYChartUtil.DataSeries batteryDataSeries = new XYChartUtil.DataSeries("Battery", batteryXHoursList,
                    batteryYWattList, ColorUtil.parseHexString(Constants.HexColors.ORANGE));

            dataSeriesList.add(batteryDataSeries);
        }

        if(includeHouse) {
            XYChartUtil.DataSeries houseDataSeries = new XYChartUtil.DataSeries("House", houseXHoursList,
                    houseYWattList, ColorUtil.parseHexString(Constants.HexColors.BLUE));

            dataSeriesList.add(houseDataSeries);
        }

        //Render chart.

        return XYChartUtil.generateXYChartImage("Watt (data from " + localDateTime.getDayOfMonth() + "." +
                        localDateTime.getMonthValue() + "." + localDateTime.getYear() + ")", "Hour",
                "Watt", dataSeriesList, 0);
    }
}
