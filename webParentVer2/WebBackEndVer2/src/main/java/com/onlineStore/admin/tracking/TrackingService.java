package com.onlineStore.admin.tracking;

import com.onlineStoreCom.entity.tracking.VisitorSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class TrackingService {

    @Autowired
    private VisitorSessionRepository sessionRepo;

    public VisitorSession registerSession(String sessionId, String ipAddress, String userAgent, String deviceType) {
        return sessionRepo.findBySessionId(sessionId).orElseGet(() -> {
            VisitorSession session = new VisitorSession();
            session.setSessionId(sessionId);
            session.setIpAddress(ipAddress);
            session.setUserAgent(userAgent);
            session.setDeviceType(deviceType);
            // tenantId handled by TenantEntityListener or Context usually, but safer to set if needed
            // session.setTenantId(TenantContext.getTenantId());
            return sessionRepo.save(session);
        });
    }

    public void updateHeartbeat(String sessionId) {
        Optional<VisitorSession> sessionOpt = sessionRepo.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            VisitorSession session = sessionOpt.get();
            session.setLastActiveTime(new Date());
            
            // Recalculate duration for display
            long durationSec = (session.getLastActiveTime().getTime() - session.getStartTime().getTime()) / 1000;
            long minutes = durationSec / 60;
            long seconds = durationSec % 60;
            session.setTotalDurationDisplay(String.format("%dm %ds", minutes, seconds));
            
            sessionRepo.save(session);
        }
    }

    public void recordPageView(String sessionId) {
        Optional<VisitorSession> sessionOpt = sessionRepo.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            VisitorSession session = sessionOpt.get();
            session.incrementPageViews();
            session.setLastActiveTime(new Date());
            sessionRepo.save(session);
        }
    }

    public void linkCustomer(String sessionId, Integer customerId) {
        sessionRepo.findBySessionId(sessionId).ifPresent(session -> {
            session.setCustomerId(customerId);
            sessionRepo.save(session);
        });
    }
}
