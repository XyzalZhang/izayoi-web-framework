package org.withinsea.izayoi.cloister.web.kernal;

import org.withinsea.izayoi.cloister.core.exception.CloisterRuntimeException;
import org.withinsea.izayoi.cloister.core.kernal.CloisterConfig;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 下午4:14
 */
public class CloisterWebConfig {

    protected static final Properties DEFAULT = new Properties(); static {
        try {
            DEFAULT.putAll(CloisterConfig.getDefault());
            DEFAULT.load(new InputStreamReader(CloisterWebConfig.class.getResourceAsStream("default.properties"), "UTF-8"));
        } catch (IOException e) {
            throw new CloisterRuntimeException("failed in loading default configuration.");
        }
    }

    public static Properties getDefault(ServletContext servletContext) {
        Properties webDefault = new Properties();
        webDefault.putAll(DEFAULT);
        try {
            InputStream is = servletContext.getResourceAsStream("/WEB-INF/cloister.properties");
            if (is != null) {
                webDefault.load(new InputStreamReader(is, "UTF-8"));
            }
        } catch (IOException e) {
            throw new CloisterRuntimeException("failed in loading default configuration.");
        }
        return webDefault;
    }
}
