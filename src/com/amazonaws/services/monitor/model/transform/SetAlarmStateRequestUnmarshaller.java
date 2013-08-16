package com.amazonaws.services.monitor.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.services.cloudwatch.model.SetAlarmStateRequest;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.MarshallingUtils;

public class SetAlarmStateRequestUnmarshaller implements
Unmarshaller<SetAlarmStateRequest, Map<String, String[]>>{
	
    private final static Logger logger = Appctx
            .getLogger(SetAlarmStateRequest.class.getName());

    private static SetAlarmStateRequestUnmarshaller instance;

    public static SetAlarmStateRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new SetAlarmStateRequestUnmarshaller();
        }
        return instance;
    }
    
	@Override
	public SetAlarmStateRequest unmarshall(Map<String, String[]> in)
			throws Exception {
	    
		final SetAlarmStateRequest req = new SetAlarmStateRequest();
		req.setAlarmName(MarshallingUtils.unmarshallString(in, "AlarmName", null, logger));
		req.setStateReason(MarshallingUtils.unmarshallString(in, "StateReason", "", logger));
		req.setStateReasonData(MarshallingUtils.unmarshallString(in, "StateReasonData", null, logger));
		req.setStateValue(MarshallingUtils.unmarshallString(in, "StateValue", null, logger));
		
		return req;
	}

}
