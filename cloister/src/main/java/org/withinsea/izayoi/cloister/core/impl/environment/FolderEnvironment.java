package org.withinsea.izayoi.cloister.core.impl.environment;

import org.withinsea.izayoi.cloister.core.exception.CloisterRuntimeException;
import org.withinsea.izayoi.cloister.core.kernal.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-23
 * Time: 上午4:13
 */
public class FolderEnvironment extends AttributesSupport implements Environment {

    protected File folder;

    public FolderEnvironment(File folder) {
        this.folder = folder;
        if (!folder.isDirectory()) {
            throw new CloisterRuntimeException("invalid folder: " + folder);
        }
    }

    @Override
    public boolean exist(String path) {
        File file = new File(folder, path);
        return file.exists();
    }

    @Override
    public boolean isFolder(String path) {
        File file = new File(folder, path);
        return file.exists() && file.isDirectory();
    }

    @Override
    public Codefile getCodefile(String path) {
        File file = new File(folder, path);
        return !file.exists() ? null : new FileCodefile(path, file);
    }

    @Override
    public List<Codefile> listCodefiles(String folderPath, final String nameRegex) {
        if (!isFolder(folderPath)) {
            return Collections.emptyList();
        } else {
            List<Codefile> codefiles = new ArrayList<Codefile>();
            for (File file : new File(folder, folderPath).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches(nameRegex);
                }
            })) {
                codefiles.add(new FileCodefile(folderPath + file.getName(), file));
            }
            return codefiles;
        }
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    protected class FileCodefile implements Codefile {

        protected String path;
        protected File file;

        public FileCodefile(String path, File file) {
            this.path = path.replaceAll("/+$", "") + (file.isDirectory() ? "/" : "");
            this.file = file;
        }

        @Override
        public Environment getEnvironment() {
            return FolderEnvironment.this;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public boolean isFolder() {
            return file.isDirectory();
        }

        @Override
        public long getLastModified() {
            return file.lastModified();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(file);
        }
    }
}
