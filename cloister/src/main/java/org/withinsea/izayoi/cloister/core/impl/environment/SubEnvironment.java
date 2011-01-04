package org.withinsea.izayoi.cloister.core.impl.environment;

import org.withinsea.izayoi.cloister.core.exception.CloisterRuntimeException;
import org.withinsea.izayoi.cloister.core.kernal.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-23
 * Time: 上午2:09
 */
public class SubEnvironment extends AttributesSupport implements Environment {

    protected Environment baseEnvironment;
    protected String subFolderPath;

    public SubEnvironment(Environment baseEnvironment, String subFolderPath) {
        this.baseEnvironment = baseEnvironment;
        this.subFolderPath = subFolderPath;
        if (!subFolderPath.endsWith("/")) {
            throw new CloisterRuntimeException("invalid folder (must be end with /): " + subFolderPath);
        }
    }

    @Override
    public boolean exist(String path) {
        return baseEnvironment.exist(trimmedSubFolderPath() + path);
    }

    @Override
    public boolean isFolder(String path) {
        return baseEnvironment.exist(trimmedSubFolderPath() + path);
    }

    @Override
    public Codefile getCodefile(String path) {
        return baseEnvironment.getCodefile(trimmedSubFolderPath() + path);
    }

    @Override
    public List<Codefile> listCodefiles(String folderPath, String nameRegex) {
        List<Codefile> codefiles = new ArrayList<Codefile>();
        for (Codefile codefile: baseEnvironment.listCodefiles(null, Pattern.quote(trimmedSubFolderPath()) + nameRegex)) {
            codefiles.add(new WrappedCodefile(codefile));
        }
        return codefiles;
    }

    protected String trimmedSubFolderPath() {
        return subFolderPath.substring(subFolderPath.length() - 1);
    }

    public Environment getBaseEnvironment() {
        return baseEnvironment;
    }

    public void setBaseEnvironment(Environment baseEnvironment) {
        this.baseEnvironment = baseEnvironment;
    }

    public String getSubFolderPath() {
        return subFolderPath;
    }

    public void setSubFolderPath(String subFolderPath) {
        this.subFolderPath = subFolderPath;
    }

    protected class WrappedCodefile implements Codefile {

        protected Codefile wrappedCodefile;

        public WrappedCodefile(Codefile wrappedCodefile) {
            this.wrappedCodefile = wrappedCodefile;
        }

        @Override
        public Environment getEnvironment() {
            return SubEnvironment.this;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return wrappedCodefile.getInputStream();
        }

        @Override
        public long getLastModified() {
            return wrappedCodefile.getLastModified();
        }

        @Override
        public String getPath() {
            return wrappedCodefile.getPath().substring(trimmedSubFolderPath().length());
        }

        @Override
        public boolean isFolder() {
            return wrappedCodefile.isFolder();
        }
    }
}
