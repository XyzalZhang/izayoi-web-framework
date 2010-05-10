package org.withinsea.izayoi.cortile.core.responder;

import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.invoker.Invoker;
import org.withinsea.izayoi.core.scope.custom.Request;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.compile.CompileManager;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-8
 * Time: 14:12:03
 */
public class Template implements Invoker<Request> {

    protected CodeManager codeManager;
    protected CompileManager compileManager;
    protected String encoding;

    @Override
    public boolean invoke(String codePath, Request scope) throws IzayoiException {

        HttpServletRequest request = scope.getRequest();
        HttpServletResponse response = scope.getResponse();
        FilterChain chain = scope.getChain();

        String entrancePath = compileManager.update(codePath, false);
        try {
            if (!entrancePath.equals(codePath)) {
                response.setCharacterEncoding(encoding);
                String mimeType = codeManager.getMimeType(new Path(codePath).getMainType());
                if (mimeType != null) {
                    response.setContentType(mimeType + "; charset=" + encoding);
                }
                request.getRequestDispatcher(entrancePath).forward(request, response);
                if (mimeType != null) {
                    response.setContentType(mimeType + "; charset=" + encoding);
                }
            } else {
                if (chain != null) {
                    chain.doFilter(request, response);
                } else {
                    response.sendError(404, codePath);
                }
            }
        } catch (Exception e) {
            throw new CortileException(e);
        }

        return false;
    }

    public void setCompileManager(CompileManager compileManager) {
        this.compileManager = compileManager;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }
}
