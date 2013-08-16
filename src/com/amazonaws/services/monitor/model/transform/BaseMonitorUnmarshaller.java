/**
 * Transcend Computing, Inc.
 * Confidential and Proprietary
 * Copyright (c) Transcend Computing, Inc. 2012
 * All Rights Reserved.
 */
package com.amazonaws.services.monitor.model.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.monitor.common.MarshallingUtils;
import com.msi.tough.monitor.common.MonitorConstants;

/**
 * @author jgardner
 *
 */
public abstract class BaseMonitorUnmarshaller<T> implements
        Unmarshaller<T, Map<String, String[]>> {

    private final static Logger logger = Appctx
            .getLogger(BaseMonitorUnmarshaller.class.getName());

    public void unmarshall(T instance, Map<String, String[]> in)
            throws Exception {
        String namespace = MarshallingUtils.unmarshallString(in,
                MonitorConstants.NODE_NAMESPACE,
                logger);
        if ("AWS/EC2".equals(namespace)) {
            // Since we are AWS compatible, accept AWS namespace, sub our own.
            namespace = "MSI/EC2";
        }
        PropertyUtils.setProperty(instance, "namespace", namespace);
    }

    /**
     * @param mapIn
     * @return
     */
    Collection<Dimension> unmarshallDimensions(
        Map<String, String[]> in)
    {
        List<Dimension> dims = new ArrayList<Dimension>();
        int i = 0;
        while (true)
        {
            i++;
            String prefix = "Dimensions.member." + i + ".";
            final String n[] = in.get(prefix + MonitorConstants.NODE_NAME);
            if (n == null)
            {
                break;
            }
            final String v[] = in.get(prefix + MonitorConstants.NODE_VALUE);
            Dimension d = new Dimension();
            d.setName(n[0]);
            d.setValue((v != null ? v[0] : ""));
            dims.add(d);
        }
        return dims;
    }
}
