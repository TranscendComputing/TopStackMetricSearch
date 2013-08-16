package com.transcend.monitor.transform;

import java.util.Collection;
import java.util.Map;

import com.msi.tough.monitor.common.MarshallingUtils;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.ErrorResponse;
import com.transcend.monitor.message.DeleteAlarmsMessage.DeleteAlarmsRequest;

public class DeleteAlarmsRequestUnmarshaller extends
    BaseMonitorUnmarshaller<DeleteAlarmsRequest> {

    private static DeleteAlarmsRequestUnmarshaller instance;

    public static DeleteAlarmsRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new DeleteAlarmsRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public DeleteAlarmsRequest unmarshall(Map<String, String[]> in) {
        final DeleteAlarmsRequest.Builder req =
                DeleteAlarmsRequest.newBuilder();
        final String prefix = MonitorConstants.NODE_ALARMNAMES + "." +
                               MonitorConstants.NODE_MEMBER + ".";
        Collection<String> names = MarshallingUtils.unmarshallStrings(in, prefix);
        if (names.size() == 0) {
            throw ErrorResponse.missingParameter();
        }
        req.addAllAlarmName(names);
        return req.buildPartial();
    }
}
