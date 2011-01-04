package org.withinsea.izayoi.cloister.core.impl.script;

import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.common.util.IOUtils;

import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-3
 * Time: 上午8:39
 */
public abstract class StaticTxtData extends StaticData {

    abstract protected Object load(String txt) throws IOException;

    protected String encoding;

    protected StaticTxtData(String encoding) {
        this.encoding = encoding;
    }

    @Override
    protected Object load(Environment.Codefile postscript) throws IOException {
        return load(IOUtils.toString(postscript.getInputStream(), encoding));
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
