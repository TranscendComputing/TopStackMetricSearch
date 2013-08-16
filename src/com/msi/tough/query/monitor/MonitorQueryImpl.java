package com.msi.tough.query.monitor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.Action;

public class MonitorQueryImpl
{
    private final static Logger logger = Appctx
        .getLogger(MonitorQueryImpl.class.getName());

    private final Map<String, Action> actionMap;

    public MonitorQueryImpl(final Map<String, Action> actionMap)
    {
        this.actionMap = actionMap;
    }

    public void process(final HttpServletRequest req,
        final HttpServletResponse resp) throws Exception
    {
        final Action a = this.actionMap.get(req.getParameter("Action"));
        logger.debug("calling action " + a);
        a.process(req, resp);
    }
}
