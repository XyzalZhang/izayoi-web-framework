package org.withinsea.izayoi.cloister.core.kernal;

import org.withinsea.izayoi.cloister.core.exception.CloisterRuntimeException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-7
 * Time: 下午5:38
 */
public class CloisterConfig {

    protected static final Properties DEFAULT = new Properties(); static {
        try {
            DEFAULT.load(new InputStreamReader(CloisterConfig.class.getResourceAsStream("default.properties"), "UTF-8"));
        } catch (IOException e) {
            throw new CloisterRuntimeException("failed in loading default configuration.");
        }
    }

    public static Properties getDefault() {
        return DEFAULT;
    }
}
