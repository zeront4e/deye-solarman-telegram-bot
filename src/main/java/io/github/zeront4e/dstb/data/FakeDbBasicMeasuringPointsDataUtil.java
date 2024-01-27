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

package io.github.zeront4e.dstb.data;

import io.github.zeront4e.dstb.data.dao.DbBasicMeasuringPoint;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class to generate (bad) dummy-data to use {@link DbBasicMeasuringPoint} instances.
 */
public class FakeDbBasicMeasuringPointsDataUtil {
    public static List<DbBasicMeasuringPoint> createFakeMeasuringPoints(int entriesPerHour, int year, int month,
                                                                        int startDay, int stopDay, int pvStartHour,
                                                                        int minWattage, int maxWattage) {
        List<DbBasicMeasuringPoint> dbBasicMeasuringPoints = new ArrayList<>();

        for(int day = startDay; day <= stopDay; day++) {
            for(int hour = 0; hour < 24; hour++) {
                for(int tmpEntryId = 1; tmpEntryId <= entriesPerHour; tmpEntryId++) {
                    int minute = (int) (60.0 / entriesPerHour) * tmpEntryId;

                    if(minute == 60)
                        minute--;

                    DbBasicMeasuringPoint dbBasicMeasuringPoint = new DbBasicMeasuringPoint();

                    LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);

                    dbBasicMeasuringPoint.setTimestamp(localDateTime.atZone(ZoneId.systemDefault())
                            .toInstant().toEpochMilli());

                    if(hour >= pvStartHour) {
                        double pvWatt = (createRandomWattValue(minWattage, maxWattage) + 1) / 2;

                        dbBasicMeasuringPoint.setPv1PowerWatt(pvWatt);
                        dbBasicMeasuringPoint.setPv2PowerWatt(pvWatt);
                    }

                    dbBasicMeasuringPoint.setBatteryOutPowerWatt(createRandomWattValue(minWattage, maxWattage));

                    dbBasicMeasuringPoint.setGridTotalPowerWatt(createRandomWattValue(minWattage, maxWattage));

                    dbBasicMeasuringPoint.setLoadTotalPowerWatt(createRandomWattValue(minWattage, maxWattage));

                    dbBasicMeasuringPoints.add(dbBasicMeasuringPoint);
                }
            }
        }

        return dbBasicMeasuringPoints;
    }

    private static double createRandomWattValue(int minWattage, int maxWattage) {
        return ThreadLocalRandom.current().nextDouble(minWattage, maxWattage);
    }
}
