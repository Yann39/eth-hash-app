package com.example.ethhashapp.vm;

import com.example.ethhashapp.utils.ApplicationConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.Locales;
import org.zkoss.util.resource.Labels;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;

import java.io.IOException;
import java.util.Locale;

/**
 * @author Yann39
 * @since 1.0.0
 */
@VariableResolver(DelegatingVariableResolver.class)
@Slf4j
public class MainVm {

    //region Variables

    // page to display in the body
    @Getter
    private String centerPage;

    @Getter
    private String previousPage;

    @Getter
    private String pageTitle;
    //endregion

    //region Init

    /**
     * View-model initialization method.
     * <br>
     * This is the central method called each time a user request a page.
     * <br>
     * It manages authentication and redirects.
     */
    @Init
    public void init() {
        log.info("Init " + this.getClass().getSimpleName());

        // resets all cached labels (just in case as they are not reloaded automatically when file is modified)
        Labels.reset();

        // get application title
        final String appTitle = Labels.getLabel("title.applicationTitle");

        // set MDC context (for logging)
        MDC.put("application.name", appTitle);

        // set page title
        Executions.getCurrent().getDesktop().getFirstPage().setTitle(appTitle);

        // set locale to a managed locale (in case it is not 'fr' nor 'en', use 'en')
        log.info("Browser language is " + Locales.getCurrent().getLanguage());
        if (Locales.getCurrent().getLanguage().equalsIgnoreCase("fr")) {
            changeLang("fr", false);
        } else {
            changeLang("en", false);
        }

        // get attributes from controller
        final String page = Executions.getCurrent().hasAttribute("page") ? (String) Executions.getCurrent().getAttribute("page") : "home";

        // keep previous page before navigating to new page
        previousPage = centerPage;

        // loading specified page from route controller
        if (page.equals("diplomas")) {
            log.info("Loading diplomas page");
            pageTitle = Labels.getLabel("title.diplomas");
            centerPage = ApplicationConstants.DIPLOMAS_PAGE;
        } else {
            log.info("Loading home page (default)");
            pageTitle = Labels.getLabel("title.home");
            centerPage = ApplicationConstants.HOME_PAGE;
        }

    }
    //endregion

    //region Command methods

    /**
     * Change the current application language to the specified language.
     *
     * @param lang       The language code as {@link String}, usually 'fr' or 'en'
     * @param reloadPage A boolean indicating if we want to reload the page or not
     */
    @GlobalCommand
    public void changeLang(@BindingParam("lang") String lang,
                           @BindingParam("reloadPage") boolean reloadPage) {
        log.info("Changing locale to " + lang);
        final Locale prefLocale = new Locale(lang);
        // change locale in HTTP session
        Sessions.getCurrent().setAttribute(Attributes.PREFERRED_LOCALE, prefLocale);
        // ask the browser to reload the messages
        try {
            Clients.reloadMessages(prefLocale);
        } catch (IOException e) {
            log.error("An error occurred while reloading messages : " + e.getMessage());
        }
        // change the default locale used by the current thread
        Locales.setThreadLocal(prefLocale);
        // reload the page if required
        if (reloadPage) {
            Executions.getCurrent().sendRedirect("");
        }
    }
    //endregion

}