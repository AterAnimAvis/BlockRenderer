package com.unascribed.blockrenderer.client.varia.logging;

import com.unascribed.blockrenderer.Reference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;

public class Log {

    public static final Logger LOGGER = LogManager.getLogger(Reference.NAME);

    /* ===================================================================================================== API ==== */

    public static void info(Marker marker, String message, Object... args) {
        LOGGER.info(marker, message, args);
    }

    public static void trace(Marker marker, String message, Object... args) {
        LOGGER.trace(marker, message, args);
    }

    public static void debug(Marker marker, String message, Object... args) {
        LOGGER.debug(marker, message, args);
    }

    public static void debug(Marker marker, Message message) {
        LOGGER.debug(marker, message);
    }

    public static void warn(Marker marker, String message, Object... args) {
        LOGGER.warn(marker, message, args);
    }

    public static void error(Marker marker, String message, Throwable t) {
        LOGGER.error(marker, message, t);
    }

    public static void error(Marker marker, String message, Object... args) {
        LOGGER.error(marker, message, args);
    }

    public static void fatal(Marker marker, String message, Object... args) {
        LOGGER.fatal(marker, message, args);
    }

}
