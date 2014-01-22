package org.greencheek.related.indexing.web.ctxlistener;

import org.greencheek.related.indexing.web.bootstrap.ApplicationCtx;
import org.greencheek.related.indexing.web.bootstrap.BootstrapApplicationCtx;
import org.greencheek.related.util.config.Configuration;

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
@WebListener
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
