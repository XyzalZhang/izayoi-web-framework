package org.withinsea.izayoi.cloister.core.impl.script;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-3
 * Time: 上午10:01
 */
public class PropertiesScriptEngine extends StaticTxtData {

    public PropertiesScriptEngine(String encoding) {
        super(encoding);
    }

    @Override
    protected Object load(String txt) throws IOException {
        java.util.Properties props = new java.util.Properties();
        props.load(new StringReader(txt));
        return props;
    }
}
