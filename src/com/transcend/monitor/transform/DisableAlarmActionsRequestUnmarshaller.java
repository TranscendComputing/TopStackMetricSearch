package com.transcend.monitor.transform;

import java.util.Map;

import com.msi.tough.monitor.common.MonitorConstants;
import com.transcend.monitor.message.DisableAlarmActionsMessage.DisableAlarmActionsRequest;

public class DisableAlarmActionsRequestUnmarshaller
{
    private static DisableAlarmActionsRequestUnmarshaller instance;

    public static DisableAlarmActionsRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new DisableAlarmActionsRequestUnmarshaller();
        }
        return instance;
    }

    public DisableAlarmActionsRequest unmarshall(Map<String, String[]> in)
    {
        final DisableAlarmActionsRequest.Builder req =
                DisableAlarmActionsRequest.newBuilder();
        final String prefix =
            MonitorConstants.NODE_ALARMNAMES + "." + MonitorConstants.NODE_MEMBER + ".";
        req.addAllAlarmName(MarshallingUtils.unmarshallStrings(in, prefix));
        return req.buildPartial();
    }
}
