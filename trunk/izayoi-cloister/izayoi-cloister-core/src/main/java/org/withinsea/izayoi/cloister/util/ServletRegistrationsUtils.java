/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF
 *
 * ANY KIND, either express or implied. See the License for the specific language governing rights and
 *
 * limitations under the License.
 *
 * The Original Code is the IZAYOI web framework.
 *
 * The Initial Developer of the Original Code is
 *
 *   Mo Chen <withinsea@gmail.com>
 *
 * Portions created by the Initial Developer are Copyright (C) 2009-2010
 * the Initial Developer. All Rights Reserved.
 */

package org.withinsea.izayoi.cloister.util;

import org.withinsea.izayoi.commons.util.IOUtils;
import org.withinsea.izayoi.commons.util.LazyLinkedHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-25
 * Time: 18:35:40
 */
public class ServletRegistrationsUtils {

    public static interface Helper {
        Set<String> getServletMappingPatterns(ServletContext servletContext);
    }

    protected static class Servlet3Helper implements Helper {
        public Set<String> getServletMappingPatterns(ServletContext servletContext) {
            Set<String> set = new HashSet<String>();
            for (ServletRegistration r : servletContext.getServletRegistrations().values()) {
                set.addAll(r.getMappings());
            }
            return set;
        }
    }

    protected static class Servlet2Helper implements Helper {

        ServletContext servletContext;
        Set<String> set = new HashSet<String>();

        public Servlet2Helper(ServletContext servletContext) {
            this.servletContext = servletContext;
            try {
                String deployer = IOUtils.toString(servletContext.getResourceAsStream("/WEB-INF/web.xml"), "UTF-8");
                Matcher mapping = Pattern.compile("<servlet-mapping>([\\s\\S]+?)</servlet-mapping>").matcher(deployer);
                while (mapping.find()) {
                    Matcher pattern = Pattern.compile("<url-pattern>([\\s\\S]+?)</url-pattern>").matcher(mapping.group(1));
                    while (mapping.find()) {
                        set.add(pattern.group(1).trim());
                    }
                }
            } catch (Exception e) {
                // do nothing;
            }
        }

        public Set<String> getServletMappingPatterns(ServletContext servletContext) {
            return (this.servletContext == servletContext) ? set :
                    new Servlet2Helper(servletContext).getServletMappingPatterns(servletContext);
        }
    }

    protected static Map<ServletContext, Helper> HELPERS = new LazyLinkedHashMap<ServletContext, Helper>() {
        @Override
        protected ServletRegistrationsUtils.Helper createValue(ServletContext servletContext) {
            return ServletRegistrationsUtils.createHelper(servletContext);
        }
    };

    protected static Helper createHelper(ServletContext servletContext) {
        if (servletContext.getMajorVersion() >= 3) {
            return new Servlet3Helper();
        } else {
            return new Servlet2Helper(servletContext);
        }
    }

    public static Helper getHelper(ServletContext servletContext) {
        return HELPERS.get(servletContext);
    }
}
