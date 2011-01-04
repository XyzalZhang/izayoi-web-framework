package org.withinsea.izayoi.rosace.core.impl.template.txt;

import org.withinsea.izayoi.common.util.StringUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.HostlangIntermediatelyEngine;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-11
 * Time: 下午1:56
 */
public class EltextTemplateEngine extends HostlangIntermediatelyEngine {

    @Override
    public String getName() {
        return "IzayoiRosace.Eltext";
    }

    @Override
    protected String precompileTemplateToHostlang(String template) throws RosaceException {
        return "<%" + precompileOpenScope() + "%>" +
                StringUtils.replaceAll(template, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
                    @Override
                    public String replace(String... groups) {
                        return "<%=" + precompileEl(groups[1].replace("\\}", "}"), true) + "%>";
                    }
                }) +
                "<%" + precompileCloseScope() + "%>";
    }
}
