package org.withinsea.izayoi.bundle.adapter.spring;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.servlet.DispatcherServlet;
import org.withinsea.izayoi.bundle.facade.IzayoiWebFacade;
import org.withinsea.izayoi.cloister.adapter.spring.SpringScope;
import org.withinsea.izayoi.cloister.beta.Jsp_beta;
import org.withinsea.izayoi.cloister.core.feature.postscript.ScriptEngine;
import org.withinsea.izayoi.cloister.core.kernal.Scope;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-5
 * Time: 上午10:17
 */
public class SpringIzayoiFilter extends DispatcherServlet implements Filter {

    protected static ThreadLocal<FilterChain> CHAIN_CARRIER = new ThreadLocal<FilterChain>();

    protected IzayoiWebFacade izayoiWebFacade = new IzayoiWebFacade() {

        @Override
        protected Scope createGlobalScope() {
            return new SpringScope(getWebApplicationContext());
        }

        @Override
        protected ScriptEngine createJspScriptEngine() {
            String encoding = globalConfig.getProperty("cloister.encoding");
            return new Jsp_beta(servletContext, encoding) {
                @Override
                protected Object createJspBean(Class<?> jspclass) throws Exception {
                    Object jspbean = jspclass.newInstance();
                    AutowireCapableBeanFactory factory = getWebApplicationContext().getAutowireCapableBeanFactory();
                    factory.autowireBeanProperties(jspclass, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
                    return jspbean;
                }
            };
        }
    };

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

        super.init(new ServletConfig() {

            @Override
            public String getInitParameter(String s) {
                return filterConfig.getInitParameter(s);
            }

            @Override
            public String getServletName() {
                return filterConfig.getFilterName();
            }

            @Override
            public ServletContext getServletContext() {
                return filterConfig.getServletContext();
            }

            @Override
            @SuppressWarnings("unchecked")
            public Enumeration<String> getInitParameterNames() {
                return filterConfig.getInitParameterNames();
            }
        });

        izayoiWebFacade.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CHAIN_CARRIER.set(chain);
        service(request, response);
        CHAIN_CARRIER.remove();
    }

    @Override
    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FilterChain chain = CHAIN_CARRIER.get();
        CHAIN_CARRIER.remove();
        if (chain == null) {
            noHandlerFound0(request, response);
        } else {
            izayoiWebFacade.doFilter(request, response, chain);
        }
    }

    protected void noHandlerFound0(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.noHandlerFound(request, response);
    }
}
