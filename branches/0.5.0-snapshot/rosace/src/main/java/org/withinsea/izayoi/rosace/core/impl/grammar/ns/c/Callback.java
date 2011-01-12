package org.withinsea.izayoi.rosace.core.impl.grammar.ns.c;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.common.util.Varstack;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.kernel.IncludeSupport;
import org.withinsea.izayoi.rosace.core.kernel.Renderer;
import org.withinsea.izayoi.rosace.core.kernel.RosaceConstants;

import java.io.PrintWriter;
import java.util.Deque;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-11
 * Time: 下午10:31
 */
public class Callback extends Call {

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("callback");
    }

    @Override
    public void processAttr(Attribute attr) throws RosaceException {
        Element elem = attr.getParent();
        String attrvalue = attr.getValue();
        if (!attrvalue.startsWith(":")) {
            throw new RosaceException("'" + attrvalue + "' is not a valid value: " +
                    "callback value should be a section name started with ':'.");
        }
        processAttr(elem, attrvalue);
        attr.detach();
    }

    @Override
    protected String precompileCall(String targetCode) throws RosaceException {
        return Callback.class.getCanonicalName() + ".callback(this," +
                RosaceConstants.VARIABLE_WRITER + ", " +
                RosaceConstants.VARIABLE_VARSTACK + ", " +
                targetCode + ")";
    }

    public static boolean callback(Renderer renderer, PrintWriter writer, Varstack varstack, String target) throws Exception {
        Deque<IncludeSupport.Tracer.Including> includingStack = IncludeSupport.Tracer.getIncludingStack();
        if (includingStack.isEmpty()) {
            return false;
        } else {
            String callbackScopeCallId = (String) varstack.get(RosaceConstants.ATTR_CALL_ID);
            String suffix = (callbackScopeCallId == null) ? "" : ("@" + callbackScopeCallId);
            String callerPath = IncludeSupport.Tracer.getIncludingStack().peek().getIncluderPath();
            return call(null, renderer, writer, varstack, callerPath + target + suffix);
        }
    }
}
