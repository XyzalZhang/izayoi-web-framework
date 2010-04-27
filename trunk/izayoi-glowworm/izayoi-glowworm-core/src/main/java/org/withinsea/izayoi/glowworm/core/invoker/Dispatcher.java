package org.withinsea.izayoi.glowworm.core.invoker;

import org.withinsea.izayoi.core.bindings.scope.Scope;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 21:54:20
 */
public class Dispatcher extends InterpretInvoker {

    @Override
    protected boolean processResult(HttpServletRequest request, HttpServletResponse response, Object result, Scope scope) throws GlowwormException {
        if (result instanceof String) {
            String url = ((String) result).trim();
            try {
                if (url.startsWith("forward:")) {
                    url = url.substring("forward:".length()).trim();
                    request.getRequestDispatcher(url).forward(request, response);
                } else if (url.startsWith("redirect:")) {
                    url = url.substring("redirect:".length()).trim();
                    url = (url.startsWith("/")) ? request.getContextPath() + url : url;
                    response.sendRedirect(url);
                } else {
                    response.sendRedirect(url);
                }
            } catch (ServletException e) {
                throw new GlowwormException(e);
            } catch (IOException e) {
                throw new GlowwormException(e);
            }
            return false;
        } else {
            return true;
        }
    }
}