package com.msi.monitorquery.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;

import com.msi.tough.model.monitor.DimensionBean;
import com.msi.tough.query.ActionTestHelper;
import com.transcend.monitor.message.DeleteAlarmsMessage.DeleteAlarmsRequest;
import com.transcend.monitor.message.DescribeAlarmsMessage.DescribeAlarmsRequest;
import com.transcend.monitor.message.MetricAlarmMessage.ComparisonOperator;
import com.transcend.monitor.message.MetricAlarmMessage.Dimension;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarm;
import com.transcend.monitor.message.MetricAlarmMessage.MetricAlarmToPut;
import com.transcend.monitor.message.MetricAlarmMessage.Statistic;
import com.transcend.monitor.message.PutMetricAlarmMessage.PutMetricAlarmRequest;
import com.transcend.monitor.worker.DeleteAlarmsWorker;
import com.transcend.monitor.worker.PutMetricAlarmWorker;

/**
 * Alarm helper for non-web tests (using actions in-VM).
 *
 * @author jgardner
 *
 */
@Component
public class AlarmLocalHelper {

    public static final String DEFAULT_NAMESPACE = "AWS/EC2";
    public static final String DEFAULT_METRIC = "CPUUtilization";

    private static List<String> alarms = new ArrayList<String>();

    private static ActionTestHelper actionHelper = null;

    // TODO: use describe instead.
    private static String genericInstance = "instance123";

    private static PutMetricAlarmWorker putAlarmWorker = null;

    private static DeleteAlarmsWorker deleteAlarmsWorker = null;

    /**
     * Construct a minimal valid alarm request.
     *
     * @param alarmName
     * @return
     */
    public static PutMetricAlarmRequest.Builder metricAlarmRequest(String alarmName) {
        final PutMetricAlarmRequest.Builder request =
                PutMetricAlarmRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(actionHelper.getAccessKey());
        request.setRequestId(alarmName);
        request.setAction("PutMA");
        MetricAlarmToPut.Builder alarm = MetricAlarmToPut.newBuilder();
        alarm.setAlarmName(alarmName);
        alarm.setAlarmDescription(alarmName);
        alarm.setComparisonOperator(ComparisonOperator.GreaterThanOrEqualToThreshold);
        alarm.setEvaluationPeriods(2);
        alarm.setMetricName(DEFAULT_METRIC);
        alarm.setPeriod(60);
        alarm.setStatistic(Statistic.Average);
        alarm.setThreshold(0.1d);
        alarm.setNamespace(DEFAULT_NAMESPACE);
        request.setMetricAlarm(alarm.build());
        request.setNamespace(DEFAULT_NAMESPACE);
        return request;
    }

    /**
     * Put a metric alarm (don't care about the details, just need one.
     *
     * @param alarmName
     */
    public static void putMetricAlarm(String alarmName) throws Exception {
        PutMetricAlarmRequest.Builder putRequest = metricAlarmRequest(alarmName);
        MetricAlarmToPut.Builder alarm = MetricAlarmToPut.newBuilder();
        alarm.mergeFrom(putRequest.getMetricAlarm());
        Dimension.Builder dim = Dimension.newBuilder();
        dim.setName(DimensionBean.DIMENSION_INSTANCE_ID).
        setValue(genericInstance);
        alarm.addDimension(dim.build());
        putRequest.setMetricAlarm(alarm);

        putAlarmWorker.doWork(putRequest.build());
        alarms.add(alarmName);
    }

    /**
     * Construct a minimal valid describe alarm request.
     *
     * @param alarmName
     * @return
     */
    public static DescribeAlarmsRequest.Builder describeAlarmRequest(String alarmName) {
        final DescribeAlarmsRequest.Builder request =
                DescribeAlarmsRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(actionHelper.getAccessKey());
        request.setRequestId(alarmName);
        request.setAction("DescA");
        request.addAlarmName(alarmName);
        return request;
    }

    /**
     * Construct a minimal valid delete alarm request.
     *
     * @param alarmName
     * @return
     */
    public static DeleteAlarmsRequest.Builder deleteAlarmsRequest(String alarmName) {
        final DeleteAlarmsRequest.Builder request =
                DeleteAlarmsRequest.newBuilder();
        request.setTypeId(true);
        request.setCallerAccessKey(actionHelper.getAccessKey());
        request.setRequestId(alarmName);
        request.setAction("DelA");
        request.addAlarmName(alarmName);
        return request;
    }

    /**
     * Delete an alarm with the given name.
     *
     * @param alarmName
     * @param client
     */
    public static void deleteAlarm(String alarmName) throws Exception {
        final DeleteAlarmsRequest.Builder request = deleteAlarmsRequest(alarmName);
        deleteAlarmsWorker.doWork(request.build());
    }

    /**
     * Delete an alarm with the given name.
     *
     * @param alarmName
     * @param client
     */
    public static void deleteAllCreatedAlarms() throws Exception {
        DeleteAlarmsRequest.Builder request = null;
        for (String alarmName : alarms) {
            if (request == null) {
                request = deleteAlarmsRequest(alarmName);
            } else {
                request.addAlarmName(alarmName);
            }
        }
        deleteAlarmsWorker.doWork(request.build());
        alarms.clear();
    }

    /**
     * Get an alarm from the collection.
     *
     * @param alarms
     * @param name
     * @return matching alarm
     */
    public static MetricAlarm getAlarmByName(List<MetricAlarm> alarms,
            String name) {
        for (MetricAlarm alarm : alarms) {
            if (alarm.getAlarmName().equals(name)) {
                return alarm;
            }
        }
        return null;
    }

    @Autowired(required=true)
    public void setActionHelper(ActionTestHelper actionTestHelper) {
        AlarmLocalHelper.actionHelper = actionTestHelper;
    }

    @Resource
    public void setPutMetricAlarmWorker(PutMetricAlarmWorker putAlarmWorker) {
        AlarmLocalHelper.putAlarmWorker = putAlarmWorker;
    }

    @Resource
    public void setDeleteAlarmsWorker(DeleteAlarmsWorker deleteAlarmsWorker) {
        AlarmLocalHelper.deleteAlarmsWorker = deleteAlarmsWorker;
    }

    public static class AbstractActionRequest {
        final Map<String, String[]> parameterMap =
                new HashMap<String, String[]>();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        public void put(String key, String value) {
            parameterMap.put(key, new String[] {value});
        }

        public void putList(String key, String[] values) {
            for (int i = 0; i < values.length; i++) {
                put(key + ".member."+(i+1), values[i]);
            }
        }

        public void put(String key, Integer value) {
            this.put(key, value.toString());
        }

        public void put(String key, Double value) {
            this.put(key, value.toString());
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public HttpServletResponse getResponse() {
            return response;
        }

        public Map<String, String[]> getMap() {
            return parameterMap;
        }

        public void reset() {
            parameterMap.clear();
            request.clearAttributes();
            response = new MockHttpServletResponse();
        }
    }
}
