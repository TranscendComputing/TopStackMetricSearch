/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.transcend.monitor.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.MonitorConstants;
import com.transcend.monitor.message.DescribeAlarmHistoryMessage.DescribeAlarmHistoryRequest;

public class DescribeAlarmHistoryRequestUnmarshaller extends
    BaseMonitorUnmarshaller<DescribeAlarmHistoryRequest>
{
    private final static Logger logger = Appctx
        .getLogger(DescribeAlarmHistoryRequestUnmarshaller.class.getName());

    private static DescribeAlarmHistoryRequestUnmarshaller instance;

    public static DescribeAlarmHistoryRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new DescribeAlarmHistoryRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public DescribeAlarmHistoryRequest unmarshall(Map<String, String[]> mapIn)
    {
        final DescribeAlarmHistoryRequest.Builder request =
                DescribeAlarmHistoryRequest.newBuilder();
        request.setAlarmName(MarshallingUtils.unmarshallString(mapIn,
                MonitorConstants.NODE_ALARMNAME, "", logger));

        if (mapIn.get("EndDate") != null) {
            request.setEndDate(mapIn.get("EndDate")[0]);
        }

        request.setHistoryItemType(MarshallingUtils.unmarshallString(mapIn,
                MonitorConstants.NODE_ALARMHISTORYITEMTYPE, "", logger));
        request.setMaxRecords(Integer.parseInt(MarshallingUtils
                .unmarshallString(mapIn, MonitorConstants.NODE_MAXRECORDS,
                        MonitorConstants.DEFAULTMAXRECORDS, logger)));
        request.setNextToken(MarshallingUtils.unmarshallString(mapIn,
                MonitorConstants.NODE_NEXTTOKEN, MonitorConstants.EMPTYSTRING, logger));

        if (mapIn.get("StartDate") != null) {
            request.setEndDate(mapIn.get("StartDate")[0]);
        }

        return request.buildPartial();
    }
}
