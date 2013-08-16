package com.transcend.monitor.transform;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.monitor.common.MonitorConstants;
import com.msi.tough.query.MarshallStruct;
import com.transcend.monitor.message.ListMetricsMessage.ListMetricsResponse;

/*

 Example Output:
 <ListMetricsResponse xmlns="http://monitoring.amazonaws.com/doc/2009-05-15/">
 <ListMetricsResult>
 <span style="color: #ff0033">
 <Datapoints>
 <Timestamp>...</Timestamp>
 <Samples>...</Samples>
 <Average>...</Average>
 <Sum>...</Sum>
 <Minimum>...</Minimum>
 <Maximum>...</Maximum>
 <Unit>...</Unit>
 <CustomUnit>...</CustomUnit>
 </Datapoints>
 </span>
 <Label>CPUUtilization</Label>
 </ListMetricsResult>
 <ResponseMetadata>
 <RequestId>ba1dec97-d79b-11df-98d7-7fa07cb4eac1</RequestId>
 </ResponseMetadata>
 </ListMetricsResponse>

 */

public class ListMetricsResultMarshaller {

    public String marshall(MarshallStruct<ListMetricsResponse> in) {
        final XMLNode nodeRoot = new XMLNode(
                MonitorConstants.NODE_LISTMETRICSRESULT);
        nodeRoot.addAttr(MonitorConstants.ATTRIBUTE_XMLNS,
                MonitorConstants.NAMESPACE);

        MarshallingUtils.marshallMetricList(nodeRoot,
                MonitorConstants.NODE_METRICS, in.getMainObject().getMetricList());
        MarshallingUtils.marshallString(nodeRoot,
                MonitorConstants.NODE_NEXTTOKEN, in.getMainObject()
                        .getNextToken());

        // Add the metadata response portion
        in.addResponseMetadata(nodeRoot, null);

        return nodeRoot.toString();
    }
}