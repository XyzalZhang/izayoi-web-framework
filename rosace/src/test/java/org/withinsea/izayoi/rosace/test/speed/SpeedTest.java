package org.withinsea.izayoi.rosace.test.speed;

import org.junit.Assert;
import org.junit.Test;
import org.withinsea.izayoi.common.util.Vars;
import org.withinsea.izayoi.common.util.Varstack;
import org.withinsea.izayoi.rosace.adapter.mvel.Mvel2ElEngine;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.HostlangUtils;
import org.withinsea.izayoi.rosace.core.kernel.Renderer;
import org.withinsea.izayoi.rosace.core.kernel.TemplateCompiler;
import org.withinsea.izayoi.rosace.test.ResourceUtils;
import org.withinsea.izayoi.rosace.util.RosaceUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-8
 * Time: 下午12:49
 */
public class SpeedTest {

    Renderer tempRender = new TemplateCompiler.CompiledTemplate() {
        @Override
        public void renderTo(PrintWriter _outwriter, Varstack _varstack) throws Exception {
            _outwriter.print("");
            _varstack.push();
            _varstack.put("nums", org.withinsea.izayoi.rosace.adapter.mvel.Mvel2ElEngine.Tricker.eval("[1,2,3,4]", _varstack));
            _varstack.push();
            _outwriter.print("<div>\n    <ul>\n        ");
            {
                _varstack.push();
                java.util.Iterator num_iter = ((Iterable) org.withinsea.izayoi.rosace.core.impl.grammar.ns.c.Cycle.asIterable(org.withinsea.izayoi.rosace.adapter.mvel.Mvel2ElEngine.Tricker.eval("nums", _varstack))).iterator();
                while (num_iter.hasNext()) {
                    Object num = num_iter.next();
                    _varstack.put("num", num);
                    _varstack.push();
                    _outwriter.print("<li>");
                    _outwriter.print(org.withinsea.izayoi.rosace.core.impl.template.HostlangUtils.checkNull(org.withinsea.izayoi.rosace.adapter.mvel.Mvel2ElEngine.Tricker.eval("num", _varstack), ""));
                    _outwriter.print("</li>");
                    _varstack.pop();
                }
                _varstack.pop();
            }
            _outwriter.print("\n    </ul>\n</div>");
            _varstack.pop();
            _varstack.pop();
            _outwriter.print("\n");
        }
    };

    Renderer tempRender2 = new TemplateCompiler.CompiledTemplate() {
        @Override
        public void renderTo(PrintWriter _outwriter, Varstack _varstack) throws Exception {
            _outwriter.print("");
            _varstack.push();
            _varstack.put("nums", org.withinsea.izayoi.rosace.adapter.mvel.Mvel2ElEngine.Tricker.eval("[1,2,3,4]", _varstack));
            _varstack.push();
            _outwriter.print("<div>\n    <ul>\n        <li>");
            _outwriter.print(org.withinsea.izayoi.rosace.core.impl.template.HostlangUtils.checkNull(org.withinsea.izayoi.rosace.adapter.mvel.Mvel2ElEngine.Tricker.eval("num", _varstack), ""));
            _outwriter.print("</li>\n    </ul>\n</div>");
            _varstack.pop();
            _varstack.pop();
            _outwriter.print("\n");
        }
    };

    @Test
    public void CompareSpeed() throws RosaceException, IOException {

        int maxCount = 10000;

        Renderer staticRender = new TemplateCompiler.CompiledTemplate() {
            @Override
            public void renderTo(PrintWriter _outwriter, Varstack _varstack) throws Exception {
                _outwriter.print("<p>\r\n    Hello, ");
                _outwriter.print(HostlangUtils.checkNull(Mvel2ElEngine.Tricker.eval("name", _varstack), ""));
                _outwriter.print("\r\n</p>");
            }
        };

        String template = ResourceUtils.text("template.html");
        Vars context = ResourceUtils.props("context.properties");
        Renderer dynamicRenderer = RosaceUtils.compile(template);

        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < maxCount; i++) {
            staticRender.render(new StringWriter(), context);
        }
        double staticTime = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for (int i = 0; i < maxCount; i++) {
            dynamicRenderer.render(new StringWriter(), context);
        }
        double dynamicTime = System.currentTimeMillis() - start;

        Assert.assertTrue(dynamicTime < staticTime || ((dynamicTime - staticTime) / staticTime) < 0.2);
    }
}

