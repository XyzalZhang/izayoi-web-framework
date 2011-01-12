package org.withinsea.izayoi.rosace.web.impl;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.common.servlet.ServletFilterUtils;
import org.withinsea.izayoi.common.util.StringUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.HostlangUtils;
import org.withinsea.izayoi.rosace.core.impl.template.dom.DomTemplateEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.Grammar;
import org.withinsea.izayoi.rosace.core.kernel.IncludeSupport;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;
import org.withinsea.izayoi.rosace.core.kernel.RosaceConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-12
 * Time: 上午9:21
 */
public class IncludedUrlAdjust implements AttrGrammar {

    protected static Set<String> URL_ATTRS = new HashSet<String>(Arrays.asList("href", "url", "src"));

    @Override
    public boolean acceptAttr(Attribute attr) {
        return (attr.getParent().getName().toLowerCase().equals("base")
                || URL_ATTRS.contains(attr.getName().toLowerCase()));
    }

    @Override
    @Priority(Grammar.Priority.LOWER)
    public void processAttr(Attribute attr) throws RosaceException {

        PrecompiletimeContext ctx = PrecompiletimeContext.get();
        DomTemplateEngine engine = ctx.getEngine();
        Element elem = attr.getParent();

        if (attr.getParent().getName().toLowerCase().equals("base")) {
            try {
                DomUtils.insertBefore("<%" + engine.precompilePut(RosaceConstants.ATTR_HAS_BASEURL, "true") + "%>", elem);
            } catch (Exception e) {
                throw new RosaceException(e);
            }
        } else {
            String valueCode = "\"\"" + StringUtils.replaceAll(attr.getValue(), "<%=([\\s\\S]*?)%>", new StringUtils.Replace() {
                @Override
                public String replace(String... groups) {
                    return "+(" + groups[1] + ")";
                }
            }, new StringUtils.Transform() {
                @Override
                public String transform(String str) {
                    return "+\"" + HostlangUtils.javaString(str) + "\"";
                }
            });
            String adjustCode = IncludedUrlAdjust.class.getCanonicalName() + ".adjust(" +
                    valueCode + ", " + RosaceConstants.VARIABLE_VARSTACK + ")";
            attr.setValue("<%=" + adjustCode + "%>");
        }
    }

    public static String adjust(String url, Map<String, Object> context) {

        try {
            Class.forName("javax.servlet.http.HttpServletRequest");
        } catch (ClassNotFoundException e) {
            return null;
        }

        HttpServletRequest httpReq = (HttpServletRequest) context.get("request");
        Deque<IncludeSupport.Tracer.Including> includingStack = IncludeSupport.Tracer.getIncludingStack();
        boolean hasBaseUrl = Boolean.TRUE.equals(context.get(RosaceConstants.ATTR_HAS_BASEURL));

        if (httpReq == null || hasBaseUrl || includingStack.isEmpty()
                || url.startsWith("/") || url.matches("^\\d+://.*")) {
            return url;
        }

        String[] original = splitFolder(ServletFilterUtils.getClientPath(httpReq));
        String[] current = splitFolder(IncludeSupport.Tracer.getPath());

        String rel = "";
        int diff = current.length - original.length;
        if (diff < 0) {
            for (int i = 0; i < -diff; i++) {
                rel += "../";
            }
        } else {
            for (int i = original.length; i < current.length; i++) {
                rel += current[i] + "/";
            }
        }

        return rel + url;
    }

    protected static String[] splitFolder(String path) {
        path = path.trim().replaceAll("/[^/]+$", "/").replaceAll("^/+|/+$", "");
        return path.equals("") ? new String[]{} : path.split("/");
    }
}
