package org.withinsea.izayoi.cloister.core.impl.dispatcher;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-4
 * Time: 上午10:33
 */
public class HiddenRequest extends CloisterException {

    protected String path;

    public HiddenRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
