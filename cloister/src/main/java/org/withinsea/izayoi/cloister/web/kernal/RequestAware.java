package org.withinsea.izayoi.cloister.web.kernal;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 上午11:43
 */
public interface RequestAware {

    HttpServletRequest getHttpServletRequest();
    HttpServletResponse getHttpServletResponse();
    FilterChain getFilterChain();
}
