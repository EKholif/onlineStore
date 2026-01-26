/**
 * Theme Bootstrap Script
 * Fetches tenant-specific theme configuration from the public API
 * and applies it as global CSS variables.
 *
 * Ensures no FOUC (Flash of Unstyled Content) by blocking paint if possible,
 * or applying defaults immediately.
 */
(function () {
    const THEME_API_URL = '/api/theme';
    const STORE_KEY = 'tenant_theme_config';

    function applyTheme(theme) {
        if (!theme) return;
        const root = document.documentElement;

        // Colors
        if (theme.primaryColor) root.style.setProperty('--theme-color-primary', theme.primaryColor);
        if (theme.secondaryColor) root.style.setProperty('--theme-color-secondary', theme.secondaryColor);

        // Header
        if (theme.headerBg) root.style.setProperty('--theme-header-bg', theme.headerBg);
        if (theme.headerColor) root.style.setProperty('--theme-header-color', theme.headerColor);

        // Footer
        if (theme.footerBg) root.style.setProperty('--theme-footer-bg', theme.footerBg);
        if (theme.footerColor) root.style.setProperty('--theme-footer-color', theme.footerColor);

        // Typography
        if (theme.fontFamily) root.style.setProperty('--theme-font-family', theme.fontFamily);
        if (theme.fontSize) root.style.setProperty('--theme-font-size', theme.fontSize);
        if (theme.fontWeight) root.style.setProperty('--theme-font-weight', theme.fontWeight);

        // Dimensions (if present in DTO)
        if (theme.headerHeight) root.style.setProperty('--theme-header-height', theme.headerHeight);

        console.log('[Theme] Applied:', theme);
    }

    async function loadTheme() {
        try {
            const response = await fetch(THEME_API_URL);
            if (!response.ok) throw new Error('Failed to fetch theme');
            const theme = await response.json();

            // Apply
            applyTheme(theme);

            // Cache for subsequent pages (optional optim)
            sessionStorage.setItem(STORE_KEY, JSON.stringify(theme));

        } catch (e) {
            console.warn('[Theme] Failed to load, falling back to defaults.', e);
            // Defaults are already in CSS, so no action needed.
        }
    }

    // Attempt to load from cache first for speed
    const cached = sessionStorage.getItem(STORE_KEY);
    if (cached) {
        applyTheme(JSON.parse(cached));
    }

    // Always refresh from server to ensuring data is fresh
    loadTheme();

})();
