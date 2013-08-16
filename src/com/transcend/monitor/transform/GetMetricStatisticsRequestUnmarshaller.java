package com.transcend.monitor.transform;

import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.transcend.monitor.message.GetMetricStatisticsMessage.GetMetricStatisticsRequest;
import com.transcend.monitor.message.MetricAlarmMessage.Statistic;
import com.transcend.monitor.message.MetricAlarmMessage.Unit;

public class GetMetricStatisticsRequestUnmarshaller extends BaseMonitorUnmarshaller<GetMetricStatisticsRequest>
{
    public static final int MAX_DATAPOINTS = 100;

    private final static Logger logger = Appctx
        .getLogger(GetMetricStatisticsRequestUnmarshaller.class.getName());

    private static GetMetricStatisticsRequestUnmarshaller instance;

    public static GetMetricStatisticsRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new GetMetricStatisticsRequestUnmarshaller();
        }
        return instance;
    }

    @Override
    public GetMetricStatisticsRequest unmarshall(Map<String, String[]> in)
    {
        final GetMetricStatisticsRequest.Builder req =
                GetMetricStatisticsRequest.newBuilder();

        req.setPeriod(MarshallingUtils.unmarshallInteger(in,
                MonitorConstants.NODE_PERIOD,
                logger));
        req.setStartTime(MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_STARTTIME,
                logger));
        req.setEndTime(MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_ENDTIME,
                logger));
        req.setMetricName(MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_METRICNAME,
                logger));
        String unit = MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_UNIT, null,
                logger);
        req.setUnit(unit == null? Unit.None : Unit.valueOf(unit));
        int i = 0;

        while (true)
        {
            i++;
            final String n[] = in.get("Statistics.member." + i);
            if (n == null)
            {
                break;
            }
            try {
                req.addStatistic(Statistic.valueOf(n[0]));
            } catch (Exception e) {
                throw QueryFaults.InvalidParameterValue();
            }
        }
        if (req.getStatisticCount() == 0) {
            throw ErrorResponse.missingParameter();
        }

        req.addAllDimension(unmarshallDimensions(in));

        Date start = DateHelper.getCalendarFromISO8601String(req.getStartTime(),
                TimeZone.getTimeZone("GMT")).getTime();
        Date end = DateHelper.getCalendarFromISO8601String(req.getEndTime(),
                TimeZone.getTimeZone("GMT")).getTime();
        if (!start.before(end)) {
            throw QueryFaults.InvalidParameterValue();
        }
        if (req.getPeriod() < 60 || req.getPeriod() % 60 != 0) {
            throw QueryFaults.InvalidParameterValue();
        }
        long timeDelta = end.getTime() -
                start.getTime();
        long numPoints = timeDelta / req.getPeriod() / 1000 / 60;
        if (numPoints > MAX_DATAPOINTS) {
            throw QueryFaults.InvalidParameterCombination("You have requested" +
                    " up to "+numPoints+" datapoints, which exceeds the " +
                    "limit of "+MAX_DATAPOINTS+".");
        }
        return super.unmarshall(req.buildPartial(), in);
    }

}
