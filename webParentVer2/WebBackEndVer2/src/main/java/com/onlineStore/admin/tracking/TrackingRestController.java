package com.onlineStore.admin.tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/tracking")
public class TrackingRestController {

    @Autowired
    private TrackingService trackingService;

    @PostMapping("/heartbeat")
    public ResponseEntity<?> heartbeat(@RequestParam("sessionId") String sessionId) {
        trackingService.updateHeartbeat(sessionId);
        return ResponseEntity.ok(Collections.singletonMap("status", "ok"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerSession(
            @RequestParam("sessionId") String sessionId,
            @RequestParam(value = "ip", required = false) String ipAddress,
            @RequestParam(value = "userAgent", required = false) String userAgent,
            @RequestParam(value = "device", required = false) String deviceType) {
        
        trackingService.registerSession(sessionId, ipAddress, userAgent, deviceType);
        return ResponseEntity.ok(Collections.singletonMap("status", "registered"));
    }

    @PostMapping("/pageview")
    public ResponseEntity<?> pageview(@RequestParam("sessionId") String sessionId) {
        trackingService.recordPageView(sessionId);
        return ResponseEntity.ok(Collections.singletonMap("status", "recorded"));
    }
}
