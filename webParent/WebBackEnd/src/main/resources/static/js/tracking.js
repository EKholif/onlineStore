/**
 * Enterprise Visitor Tracking Agent
 * Sends heartbeats and activity data to the backend.
 */
(function() {
    const TRACKING_API_BASE = "/onlineStoreAdmin/tracking"; // Adjust context path if needed
    let sessionId = getCookie("visitor_session_id");

    function generateUUID() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

    function setCookie(name, value, days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + (value || "") + expires + "; path=/";
    }

    function getCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    // Initialize Session
    if (!sessionId) {
        sessionId = generateUUID();
        setCookie("visitor_session_id", sessionId, 1); // 1 Day expiration
        
        // Register new session
        fetch(`${TRACKING_API_BASE}/register?sessionId=${sessionId}&deviceType=${detectDevice()}`, { method: 'POST' });
    }

    function detectDevice() {
        const ua = navigator.userAgent;
        if (/Mobile|Android|iP(hone|od)|IEMobile|BlackBerry|Kindle|Silk-Accelerated/.test(ua)) {
            return "Mobile";
        } else if (/(Tablet|iPad|Playbook)/.test(ua)) {
            return "Tablet";
        }
        return "Desktop";
    }

    // Heartbeat (every 30 seconds)
    setInterval(function() {
        if (document.visibilityState === 'visible') {
            fetch(`${TRACKING_API_BASE}/heartbeat?sessionId=${sessionId}`, { method: 'POST' });
        }
    }, 30000);

    // Initial Page View
    fetch(`${TRACKING_API_BASE}/pageview?sessionId=${sessionId}`, { method: 'POST' });

})();
