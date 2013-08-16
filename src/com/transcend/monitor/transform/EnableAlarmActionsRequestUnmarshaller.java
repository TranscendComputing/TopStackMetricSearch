package com.transcend.monitor.transform;

import java.util.Map;

import com.msi.tough.monitor.common.MarshallingUtils;
import com.msi.tough.monitor.common.MonitorConstants;
import com.transcend.monitor.message.EnableAlarmActionsMessage.EnableAlarmActionsRequest;

public class EnableAlarmActionsRequestUnmarshaller
{
    private static EnableAlarmActionsRequestUnmarshaller instance;

    public static EnableAlarmActionsRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new EnableAlarmActionsRequestUnmarshaller();
        }
        return instance;
    }

    public EnableAlarmActionsRequest unmarshall(Map<String, String[]> in)
    {
        final EnableAlarmActionsRequest.Builder req =
                EnableAlarmActionsRequest.newBuilder();
        final String prefix =
            MonitorConstants.NODE_ALARMNAMES + "." + MonitorConstants.NODE_MEMBER + ".";
        req.addAllAlarmName(MarshallingUtils.unmarshallStrings(in, prefix));
        return req.buildPartial();
    }
}
