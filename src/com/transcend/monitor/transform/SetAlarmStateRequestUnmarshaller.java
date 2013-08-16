package com.transcend.monitor.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.MarshallingUtils;
import com.transcend.monitor.message.MetricAlarmMessage.AlarmState;
import com.transcend.monitor.message.SetAlarmStateMessage.SetAlarmStateRequest;

public class SetAlarmStateRequestUnmarshaller {

    private final static Logger logger = Appctx
            .getLogger(SetAlarmStateRequestUnmarshaller.class.getName());

    private static SetAlarmStateRequestUnmarshaller instance;

    public static SetAlarmStateRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new SetAlarmStateRequestUnmarshaller();
        }
        return instance;
    }

    public SetAlarmStateRequest unmarshall(Map<String, String[]> in) {

        final SetAlarmStateRequest.Builder req =
                SetAlarmStateRequest.newBuilder();
        req.setAlarmName(MarshallingUtils.unmarshallString(in, "AlarmName",
                null, logger));
        req.setStateReason(MarshallingUtils.unmarshallString(in, "StateReason",
                "", logger));
        req.setStateReasonData(MarshallingUtils.unmarshallString(in,
                "StateReasonData", null, logger));
        req.setStateValue(AlarmState.valueOf(
                MarshallingUtils.unmarshallString(in, "StateValue",
                null, logger)));

        return req.buildPartial();
    }

}
