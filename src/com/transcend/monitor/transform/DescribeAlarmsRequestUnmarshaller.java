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
import com.msi.tough.monitor.common.MarshallingUtils;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.QueryFaults;
import com.mysql.jdbc.StringUtils;
import com.transcend.monitor.message.DescribeAlarmsMessage.DescribeAlarmsRequest;
import com.transcend.monitor.message.MetricAlarmMessage.AlarmState;

public class DescribeAlarmsRequestUnmarshaller extends
    BaseMonitorUnmarshaller<DescribeAlarmsRequest> {

    private final static Logger logger = Appctx
        .getLogger(DescribeAlarmsRequestUnmarshaller.class.getName());

    private static DescribeAlarmsRequestUnmarshaller instance;

    public static DescribeAlarmsRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new DescribeAlarmsRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public DescribeAlarmsRequest unmarshall(Map<String, String[]> mapIn)
    {
        final DescribeAlarmsRequest.Builder request = DescribeAlarmsRequest.newBuilder();

        request.setActionPrefix(MarshallingUtils.unmarshallString(mapIn,
            MonitorConstants.NODE_ACTIONPREFIX, MonitorConstants.EMPTYSTRING, logger));
        request.setAlarmNamePrefix(MarshallingUtils.unmarshallString(mapIn,
            MonitorConstants.NODE_ALARMNAMEPREFIX, MonitorConstants.EMPTYSTRING, logger));
        final String prefix =
                MonitorConstants.NODE_ALARMNAMES + "." + MonitorConstants.NODE_MEMBER + ".";
        request.addAllAlarmName(MarshallingUtils.unmarshallStrings(mapIn, prefix));
        request.setMaxRecords(Integer.parseInt(MarshallingUtils
            .unmarshallString(mapIn, MonitorConstants.NODE_MAXRECORDS,
                MonitorConstants.DEFAULTMAXRECORDS, logger)));
        request.setNextToken(MarshallingUtils.unmarshallString(mapIn,
            MonitorConstants.NODE_NEXTTOKEN, MonitorConstants.EMPTYSTRING, logger));
        String state = MarshallingUtils.unmarshallString(mapIn,
                MonitorConstants.NODE_STATEVALUE,
                MonitorConstants.EMPTYSTRING, logger);
        if (!StringUtils.isNullOrEmpty(state)) {
            try {
                request.setStateValue(AlarmState.valueOf(state));
            } catch (Exception e) {
                throw QueryFaults.InvalidParameterValue();
            }
        }
        return request.buildPartial();
    }
}
