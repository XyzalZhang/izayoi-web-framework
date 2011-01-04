package org.withinsea.izayoi.cloister.core.feature.overlay;

import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.common.util.Vars;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-23
 * Time: 上午10:02
 */
public class OverlaysEnvironment implements Environment {

    protected List<Environment> overlays = new ArrayList<Environment>();
    protected Vars attributes = new Vars();

    public OverlaysEnvironment() {
        this(Collections.<Environment>emptyList());
    }

    public OverlaysEnvironment(Environment... overlays) {
        this(Arrays.asList(overlays));
    }

    public OverlaysEnvironment(List<Environment> overlays) {
        this.overlays.addAll(overlays);
    }

    @Override
    public boolean exist(String path) {
        for (Environment overlay : overlays) {
            if (overlay.exist(path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isFolder(String path) {
        for (Environment overlay : overlays) {
            if (overlay.exist(path)) {
                return overlay.isFolder(path);
            }
        }
        return false;
    }

    @Override
    public Codefile getCodefile(String path) {
        for (Environment overlay : overlays) {
            if (overlay.exist(path)) {
                return overlay.getCodefile(path);
            }
        }
        return null;
    }

    @Override
    public List<Codefile> listCodefiles(String folderPath, String nameRegex) {
        List<Codefile> codefiles = new ArrayList<Codefile>();
        for (Environment overlay : overlays) {
            for (Codefile codefile : overlay.listCodefiles(folderPath, nameRegex)) {
                codefiles.add(new WrappedCodefile(codefile));
            }
        }
        return codefiles;
    }

    public List<Environment> getOverlays() {
        return overlays;
    }

    public void setOverlays(List<Environment> overlays) {
        this.overlays = overlays;
    }

    @Override
    public Vars getAttributes() {
        return attributes;
    }

    protected class WrappedCodefile implements Codefile {

        protected Codefile wrappedCodefile;

        public WrappedCodefile(Codefile wrappedCodefile) {
            this.wrappedCodefile = wrappedCodefile;
        }

        @Override
        public Environment getEnvironment() {
            return OverlaysEnvironment.this;
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
            return wrappedCodefile.getPath();
        }

        @Override
        public boolean isFolder() {
            return wrappedCodefile.isFolder();
        }
    }
}
