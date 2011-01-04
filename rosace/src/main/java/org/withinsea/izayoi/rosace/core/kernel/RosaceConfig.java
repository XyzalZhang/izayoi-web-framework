package org.withinsea.izayoi.rosace.core.kernel;

import org.withinsea.izayoi.rosace.core.exception.RosaceRuntimeException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-7
 * Time: 下午5:38
 */
public class RosaceConfig {

    protected static final Properties DEFAULT = new Properties(); static {
        try {
            DEFAULT.load(new InputStreamReader(RosaceConfig.class.getResourceAsStream("default.properties"), "UTF-8"));
        } catch (IOException e) {
            throw new RosaceRuntimeException("failed in loading default configuration.");
        }
    }

    public static Properties getDefault() {
        return DEFAULT;
    }

    public static String getDefault(String key) {
        key = key.startsWith("rosace.") ? key : "rosace." + key;
        return DEFAULT.getProperty(key);
    }
}
