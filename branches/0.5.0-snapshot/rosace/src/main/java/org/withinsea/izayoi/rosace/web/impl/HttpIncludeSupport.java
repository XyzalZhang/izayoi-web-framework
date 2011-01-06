package org.withinsea.izayoi.rosace.web.impl;

import org.withinsea.izayoi.common.servlet.ServletFilterUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.kernel.IncludeSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-31
 * Time: 下午2:30
 */
public class HttpIncludeSupport implements IncludeSupport {

    @Override
    public void include(Writer writer, String path, Map<String, Object> context) throws RosaceException {

        HttpServletRequest httpReq = (HttpServletRequest) context.get("request");
        HttpServletResponse httpResp = (HttpServletResponse) context.get("response");

        String requestPath = ServletFilterUtils.getRequestPath(httpReq);
        String includePath = (path == null) ? requestPath
                : (!path.startsWith("/")) ? requestPath.replaceAll("/[^/]+$", "/") + path
                : path;
        try {
            writer.flush();
            httpReq.getRequestDispatcher(includePath).include(httpReq, httpResp);
        } catch (ServletException e) {
            throw new RosaceException(e);
        } catch (IOException e) {
            throw new RosaceException(e);
        }
    }
}
