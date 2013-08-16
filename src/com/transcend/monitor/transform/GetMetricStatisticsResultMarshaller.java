package com.transcend.monitor.transform;

import java.util.List;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.query.QueryUtil;
import com.transcend.monitor.message.GetMetricStatisticsMessage.GetMetricStatisticsResponse;
import com.transcend.monitor.message.GetMetricStatisticsMessage.GetMetricStatisticsResponse.Datapoint;

public class GetMetricStatisticsResultMarshaller {

    public String marshall(final MarshallStruct<GetMetricStatisticsResponse> in) {
        final XMLNode nodeRoot = new XMLNode("GetMetricStatisticsResponse");
        nodeRoot.addAttr(MonitorConstants.ATTRIBUTE_XMLNS,
                MonitorConstants.NAMESPACE);

        final XMLNode nodeRes = new XMLNode("GetMetricStatisticsResult");
        nodeRoot.addNode(nodeRes);

        final List<Datapoint> data = in.getMainObject().getDataPointList();
        final XMLNode ndp = new XMLNode("Datapoints");
        nodeRes.addNode(ndp);
        for (final Datapoint dp : data) {
            final XMLNode member = new XMLNode("member");
            ndp.addNode(member);

            final XMLNode nts = new XMLNode("Timestamp");
            final XMLNode ts = new XMLNode();
            nts.addNode(ts);

            QueryUtil.addNode(member, "Timestamp", dp.getTimestamp());

            if (dp.getUnit() != null) {
                final XMLNode nunit = new XMLNode("Unit");

                final XMLNode unit = new XMLNode();
                nunit.addNode(unit);
                unit.setPlaintext("" + dp.getUnit());

                member.addNode(nunit);
            }

            if (dp.hasSampleCount()) {
                final XMLNode nsc = new XMLNode("SampleCount");

                final XMLNode sc = new XMLNode();
                nsc.addNode(sc);
                sc.setPlaintext("" + dp.getSampleCount());

                member.addNode(nsc);
            }
            if (dp.hasAverage()) {
                final XMLNode navg = new XMLNode("Average");

                final XMLNode avg = new XMLNode();
                navg.addNode(avg);
                avg.setPlaintext("" + dp.getAverage());

                member.addNode(navg);
            }
            if (dp.hasSum()) {
                final XMLNode nsum = new XMLNode("Sum");

                final XMLNode sum = new XMLNode();
                nsum.addNode(sum);
                sum.setPlaintext("" + dp.getSum());

                member.addNode(nsum);
            }
            if (dp.hasMinimum()) {
                final XMLNode nmin = new XMLNode("Minimum");

                final XMLNode min = new XMLNode();
                nmin.addNode(min);
                min.setPlaintext("" + dp.getMinimum());

                member.addNode(nmin);
            }
            if (dp.hasMaximum()) {
                final XMLNode nmax = new XMLNode("Maximum");

                final XMLNode max = new XMLNode();
                nmax.addNode(max);
                max.setPlaintext("" + dp.getMaximum());

                member.addNode(nmax);
            }
        }
        final XMLNode nlbl = new XMLNode("Label");
        final XMLNode lbl = new XMLNode();
        lbl.setPlaintext(in.getMainObject().getLabel());
        nlbl.addNode(lbl);
        nodeRes.addNode(nlbl);
        in.addResponseMetadata(nodeRoot, null);

        return nodeRoot.toString();
    }

}
