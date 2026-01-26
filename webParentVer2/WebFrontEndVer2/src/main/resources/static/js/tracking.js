/**
 * Visitor Tracking System
 * Handles session registration, heartbeats, and page view tracking.
 */
const TrackingSystem = {
    sessionId: null,
    heartbeatInterval: 30000, // 30 seconds
    serverUrl: '/tracking', // Relative URL (Assuming Backend and Frontend are on same domain or proxy)
                            // If separated, this needs full URL e.g. 'http://localhost:82/tracking'

    init: function() {
        this.sessionId = this.getCookie('visitor_session_id');
        if (!this.sessionId) {
            this.sessionId = this.generateUUID();
            this.setCookie('visitor_session_id', this.sessionId, 1); // 1 day
            this.registerSession();
        } else {
            console.log("Resuming session: " + this.sessionId);
            this.startHeartbeat();
        }
        
        // Track page view on load
        this.trackPageView();
    },

    generateUUID: function() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    },

    registerSession: function() {
        const payload = {
            sessionId: this.sessionId,
            userAgent: navigator.userAgent,
            device: this.getDeviceType(),
            // ip is handled by server
        };

        // Note: Cross-Origin Resource Sharing (CORS) might be needed if Backend is on different port
        // Assuming user runs them together or proxy
        $.post(this.serverUrl + '/register', payload)
            .done((response) => {
                console.log("Session registered", response);
                this.startHeartbeat();
            })
            .fail((xhr) => {
                console.error("Tracking registration failed", xhr);
            });
    },

    startHeartbeat: function() {
        setInterval(() => {
            $.post(this.serverUrl + '/heartbeat', { sessionId: this.sessionId });
        }, this.heartbeatInterval);
        console.log("Heartbeat started");
    },

    trackPageView: function() {
        $.post(this.serverUrl + '/pageview', { sessionId: this.sessionId });
    },

    getDeviceType: function() {
        const ua = navigator.userAgent;
        if (/(tablet|ipad|playbook|silk)|(android(?!.*mobi))/i.test(ua)) {
            return "tablet";
        }
        if (/Mobile|Android|iP(hone|od)|IEMobile|BlackBerry|Kindle|Silk-Accelerated|(hpw|web)OS|Opera M(obi|ini)/.test(ua)) {
            return "mobile";
        }
        return "desktop";
    },

    setCookie: function(name, value, days) {
        var expires = "";
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days*24*60*60*1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + (value || "")  + expires + "; path=/";
    },

    getCookie: function(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for(var i=0;i < ca.length;i++) {
            var c = ca[i];
            while (c.charAt(0)==' ') c = c.substring(1,c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
        }
        return null;
    }
};

$(document).ready(function() {
    TrackingSystem.init();
});
