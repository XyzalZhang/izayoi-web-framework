package org.withinsea.izayoi.cloister.core.kernal;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-13
 * Time: 上午4:34
 */
public interface Environment {

    interface Codefile {

        Environment getEnvironment();

        String getPath();

        boolean isFolder();

        long getLastModified();

        InputStream getInputStream() throws IOException;
    }

    boolean exist(String path);

    boolean isFolder(String path);

    Codefile getCodefile(String path);

    List<Codefile> listCodefiles(String folderPath, String nameRegex);

    Map<String, Object> getAttributes();
}
