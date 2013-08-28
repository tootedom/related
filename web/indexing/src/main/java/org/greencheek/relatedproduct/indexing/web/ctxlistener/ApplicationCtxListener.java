package org.greencheek.relatedproduct.indexing.web.ctxlistener;

import org.greencheek.relatedproduct.indexing.bootstrap.ApplicationCtx;
import org.greencheek.relatedproduct.indexing.bootstrap.BootstrapApplicationCtx;
import org.greencheek.relatedproduct.util.config.Configuration;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created with IntelliJ IDEA.
 * User: dominictootell
 * Date: 02/06/2013
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationCtxListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext ctx = servletContextEvent.getServletContext();
        ApplicationCtx applicationCtx = new BootstrapApplicationCtx();

        ctx.setAttribute(Configuration.APPLICATION_CONTEXT_ATTRIBUTE_NAME,applicationCtx);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
