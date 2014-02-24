/*
 *
 *  * Licensed to Relateit under one or more contributor
 *  * license agreements. See the NOTICE file distributed with
 *  * this work for additional information regarding copyright
 *  * ownership. Relateit licenses this file to you under
 *  * the Apache License, Version 2.0 (the "License"); you may
 *  * not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package org.greencheek.related.indexing.web.ctxlistener;

import org.apache.logging.log4j.core.async.AsyncLogger;
import org.greencheek.related.indexing.web.bootstrap.ApplicationCtx;
import org.greencheek.related.indexing.web.bootstrap.BootstrapApplicationCtx;
import org.greencheek.related.util.config.Configuration;
import org.greencheek.related.util.config.ConfigurationConstants;

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
        ConfigurationConstants.setLoggingProperties(ConfigurationConstants.PROPNAME_INDEXING_LOG_FILE,
                ConfigurationConstants.PROPNAME_INDEXING_LOG_LEVEL,
                "indexing.log","WARN");
        ServletContext ctx = servletContextEvent.getServletContext();
        ApplicationCtx applicationCtx = new BootstrapApplicationCtx();

        ctx.setAttribute(Configuration.APPLICATION_CONTEXT_ATTRIBUTE_NAME,applicationCtx);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        AsyncLogger.stop();
    }
}
