package com.transcend.monitor.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.MarshallingUtils;
import com.msi.tough.monitor.common.MonitorConstants;
import com.transcend.monitor.message.MetricAlarmMessage.ComparisonOperator;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarmToPut;
import com.transcend.monitor.message.MetricAlarmMessage.Statistic;
import com.transcend.monitor.message.PutMetricAlarmMessage.PutMetricAlarmRequest;

public class PutMetricAlarmRequestUnmarshaller extends
    BaseMonitorUnmarshaller<PutMetricAlarmRequest> {

    private final static Logger logger = Appctx
            .getLogger(PutMetricAlarmRequestUnmarshaller.class.getName());

    private static PutMetricAlarmRequestUnmarshaller instance;

    public static PutMetricAlarmRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new PutMetricAlarmRequestUnmarshaller();
        }
        return instance;
    }

    public PutMetricAlarmRequest unmarshall(Map<String, String[]> in) {

        PutMetricAlarmRequest.Builder builder =
                PutMetricAlarmRequest.newBuilder();

        MetricAlarmToPut.Builder alarm = MetricAlarmToPut.newBuilder();
        alarm.setActionsEnabled(MarshallingUtils.unmarshallBoolean(in,
                MonitorConstants.NODE_ACTIONSENABLED,
                MonitorConstants.NOT_ENABLED, logger));
        alarm.setAlarmDescription(MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_ALARMDESCRIPTION,
                MonitorConstants.EMPTYSTRING, logger));
        alarm.setAlarmName(MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_ALARMNAME,
                logger));
        String operator = MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_COMPARISONOPERATOR,
                logger);
        alarm.setComparisonOperator(ComparisonOperator.valueOf(operator));
        alarm.setEvaluationPeriods(MarshallingUtils.unmarshallInteger(in,
                MonitorConstants.NODE_EVALUATIONPERIODS,
                logger));
        alarm.setMetricName(MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_METRICNAME,
                logger));
        alarm.setPeriod(MarshallingUtils.unmarshallInteger(in,
                MonitorConstants.NODE_PERIOD,
                logger));
        String statistic = MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_STATISTIC,
                logger);
        alarm.setStatistic(Statistic.valueOf(statistic));
        alarm.setThreshold(MarshallingUtils.unmarshallDouble(in,
                MonitorConstants.NODE_THRESHOLD,
                logger));
        alarm.setUnit(MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_UNIT,
                MonitorConstants.EMPTYSTRING, logger));
        alarm.addAllAlarmAction(parseResourceList("AlarmActions.member.", in));
        alarm.addAllOKAction(parseResourceList("OKActions.member.", in));
        alarm.addAllInsufficientDataAction(parseResourceList(
                "InsufficientDataActions.member.", in));

        alarm.addAllDimension(unmarshallDimensions(in));
        builder.setMetricAlarm(alarm);
        PutMetricAlarmRequest req = super.unmarshall(builder.buildPartial(), in);
        builder = req.toBuilder();
        builder.getMetricAlarmBuilder().setNamespace(req.getNamespace());
        return builder.buildPartial();
    }

    public String getValue(String key, Map<String, String[]> in,
            String defaultValue) {
        if (key == null)
            return defaultValue;
        String[] v = in.get(key);
        if (v == null || v.length < 1)
            return defaultValue;
        return v[0];
    }

    public List<String> parseResourceList(String lookupName,
            Map<String, String[]> in) {
        List<String> resourceList = new ArrayList<String>();
        int i = 0;
        while (true) {
            i++;
            final String names[] = in.get(lookupName + i);
            if (names == null)
                break;
            resourceList.add(names[0]);
        }
        return resourceList;
    }

}
