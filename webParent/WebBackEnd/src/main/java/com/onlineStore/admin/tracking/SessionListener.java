package com.onlineStore.admin.tracking;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionListener implements HttpSessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);

    @Autowired
    private ActivityTracker activityTracker;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activityTracker.visitorConnected();
        LOGGER.debug("New Visitor Session: " + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activityTracker.visitorDisconnected();
        LOGGER.debug("Visitor Session Destroyed: " + se.getSession().getId());
    }
}
