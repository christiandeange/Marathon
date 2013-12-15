package com.deange.marathonapp.utils;

import android.view.View;

public final class Utils {

    public static int calculateWindowFlags() {

        int windowFlags = 0;

        if (PlatformUtils.hasKitKat()) {
            windowFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            windowFlags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            windowFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            windowFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            windowFlags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            windowFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            windowFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        return windowFlags;
    }

    public static void sleepQuietly(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException ignored) {
            // Nothing to do here
        }
    }

    private Utils() {
        // Uninstantiable
    }
}
