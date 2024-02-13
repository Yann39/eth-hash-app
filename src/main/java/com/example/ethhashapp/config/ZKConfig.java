package com.example.ethhashapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.impl.GlobalDesktopCacheProvider;

import javax.annotation.PostConstruct;

/**
 * ZK framework configuration.
 *
 * @author Yann39
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class ZKConfig {

    private static final String BOOTSTRAP_CSS = "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css";
    private static final String COOKIE_CONSENT = "//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.1.0/cookieconsent.min.css";
    private static final String CUSTOM_CSS = "/css/custom.css?t=" + System.currentTimeMillis();

    private static final String ORG_ZKOSS_THEME_PREFERRED = "org.zkoss.theme.preferred";
    private static final String ORG_ZKOSS_ZUL_PROGRESSBOX_POSITION = "org.zkoss.zul.progressbox.position";
    private static final String ORG_ZKOSS_ZK_ZUML_CACHE = "org.zkoss.zk.ZUML.cache";
    private static final String ORG_ZKOSS_ZK_WPD_CACHE = "org.zkoss.zk.WPD.cache";
    private static final String ORG_ZKOSS_ZK_WCS_CACHE = "org.zkoss.zk.WCS.cache";
    private static final String ORG_ZKOSS_WEB_CLASS_WEB_RESOURCE_CACHE = "org.zkoss.web.classWebResource.cache";
    private static final String ORG_ZKOSS_UTIL_LABEL_CACHE = "org.zkoss.util.label.cache";
    private static final String ORG_ZKOSS_BIND_DEBUGGER_FACTORY_ENABLE = "org.zkoss.bind.DebuggerFactory.enable";
    private static final String ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE = "org.zkoss.web.preferred.timeZone";
    private static final String ORG_ZKOSS_ZK_UI_UUID_RECYCLE_DISABLED = "org.zkoss.zk.ui.uuidRecycle.disabled";

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    @Configuration
    public static class ZKCommonConfig {

        @PostConstruct
        public void initCommonProperties() {

            log.info("Using ZK development configuration");

            // specify additional CSS files to load
            WebApps.getCurrent().getConfiguration().addThemeURI(BOOTSTRAP_CSS);
            WebApps.getCurrent().getConfiguration().addThemeURI(COOKIE_CONSENT);
            WebApps.getCurrent().getConfiguration().addThemeURI(CUSTOM_CSS);

            // use global desktop cache (stores all desktops from the same application into one desktop cache)
            WebApps.getCurrent().getConfiguration().setDesktopCacheProviderClass(GlobalDesktopCacheProvider.class);

            // session timeout
            WebApps.getCurrent().getConfiguration().setSessionMaxInactiveInterval(1800);

            // message to display on session timeout
            WebApps.getCurrent().getConfiguration().setTimeoutMessage("ajax", Labels.getLabel("message.timeout"));

            // enable loading original uncompressed JavaScript files
            WebApps.getCurrent().getConfiguration().setDebugJS(true);

            // the default ZK theme to use
            Library.setProperty(ORG_ZKOSS_THEME_PREFERRED, "iceblue");

            // specifies how to display the progress box at the client
            Library.setProperty(ORG_ZKOSS_ZUL_PROGRESSBOX_POSITION, "center");

            // set time zone for date pickers, calendars, etc.
            Library.setProperty(ORG_ZKOSS_WEB_PREFERRED_TIME_ZONE, "Europe/Zurich");

            // better to disable UID recycle to prevent some unwanted widget uuid reusing at client side accidentally
            Library.setProperty(ORG_ZKOSS_ZK_UI_UUID_RECYCLE_DISABLED, TRUE);

            // specifies whether to cache the result of ZK ZUML files (the *.zul files in the classpath) at the server
            Library.setProperty(ORG_ZKOSS_ZK_ZUML_CACHE, FALSE);

            // specifies whether to cache the result of ZK WPD files (the JavaScript code for widgets) at the server
            Library.setProperty(ORG_ZKOSS_ZK_WPD_CACHE, FALSE);

            // specifies whether to cache the result of ZK WCS files (the stylesheets of components) at the server
            Library.setProperty(ORG_ZKOSS_ZK_WCS_CACHE, FALSE);

            // specifies whether to allow the browsers to cache web resources (CSS and JS files)
            Library.setProperty(ORG_ZKOSS_WEB_CLASS_WEB_RESOURCE_CACHE, FALSE);

            // specifies whether to cache zk-labels.properties
            Library.setProperty(ORG_ZKOSS_UTIL_LABEL_CACHE, FALSE);

            // enable debugging MVVM commands and binding (very verbose)
            Library.setProperty(ORG_ZKOSS_BIND_DEBUGGER_FACTORY_ENABLE, FALSE);

        }

    }

}