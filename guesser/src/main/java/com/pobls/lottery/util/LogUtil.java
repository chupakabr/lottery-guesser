package com.pobls.lottery.util;

import java.util.logging.Logger;

/**
 * Created by myltik on 19/10/2016.
 */
final public class LogUtil {

    /**
     * Enable/Disable debug. Disabled by default.
     * @note not really best practice, but whatever! we have just one thread anyway }:>
     */
    public static volatile boolean isDebug = false;

    /**
     * Log this only when debug logging is enabled.
     * @param logger     Logger instance
     * @param message    Debug message
     */
    public static void d(Logger logger, String message) {
        if (isDebug) {
            logger.info(message);
        }
    }

    /**
     * Always log this.
     * @param logger     Logger instance
     * @param message    Info message
     */
    public static void l(Logger logger, String message) {
        logger.info(message);
    }
}
