package org.withinsea.izayoi.cloister.core.feature.dispatcher;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-26
 * Time: 上午8:56
 */
public class Dispatching extends CloisterException {

    protected static Dispatching FINISH = new Dispatching(null, true, true, false);

    protected String targetPath;
    protected boolean finish = false;
    protected boolean redirect = false;
    protected boolean forward = false;

    public static Dispatching include(String path) {
        return new Dispatching(path, false, false, false);
    }

    public static Dispatching redirect(String path) {
        return new Dispatching(path, false, true, false);
    }

    public static Dispatching forward(String path) {
        return new Dispatching(path, true, false, true);
    }

    public static Dispatching finish() {
        return FINISH;
    }

    protected Dispatching(String targetPath, boolean finish, boolean redirect, boolean forward) {
        this.targetPath = targetPath;
        this.finish = finish;
        this.redirect = redirect;
        this.forward = forward;
    }

    public boolean isFinish() {
        return finish;
    }

    public boolean isForward() {
        return forward;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public String getTargetPath() {
        return targetPath;
    }
}
