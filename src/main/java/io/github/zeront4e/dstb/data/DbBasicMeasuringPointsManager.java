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

import com.j256.ormlite.dao.Dao;
import io.github.zeront4e.dstb.data.dao.DbBasicMeasuringPoint;
import io.github.zeront4e.dstb.util.InverterTimestampUtil;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Log4j2
public class DbBasicMeasuringPointsManager {
    private final Dao<DbBasicMeasuringPoint, Long> dbBasicMeasuringPointDao;

    public DbBasicMeasuringPointsManager(Dao<DbBasicMeasuringPoint, Long> dbBasicMeasuringPointDao) {
        this.dbBasicMeasuringPointDao = dbBasicMeasuringPointDao;
    }

    public List<DbBasicMeasuringPoint> getDbBasicMeasuringPoints(Date startDate, Date endDate) throws SQLException {
        return dbBasicMeasuringPointDao.queryBuilder()
                .where()
                .between("timestamp", startDate.getTime(), endDate.getTime())
                .query();
    }

    public List<DbBasicMeasuringPoint> getDbBasicMeasuringPoints() throws SQLException {
        return dbBasicMeasuringPointDao.queryForAll();
    }

    public void tryToStoreBasicMeasuringPoint(DeyeDataContainer deyeDataContainer) {
        //Check if the daily measurement should be stored.

        try {
            log.info("Try to store basic measuring point.");

            DbBasicMeasuringPoint dbBasicMeasuringPoint = new DbBasicMeasuringPoint();

            //Set time values.

            dbBasicMeasuringPoint.setTimestamp(System.currentTimeMillis());

            String inverterTimeString = deyeDataContainer.getJustInTimeInverterData()
                    .getInverterTimeString();

            long inverterTimestamp = InverterTimestampUtil.parseStringToMilliseconds(inverterTimeString);

            dbBasicMeasuringPoint.setDeviceTimestamp(inverterTimestamp);

            //Set PV related data.

            dbBasicMeasuringPoint.setPv1PowerWatt(deyeDataContainer.getJustInTimePvData().getPv1InPowerWatt());

            dbBasicMeasuringPoint.setPv1Volt(deyeDataContainer.getJustInTimePvData().getPv1VoltageVolt());

            dbBasicMeasuringPoint.setPv1CurrentAmpere(deyeDataContainer.getJustInTimePvData().getPv1CurrentAmpere());

            dbBasicMeasuringPoint.setPv2PowerWatt(deyeDataContainer.getJustInTimePvData().getPv2InPowerWatt());

            dbBasicMeasuringPoint.setPv2Volt(deyeDataContainer.getJustInTimePvData().getPv2VoltageVolt());

            dbBasicMeasuringPoint.setPv2CurrentAmpere(deyeDataContainer.getJustInTimePvData().getPv2CurrentAmpere());

            //Set battery related data.

            dbBasicMeasuringPoint.setBatteryTemperatureCelsius(deyeDataContainer.getJustInTimeBatteryData()
                    .getBatteryTemperatureCelsius());

            dbBasicMeasuringPoint.setBatterySocPercentage(deyeDataContainer.getJustInTimeBatteryData()
                    .getBatterySocPercentage());

            dbBasicMeasuringPoint.setBatteryOutPowerWatt(deyeDataContainer.getJustInTimeBatteryData()
                    .getBatteryOutPowerWatt());

            dbBasicMeasuringPoint.setBatteryVoltageVolt(deyeDataContainer.getJustInTimeBatteryData()
                    .getBatteryVoltageVolt());

            dbBasicMeasuringPoint.setBatteryOutCurrentAmpere(deyeDataContainer.getJustInTimeBatteryData()
                    .getBatteryOutCurrentAmpere());

            //Set grid related data.

            dbBasicMeasuringPoint.setGridTotalPowerWatt(deyeDataContainer.getJustInTimeGridData()
                    .getGridTotalPowerWatt());

            //Set load related data.

            dbBasicMeasuringPoint.setLoadPhaseAVolt(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadPhaseAVolt());

            dbBasicMeasuringPoint.setLoadPhaseBVolt(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadPhaseBVolt());

            dbBasicMeasuringPoint.setLoadPhaseCVolt(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadPhaseCVolt());


            dbBasicMeasuringPoint.setLoadPhaseACurrent(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadPhaseACurrent());

            dbBasicMeasuringPoint.setLoadPhaseBCurrent(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadPhaseBCurrent());

            dbBasicMeasuringPoint.setLoadPhaseCCurrent(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadPhaseCCurrent());


            dbBasicMeasuringPoint.setLoadPhaseAPowerWatt(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadPhaseAPowerWatt());

            dbBasicMeasuringPoint.setLoadPhaseBPowerWatt(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadPhaseBPowerWatt());

            dbBasicMeasuringPoint.setLoadPhaseCPowerWatt(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadPhaseCPowerWatt());


            dbBasicMeasuringPoint.setLoadTotalPowerWatt(deyeDataContainer.getJustInTimeLoadData()
                    .getLoadTotalPowerWatt());

            //Store measuring point.

            dbBasicMeasuringPointDao.create(dbBasicMeasuringPoint);

            log.info("Stored basic measuring point.");
        }
        catch (Exception exception) {
            log.error("Unable to add basic measuring point.", exception);
        }
    }
}