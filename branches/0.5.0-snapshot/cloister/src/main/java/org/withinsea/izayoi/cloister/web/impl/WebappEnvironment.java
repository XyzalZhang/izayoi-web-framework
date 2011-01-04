package org.withinsea.izayoi.cloister.web.impl;

import org.withinsea.izayoi.cloister.core.impl.environment.FolderEnvironment;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 上午10:32
 */
public class WebappEnvironment extends FolderEnvironment {

    protected ServletContext servletContext;

    public WebappEnvironment(ServletContext servletContext) {
        super(new File(servletContext.getRealPath("/").replace("%20", " ")));
        this.servletContext = servletContext;
    }
}
