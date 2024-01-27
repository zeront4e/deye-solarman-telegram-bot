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

import io.github.zeront4e.dstb.data.dao.DbDailyMeasuringPoint;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class to generate (bad) dummy-data to use {@link DbDailyMeasuringPoint} instances.
 */
public class FakeDbDailyMeasuringPointsDataUtil {
    public static List<DbDailyMeasuringPoint> createFakeMeasuringPoints(int year, int month,
                                                                        int startDay, int stopDay,
                                                                        int minWattage, int maxWattage) {
        List<DbDailyMeasuringPoint> dbDailyMeasuringPoints = new ArrayList<>();

        for(int day = startDay; day <= stopDay; day++) {
            LocalDateTime localDateTime = LocalDateTime.of(year, month, day, 23, 59);

            DbDailyMeasuringPoint dbDailyMeasuringPoint = new DbDailyMeasuringPoint();

            dbDailyMeasuringPoint.setDateString(day + "." + month + "." + year);

            dbDailyMeasuringPoint.setTimestamp(localDateTime.atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli());

            dbDailyMeasuringPoint.setDeviceTimestamp(localDateTime.atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli());

            dbDailyMeasuringPoint.setInverterTodayFromPvKilowattHours(createRandomKwhValue(minWattage, maxWattage));

            dbDailyMeasuringPoint.setInverterTodayBoughtFromGridKilowattHours(createRandomKwhValue(minWattage,
                    maxWattage));

            dbDailyMeasuringPoint.setInverterTodayBatteryDischargeKilowattHours(createRandomKwhValue(minWattage,
                    maxWattage));

            dbDailyMeasuringPoint.setInverterTodayToLoadKilowattHours(createRandomKwhValue(minWattage, maxWattage));

            dbDailyMeasuringPoints.add(dbDailyMeasuringPoint);
        }

        return dbDailyMeasuringPoints;
    }

    private static double createRandomKwhValue(int minWattage, int maxWattage) {
        double randomWatt = createRandomWattValue(minWattage, maxWattage);

        return randomWatt * 24 / 1000;
    }

    private static double createRandomWattValue(int minWattage, int maxWattage) {
        return ThreadLocalRandom.current().nextDouble(minWattage, maxWattage);
    }
}
