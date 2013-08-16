package com.transcend.monitor.transform;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.msi.tough.core.DateHelper;
import com.transcend.monitor.message.MetricAlarmMessage.Unit;
import com.transcend.monitor.message.PutMetricDataMessage.MetricDatum;
import com.transcend.monitor.message.PutMetricDataMessage.PutMetricDataRequest;

public class PutMetricDataRequestUnmarshaller extends
    BaseMonitorUnmarshaller<PutMetricDataRequest> {

    private static PutMetricDataRequestUnmarshaller instance;
    private Calendar cal;

    public static PutMetricDataRequestUnmarshaller getInstance() {
        if (instance == null) {
            instance = new PutMetricDataRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public PutMetricDataRequest unmarshall(Map<String, String[]> in) {
        final PutMetricDataRequest.Builder req = PutMetricDataRequest.newBuilder();
        int i = 0;

        while (true) {
            MetricDatum.Builder mDatum = MetricDatum.newBuilder();
            i++;
            String metricName[] = in.get("MetricData.member." + i
                    + ".MetricName");
            String unit[] = in.get("MetricData.member." + i + ".Unit");
            String value[] = in.get("MetricData.member." + i + ".Value");
            String timestamp[] = in
                    .get("MetricData.member." + i + ".TimeStamp");
            if (metricName == null || unit == null || value == null) {
                break;
            }

            mDatum.setMetricName(metricName[0]);
            mDatum.setUnit(Unit.valueOf(unit[0]));
            mDatum.setValue(Double.parseDouble(value[0]));
            mDatum.addAllDimension(unmarshallDimensions(in));
            if (timestamp == null) {
                mDatum.setTimestamp(DateHelper.getISO8601Date(getUTCtime()));

            } else {
                mDatum.setTimestamp(timestamp[0]);
            }
            req.addMetricData(mDatum);
        }

        return unmarshall(req.buildPartial(), in);
    }

    private Date getUTCtime() {
        cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND,
                cal.getTimeZone().getOffset(cal.getTime().getTime()) * -1);
        return cal.getTime();
    }
}
