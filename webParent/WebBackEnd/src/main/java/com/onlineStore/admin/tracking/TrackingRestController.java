package com.onlineStore.admin.tracking;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking")
public class TrackingRestController {

    @Autowired
    private TrackingService trackingService;

    @PostMapping("/heartbeat")
    public void heartbeat(@RequestParam("sessionId") String sessionId) {
        trackingService.updateHeartbeat(sessionId);
    }

    @PostMapping("/pageview")
    public void pageView(@RequestParam("sessionId") String sessionId) {
        trackingService.recordPageView(sessionId);
    }

    @PostMapping("/register")
    public String registerSession(@RequestParam("sessionId") String sessionId,
            @RequestParam(value = "deviceType", defaultValue = "Desktop") String deviceType,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String agent = request.getHeader("User-Agent");
        trackingService.registerSession(sessionId, ip, agent, deviceType);
        return "OK";
    }
}
