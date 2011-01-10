package org.withinsea.izayoi.cloister.core.impl.role;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.feature.dispatcher.Dispatching;
import org.withinsea.izayoi.cloister.core.feature.postscript.RoleEngine;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-3
 * Time: 上午10:21
 */
public class DispatchingRoleEngine implements RoleEngine<String> {

    @Override
    public void process(String data, Map<String, Object> context) throws CloisterException {

        String url = data.trim();

        if (url.equals("continue")) {

        } else if (url.equals("stop")) {
            throw Dispatching.finish();
        } else if (url.equals("cancel")) {
            throw Dispatching.cancel();
        } else if (url.startsWith("forward:")) {
            url = url.substring("forward:".length()).trim();
            throw Dispatching.forward(url);
        } else if (url.startsWith("redirect:")) {
            url = url.substring("redirect:".length()).trim();
            throw Dispatching.redirect(url);
        } else {
            throw Dispatching.redirect(url);
        }
    }
}
