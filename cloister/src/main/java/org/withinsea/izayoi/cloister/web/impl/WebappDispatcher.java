package org.withinsea.izayoi.cloister.web.impl;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.impl.dispatcher.DefaultDispatcher;
import org.withinsea.izayoi.cloister.core.kernal.Request;
import org.withinsea.izayoi.cloister.web.kernal.RequestAware;
import org.withinsea.izayoi.common.servlet.ParamsAdjustHttpServletRequestWrapper;
import org.withinsea.izayoi.common.servlet.ServletFilterUtils;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 上午3:03
 */
public class WebappDispatcher extends DefaultDispatcher {

    @Override
    protected boolean isDirectAccess(Request request) throws CloisterException {
        if (!(request instanceof RequestAware)) {
            throw new UnsupportedOperationException("WebappDispatcher needs a RequestAware request.");
        } else {
            RequestAware reqa = (RequestAware) request;
            return !(ServletFilterUtils.isForwarded(reqa.getHttpServletRequest())
                    || ServletFilterUtils.isIncluded(reqa.getHttpServletRequest()));
        }
    }

    @Override
    protected String getOutputContentType(Request request, String encoding) {
        if (!(request instanceof RequestAware)) {
            throw new UnsupportedOperationException("WebappDispatcher needs a RequestAware request.");
        } else {
            RequestAware reqa = (RequestAware) request;
            String mimeType = reqa.getHttpServletRequest().getSession().getServletContext().getMimeType(request.getPath());
            return mimeType + "; charset=" + encoding;
        }
    }

    @Override
    protected void redispatch(Request request, Target target) throws CloisterException {
        if (!(request instanceof RequestAware)) {
            throw new UnsupportedOperationException("WebappDispatcher needs a RequestAware request.");
        } else {
            RequestAware reqa = (RequestAware) request;
            if (target.getPath().endsWith("/") && !request.getPath().endsWith("/")) {
                redirect(request, request.getPath() + "/");
            } else {
                ParamsAdjustHttpServletRequestWrapper wrappedHttpReq = new ParamsAdjustHttpServletRequestWrapper(reqa.getHttpServletRequest());
                wrappedHttpReq.appendParams(target.getParameters());
                RequestRequest wrappedRequest = new RequestRequest(request.getEnvironment(), request.getScope(),
                        wrappedHttpReq, reqa.getHttpServletResponse(), reqa.getFilterChain());
                if (ServletFilterUtils.isIncluded(reqa.getHttpServletRequest())) {
                    include(wrappedRequest, target.getPath());
                } else {
                    forward(wrappedRequest, target.getPath());
                }
            }
        }
    }

    @Override
    protected void forward(Request request, String targetPath) throws CloisterException {
        if (!(request instanceof RequestAware)) {
            throw new UnsupportedOperationException("forwarding in WebappDispatcher needs a RequestAware request.");
        } else {
            RequestAware reqa = (RequestAware) request;
            try {
                reqa.getHttpServletRequest().getRequestDispatcher(targetPath).forward(
                        reqa.getHttpServletRequest(), reqa.getHttpServletResponse());
            } catch (ServletException e) {
                throw new CloisterException(e);
            } catch (IOException e) {
                throw new CloisterException(e);
            }
        }
    }

    @Override
    protected void include(Request request, String targetPath) throws CloisterException {
        if (!(request instanceof RequestAware)) {
            throw new UnsupportedOperationException("forwarding in WebappDispatcher needs a RequestAware request.");
        } else {
            RequestAware reqa = (RequestAware) request;
            try {
                reqa.getHttpServletRequest().getRequestDispatcher(targetPath).include(
                        reqa.getHttpServletRequest(), reqa.getHttpServletResponse());
            } catch (ServletException e) {
                throw new CloisterException(e);
            } catch (IOException e) {
                throw new CloisterException(e);
            }
        }
    }

    @Override
    protected void redirect(Request request, String targetPath) throws CloisterException {
        if (!(request instanceof RequestAware)) {
            throw new UnsupportedOperationException("forwarding in WebappDispatcher needs a RequestAware request.");
        } else {
            RequestAware reqa = (RequestAware) request;
            String requestFolder = ("/"
                    + reqa.getHttpServletRequest().getSession().getServletContext().getContextPath()
                    + ServletFilterUtils.getRequestPath(reqa.getHttpServletRequest())
            ).replaceAll("/[^/]+$", "/").replaceAll("/+", "/");
            targetPath = targetPath.startsWith("/") ? targetPath : requestFolder + targetPath;
            while (!targetPath.startsWith("/..") && targetPath.indexOf("/..") > 0) {
                targetPath = targetPath.replaceFirst("/[^/]+/\\.\\.", "");
            }
            try {
                reqa.getHttpServletResponse().sendRedirect(targetPath);
            } catch (IOException e) {
                throw new CloisterException(e);
            }
        }
    }
}
