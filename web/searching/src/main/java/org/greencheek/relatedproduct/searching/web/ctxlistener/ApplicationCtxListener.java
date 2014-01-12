package org.greencheek.relatedproduct.searching.web.ctxlistener;

import org.greencheek.relatedproduct.searching.web.bootstrap.SearchBootstrapApplicationCtx;
import org.greencheek.relatedproduct.util.config.Configuration;
import org.greencheek.relatedproduct.searching.web.bootstrap.ApplicationCtx;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
@WebListener()
public class ApplicationCtxListener implements ServletContextListener{

    private final ApplicationCtx applicationCtx;

    public ApplicationCtxListener() {
        applicationCtx = new SearchBootstrapApplicationCtx();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();
        ctx.setAttribute(Configuration.APPLICATION_CONTEXT_ATTRIBUTE_NAME,applicationCtx);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        applicationCtx.shutdown();
    }
}
