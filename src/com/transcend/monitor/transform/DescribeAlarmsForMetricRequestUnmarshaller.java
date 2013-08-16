/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2013
 * All Rights Reserved.
 */
package com.transcend.monitor.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.MonitorConstants;
import com.transcend.monitor.message.DescribeAlarmsForMetricMessage.DescribeAlarmsForMetricRequest;
import com.transcend.monitor.message.MetricAlarmMessage.Statistic;
import com.transcend.monitor.message.MetricAlarmMessage.Unit;

public class DescribeAlarmsForMetricRequestUnmarshaller
    extends BaseMonitorUnmarshaller<DescribeAlarmsForMetricRequest> {
    private final static Logger logger = Appctx
            .getLogger(DescribeAlarmsForMetricRequestUnmarshaller.class.getName());

    private static DescribeAlarmsForMetricRequestUnmarshaller instance;

    public static DescribeAlarmsForMetricRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new DescribeAlarmsForMetricRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public DescribeAlarmsForMetricRequest unmarshall(Map<String, String[]> mapIn) {
        final DescribeAlarmsForMetricRequest.Builder request =
                DescribeAlarmsForMetricRequest.newBuilder();
        //Required
        request.setMetricName(MarshallingUtils.unmarshallString(mapIn,
                "MetricName", logger));

        //Optional
        Integer period = MarshallingUtils.
                unmarshallInteger(mapIn, "Period", null, logger);
        if (period != null) {
            request.setPeriod(period);
        }
        String statistic = MarshallingUtils.unmarshallString(mapIn,
                MonitorConstants.NODE_STATISTIC, null,
                logger);
        if (statistic != null) {
            request.setStatistic(Statistic.valueOf(statistic));
        }
        String unit = MarshallingUtils.unmarshallString(mapIn,
                MonitorConstants.NODE_UNIT, null,
                logger);
        if (unit != null) {
            request.setUnit(Unit.valueOf(unit));
        }
        request.addAllDimension(MarshallingUtils.unmarshallDimensions(mapIn));

        return super.unmarshall(request.buildPartial(), mapIn);
    }
}
