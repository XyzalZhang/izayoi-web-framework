package org.withinsea.izayoi.cloister.core.impl.environment;

import org.withinsea.izayoi.cloister.core.exception.CloisterRuntimeException;
import org.withinsea.izayoi.cloister.core.kernal.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-23
 * Time: 上午2:23
 */
public class ZipfileEnvironment extends AttributesSupport implements Environment {

    protected File file;
    protected ZipFile zipFile;
    protected long lastModified;

    protected Map<String, Codefile> codefiles;
    protected Map<String, List<Codefile>> folderCodefiles;

    public ZipfileEnvironment(File file) {
        this.file = file;
        update();
    }

    @Override
    public boolean exist(String path) {
        update();
        Codefile codefile = codefiles.get(path);
        return (codefile != null);
    }

    @Override
    public boolean isFolder(String path) {
        update();
        Codefile codefile = codefiles.get(path);
        return (codefile != null && codefile.isFolder());
    }

    @Override
    public Codefile getCodefile(String path) {
        update();
        return codefiles.get(path);
    }

    @Override
    public List<Codefile> listCodefiles(String folderPath, String nameRegex) {
        update();
        if (!isFolder(folderPath)) {
            return Collections.emptyList();
        } else {
            List<Codefile> matched = new ArrayList<Codefile>();
            for (Codefile codefile : folderCodefiles.get(folderPath)) {
                String name = codefile.getPath().replaceAll("^.*/", "");
                if (name.matches(nameRegex)) {
                    matched.add(codefile);
                }
            }
            return matched;
        }
    }

    protected void update() {

        try {

            if (zipFile == null || file.lastModified() > lastModified) {

                zipFile = new ZipFile(file);
                lastModified = file.lastModified();
                codefiles = new HashMap<String, Codefile>();
                folderCodefiles = new HashMap<String, List<Codefile>>();

                Enumeration<? extends ZipEntry> enu = zipFile.entries();
                while (enu.hasMoreElements()) {
                    ZipEntry zipEntry = enu.nextElement();
                    String path = zipEntry.getName();
                    codefiles.put(path, new ZipEntryCodefile(path, zipEntry));
                    if (zipEntry.isDirectory()) {
                        String folderPath = path + (path.endsWith("/") ? "" : "/");
                        folderCodefiles.put(folderPath, new ArrayList<Codefile>());
                    }
                }

                for (Codefile codefile : codefiles.values()) {
                    if (codefile.isFolder()) {
                        String folderPath = codefile.getPath().replaceAll("/.*$", "/");
                        folderCodefiles.get(folderPath).add(codefile);
                    }
                }
            }

        } catch (IOException ex) {
            throw new CloisterRuntimeException("failed in updating zip environment on " + file, ex);
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
        update();
    }

    protected class ZipEntryCodefile implements Codefile {

        protected String path;
        protected ZipEntry zipEntry;

        public ZipEntryCodefile(String path, ZipEntry zipEntry) {
            this.path = path.replaceAll("/+$", "") + (zipEntry.isDirectory() ? "/" : "");
            this.zipEntry = zipEntry;
        }

        @Override
        public Environment getEnvironment() {
            return ZipfileEnvironment.this;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public boolean isFolder() {
            return zipEntry.isDirectory();
        }

        @Override
        public long getLastModified() {
            return lastModified;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return isFolder() ? null : zipFile.getInputStream(zipEntry);
        }
    }
}
