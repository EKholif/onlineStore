/**
 * AG-TENANT-RESOLVER
 * Critical Security Component
 *
 * Logic:
 * 1. Parses window.location.hostname
 * 2. Extracts strict subdomain as Tenant Key
 * 3. BLOCKS execution if no subdomain is present (e.g. localhost, IP)
 * 4. BLOCKS execution if 'www' is used (Reserved for landing)
 */
(function () {
    console.log("AG-TENANT-RESOLVER: Initializing...");

    const hostname = window.location.hostname;
    const parts = hostname.split('.');

    // Guard 1: Direct IP Access
    // Regex checks for basic IPv4 format
    const isIp = /^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$/.test(hostname);
    if (isIp) {
        blockApp("Security Alert", "Direct IP access is not permitted. Please use your store address.");
        throw new Error("AG-SECURITY: Direct IP access blocked");
    }

    // Guard 2: Missing Subdomain (localhost case)
    // If only one part (e.g. 'localhost'), it's invalid
    if (parts.length === 1 && hostname.toLowerCase() !== 'localhost') {
        // This might cover intranet too, but strict mode requires subdomain
        // Actually, if it is just 'localhost', parts.length is 1.
    }

    if (hostname.toLowerCase() === 'localhost') {
        blockApp("Configuration Error", "Missing Tenant Subdomain.<br>Please use format: <code>tenant.localhost</code>");
        throw new Error("AG-SECURITY: Missing tenant subdomain");
    }

    // Guard 3: 'www' is reserved
    const candidateKey = parts[0];
    if (candidateKey.toLowerCase() === 'www') {
        blockApp("Invalid Store", "The 'www' address is reserved. Please use your specific store subdomain.");
        throw new Error("AG-SECURITY: 'www' subdomain blocked");
    }

    // Success
    window.TENANT_KEY = candidateKey;
    console.log("AG-TENANT-RESOLVER: Resolved Tenant Key:", window.TENANT_KEY);

    // Helper to kill the UI
    function blockApp(title, message) {
        document.body.innerHTML = `
            <div style="
                position: fixed; top: 0; left: 0; width: 100%; height: 100%;
                background: linear-gradient(135deg, #1a1a1a 0%, #000000 100%);
                color: #fff; display: flex; flex-direction: column;
                align-items: center; justify-content: center; z-index: 99999;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
            ">
                <h1 style="font-size: 2.5rem; margin-bottom: 20px; color: #ff6b6b;">${title}</h1>
                <p style="font-size: 1.2rem; text-align: center; line-height: 1.6;">${message}</p>
                <div style="margin-top: 40px; padding: 10px 20px; background: #333; border-radius: 5px; font-family: monospace;">
                    Current Host: ${hostname}
                </div>
            </div>
        `;
        // Stop all other script execution if possible (throw error does this for current stack)
    }

})();
