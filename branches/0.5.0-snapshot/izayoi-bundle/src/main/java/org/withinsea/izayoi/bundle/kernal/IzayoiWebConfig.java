package org.withinsea.izayoi.bundle.kernal;

import org.withinsea.izayoi.cloister.core.exception.CloisterRuntimeException;
import org.withinsea.izayoi.cloister.core.kernal.CloisterConfig;
import org.withinsea.izayoi.cloister.web.kernal.CloisterWebConfig;
import org.withinsea.izayoi.rosace.core.kernel.RosaceConfig;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-29
 * Time: 下午4:25
 */
public class IzayoiWebConfig {

    protected static Properties DEFAULT = new Properties(); static {
        try {
            DEFAULT.putAll(RosaceConfig.getDefault());
            DEFAULT.putAll(CloisterConfig.getDefault());
            DEFAULT.load(new InputStreamReader(IzayoiWebConfig.class.getResourceAsStream("default.properties"), "UTF-8"));
        } catch (IOException e) {
            throw new CloisterRuntimeException("failed in loading default configuration.");
        }
    }

    public static Properties getDefault(ServletContext servletContext) {
        Properties webDefault = new Properties();
        webDefault.putAll(DEFAULT);
        webDefault.putAll(CloisterWebConfig.getDefault(servletContext));
        try {
            InputStream is = servletContext.getResourceAsStream("/WEB-INF/izayoi.properties");
            if (is != null) {
                webDefault.load(new InputStreamReader(is, "UTF-8"));
            }
        } catch (IOException e) {
            throw new CloisterRuntimeException("failed in loading default configuration.");
        }
        return webDefault;
    }
}
