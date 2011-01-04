package org.withinsea.izayoi.rosace.test.rough;

import org.junit.Assert;
import org.junit.Test;
import org.withinsea.izayoi.common.util.Vars;
import org.withinsea.izayoi.rosace.test.ResourceUtils;
import org.withinsea.izayoi.rosace.util.RosaceUtils;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-7
 * Time: 下午7:53
 */
public class RoughTest {

    @Test
    public void testDom() throws Exception {

        String expected = ResourceUtils.text("expected.html");
        String template = ResourceUtils.text("template.html");
        Vars context = ResourceUtils.props("context.properties");

        String generated = RosaceUtils.render(template, context);

        Assert.assertEquals(expected, generated);
    }

    @Test
    public void testEltext() throws Exception {

        String expected = ResourceUtils.text("expected.txt");
        String template = ResourceUtils.text("template.txt");
        Vars context = ResourceUtils.props("context.properties");

        String generated = RosaceUtils.render(template, "elt", context);

        Assert.assertEquals(expected, generated);
    }
}
