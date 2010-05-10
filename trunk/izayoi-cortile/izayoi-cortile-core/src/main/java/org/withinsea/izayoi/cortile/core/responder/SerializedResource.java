package org.withinsea.izayoi.cortile.core.responder;

import org.withinsea.izayoi.commons.servlet.ServletFilterUtils;
import org.withinsea.izayoi.commons.util.IOUtils;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.invoker.ScriptInvoker;
import org.withinsea.izayoi.core.scope.custom.Request;
import org.withinsea.izayoi.core.serialize.SerializeManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-8
 * Time: 14:16:08
 */
public class SerializedResource extends ScriptInvoker<Request> {

    protected SerializeManager serializeManager;
    protected String encoding;

    @Override
    protected boolean processResult(Object result, String codePath, Request scope) throws IzayoiException {

        HttpServletRequest request = scope.getRequest();
        HttpServletResponse response = scope.getResponse();

        try {

            String accept = request.getHeader("Accept");
            String asType = new Path(codePath).getMainType();
            if (asType.equals("")) {
                asType = serializeManager.findType(accept);
                if (asType == null || asType.equals("")) {
                    response.sendError(404);
                    return false;
                }
            }

            String mimeType = codeManager.getMimeType(asType);
            if (!ServletFilterUtils.matchContentType(mimeType, accept)) {
                response.sendError(404);
                return false;
            } else if (mimeType != null) {
                response.setContentType(mimeType + "; charset=" + encoding);
            }

            response.setCharacterEncoding(encoding);

            if (result == null) {

            } else if (result instanceof String) {
                IOUtils.write((String) result, response.getOutputStream(), encoding);
            } else if (result.getClass().isArray() && result.getClass().getComponentType() == byte.class) {
                response.getOutputStream().write((byte[]) result);
            } else {
                serializeManager.serialize(result.getClass(), result, asType, response.getOutputStream(), encoding);
            }

            response.flushBuffer();

        } catch (IOException e) {
            throw new IzayoiException(e);
        }

        return false;
    }

    protected List<String> getAcceptContentTypeRegexes(HttpServletRequest request) {
        List<String> regexes = new ArrayList<String>();
        String accepts = request.getHeader("Accept").trim();
        if (accepts.equals("")) {
            regexes.add(".*");
        } else {
            for (String accept : accepts.split("[,;\\s]+")) {
                if (accept.indexOf("/") >= 0) {
                    regexes.add(accept.replace("*", ".+"));
                }
            }
        }
        return regexes;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setSerializeManager(SerializeManager serializeManager) {
        this.serializeManager = serializeManager;
    }
}
