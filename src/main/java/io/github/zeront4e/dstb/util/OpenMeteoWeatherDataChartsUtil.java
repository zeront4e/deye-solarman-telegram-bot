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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.zeront4e.dstb.Constants;
import io.github.zeront4e.dstb.charts.XYChartUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OpenMeteoWeatherDataChartsUtil {
    public static byte[] generateWeatherChart(LocalDateTime localDateTime,
                                              JsonObject weatherDataJsonObject) throws IOException {
        JsonObject hourlyJsonObject = weatherDataJsonObject.getAsJsonObject("hourly");

        //Values from 0 to 100.
        JsonArray cloudCoverJsonArray = hourlyJsonObject.getAsJsonArray("cloud_cover");

        //Values from 0 to 100.
        JsonArray precipitationProbabilityJsonArray = hourlyJsonObject
                .getAsJsonArray("precipitation_probability");

        List<Integer> xHoursList = new ArrayList<>(24);

        List<Double> cloudCoverPercentageList = new ArrayList<>(24);

        List<Double> precipitationProbabilityPercentageList = new ArrayList<>(24);

        for(int tmpHourIndex = 0; tmpHourIndex < 24; tmpHourIndex++) {
            double cloudCoverPercentage = cloudCoverJsonArray.get(tmpHourIndex).getAsDouble();

            double precipitationProbabilityPercentage = precipitationProbabilityJsonArray.get(tmpHourIndex)
                    .getAsDouble();

            xHoursList.add(tmpHourIndex);

            cloudCoverPercentageList.add(cloudCoverPercentage);

            precipitationProbabilityPercentageList.add(precipitationProbabilityPercentage);
        }

        //Render chart.

        List<XYChartUtil.DataSeries> dataSeriesList = new ArrayList<>();

        XYChartUtil.DataSeries cloudCoverDataSeries = new XYChartUtil.DataSeries("Cloud cover percentage",
                xHoursList, cloudCoverPercentageList, ColorUtil.parseHexString(Constants.HexColors.RED));

        dataSeriesList.add(cloudCoverDataSeries);

        XYChartUtil.DataSeries precipitationDataSeries = new XYChartUtil.DataSeries("Precipitation percentage",
                xHoursList, precipitationProbabilityPercentageList, ColorUtil.parseHexString(Constants.HexColors.BLUE));

        dataSeriesList.add(precipitationDataSeries);

        return XYChartUtil.generateXYChartImage("Weather data (from " + localDateTime.getDayOfMonth() + "." +
                        localDateTime.getMonthValue() + "." + localDateTime.getYear() + ")", "Hour",
                "Percentage", dataSeriesList, 6);
    }
}
