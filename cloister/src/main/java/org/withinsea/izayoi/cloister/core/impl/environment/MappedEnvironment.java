package org.withinsea.izayoi.cloister.core.impl.environment;

import org.withinsea.izayoi.cloister.core.exception.CloisterRuntimeException;
import org.withinsea.izayoi.cloister.core.kernal.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-23
 * Time: 上午10:37
 */
public class MappedEnvironment extends AttributesSupport implements Environment {

    protected Environment baseEnvironment;
    protected String mappedFolderPath;

    public MappedEnvironment(Environment baseEnvironment, String mappedFolderPath) {
        this.baseEnvironment = baseEnvironment;
        this.mappedFolderPath = mappedFolderPath;
        if (!mappedFolderPath.endsWith("/")) {
            throw new CloisterRuntimeException("invalid folder (must be end with /): " + mappedFolderPath);
        }
    }

    @Override
    public boolean exist(String path) {
        String prefix = trimmedMappedFolderPath();
        return path.startsWith(prefix) && baseEnvironment.exist(path.substring(prefix.length()));
    }

    @Override
    public boolean isFolder(String path) {
        String prefix = trimmedMappedFolderPath();
        return path.startsWith(prefix) && baseEnvironment.isFolder(path.substring(prefix.length()));
    }

    @Override
    public Codefile getCodefile(String path) {
        String prefix = trimmedMappedFolderPath();
        if (!path.startsWith(prefix)) {
            return null;
        } else {
            return baseEnvironment.getCodefile(path.substring(prefix.length()));
        }
    }

    @Override
    public List<Codefile> listCodefiles(String folderPath, String nameRegex) {
        String prefix = trimmedMappedFolderPath();
        if (!folderPath.startsWith(prefix)) {
            return Collections.emptyList();
        } else {
            List<Codefile> codefiles = new ArrayList<Codefile>();
            for (Codefile codefile : baseEnvironment.listCodefiles(folderPath.substring(prefix.length()), nameRegex)) {
                codefiles.add(new WrappedCodefile(codefile));
            }
            return codefiles;
        }
    }

    protected String trimmedMappedFolderPath() {
        return mappedFolderPath.substring(mappedFolderPath.length() - 1);
    }

    protected class WrappedCodefile implements Codefile {

        protected Codefile wrappedCodefile;

        public WrappedCodefile(Codefile wrappedCodefile) {
            this.wrappedCodefile = wrappedCodefile;
        }

        @Override
        public Environment getEnvironment() {
            return MappedEnvironment.this;
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
            return trimmedMappedFolderPath() + wrappedCodefile.getPath();
        }

        @Override
        public boolean isFolder() {
            return wrappedCodefile.isFolder();
        }
    }
}
