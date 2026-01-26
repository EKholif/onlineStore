/**
 * AG-HTTP-INTERCEPTOR
 * Critical Connectivity Component
 *
 * Logic:
 * 1. Checks if window.TENANT_KEY is resolved (by tenant_resolver.js)
 * 2. Intercepts jQuery $.ajax requests and adds X-Tenant-Key header
 * 3. Intercepts native fetch() requests and adds X-Tenant-Key header
 */
(function () {
    // Wait for validation
    if (!window.TENANT_KEY) {
        console.warn("AG-HTTP-INTERCEPTOR: Tenant Key not found. Resolver might have failed or blocked execution.");
        return;
    }

    const HEADER_NAME = "X-Tenant-Key";
    const TENANT_VALUE = window.TENANT_KEY;

    // 1. jQuery Interceptor (if jQuery is loaded)
    if (window.jQuery) {
        $.ajaxSetup({
            beforeSend: function (xhr) {
                xhr.setRequestHeader(HEADER_NAME, TENANT_VALUE);
            }
        });
        console.log("AG-HTTP-INTERCEPTOR: jQuery AJAX interceptor registered.");
    }

    // 2. Native Fetch Interceptor
    const originalFetch = window.fetch;
    if (originalFetch) {
        window.fetch = function (url, options) {
            options = options || {};
            options.headers = options.headers || {};

            // Handle Headers object vs plain object
            if (options.headers instanceof Headers) {
                options.headers.append(HEADER_NAME, TENANT_VALUE);
            } else if (Array.isArray(options.headers)) {
                // Handle array of arrays format [['Key', 'Value']]
                options.headers.push([HEADER_NAME, TENANT_VALUE]);
            } else {
                // Plain object
                options.headers[HEADER_NAME] = TENANT_VALUE;
            }

            return originalFetch(url, options);
        };
        console.log("AG-HTTP-INTERCEPTOR: Native Fetch interceptor registered.");
    }

})();
