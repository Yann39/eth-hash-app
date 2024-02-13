package com.example.ethhashapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.Notification;

/**
 * Application utilities.
 *
 * @author Yann39
 * @since 1.0.0
 */
@Slf4j
public final class ApplicationUtils {

    public static final String MIDDLE_CENTER = "middle_center";

    private ApplicationUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Display an information {@code message} to the user using ZK notifications.
     *
     * @param message The information message to display
     */
    public static void showInfo(String message) {
        Notification.show(message, Clients.NOTIFICATION_TYPE_INFO, null, MIDDLE_CENTER, 8000, true);
        log.info(message);
    }

    /**
     * Display a warning {@code message} to the user using ZK notifications.
     *
     * @param message The warning message to display
     */
    public static void showWarning(String message) {
        Notification.show(message, Clients.NOTIFICATION_TYPE_WARNING, null, MIDDLE_CENTER, 8000, true);
        log.warn(message);
    }

    /**
     * Display an error {@code message} to the user using ZK notifications.
     *
     * @param message The error message to display
     */
    public static void showError(String message) {
        Notification.show(message, Clients.NOTIFICATION_TYPE_ERROR, null, MIDDLE_CENTER, 8000, true);
        log.error(message);
    }

}