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

package io.github.zeront4e.dstb.charts;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class XYChartUtil {
    public record DataSeries(String legendText, List<? extends Number> xData, List<? extends Number> yData, Color color) {
        @Override
        public String toString() {
            return "DataSeries{" +
                    "legendText='" + legendText + '\'' +
                    ", xData=" + xData +
                    ", yData=" + yData +
                    ", color=" + color +
                    '}';
        }
    }

    public static byte[] generateXYChartImage(String title, String xAxisTitle, String yAxisTitle,
                                              List<DataSeries> dataSeries, int markerSize) throws IOException {
        XYChart xyChart = new XYChartBuilder()
                .theme(Styler.ChartTheme.GGPlot2)
                .title(title)
                .xAxisTitle(xAxisTitle)
                .yAxisTitle(yAxisTitle)
                .width(1280)
                .height(720)
                .build();

        xyChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        xyChart.getStyler().setMarkerSize(markerSize);

        for(DataSeries tmpDataSeries : dataSeries) {
            if(!tmpDataSeries.xData().isEmpty() && !tmpDataSeries.yData().isEmpty()) {
                XYSeries xySeries = xyChart.addSeries(tmpDataSeries.legendText(), tmpDataSeries.xData(),
                        tmpDataSeries.yData());

                if(tmpDataSeries.color() != null) {
                    xySeries.setLineColor(tmpDataSeries.color()).setFillColor(tmpDataSeries.color());
                    xySeries.setMarkerColor(tmpDataSeries.color()).setFillColor(tmpDataSeries.color());
                }
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        BufferedImage bufferedImage = BitmapEncoder.getBufferedImage(xyChart);

        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}
