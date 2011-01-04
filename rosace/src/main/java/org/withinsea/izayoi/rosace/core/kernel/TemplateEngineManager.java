package org.withinsea.izayoi.rosace.core.kernel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-3
 * Time: 上午12:37
 */
public class TemplateEngineManager {

    protected Map<String, TemplateEngine> templateEngines  = new HashMap<String, TemplateEngine>();

    public TemplateEngine lookupTemplateEngine(String type) {
        return templateEngines.get(type);
    }

    public void registerTemplateEngine(String type, TemplateEngine templateEngine) {
        templateEngines.put(type, templateEngine);
    }
}
