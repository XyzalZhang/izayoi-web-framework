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

package org.withinsea.izayoi.core.bean;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-17
 * Time: 17:52:11
 */
public class JndiBeanSource implements BeanSource {

    @Override
    public Set<String> names() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exist(Object bean) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exist(String name) {
        return get(name) != null;
    }

    @Override
    public boolean exist(Class<?> claz) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) lookupJndi(name);
    }

    @Override
    public <T> T get(Class<T> claz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> list(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> list(Class<T> claz) {
        throw new UnsupportedOperationException();
    }

    protected static Collection<String> PREFIXES = new LinkedHashSet<String>(Arrays.asList(
            "",
            "java:module/",
            "java:app/",
            "java:global/",
            "java:comp/"
    ));

    protected static Collection<String> NAMESPACES = new LinkedHashSet<String>(Arrays.asList(
            "",
            "env/",
            "ejb/",
            "jms/"
    ));

    protected static Object lookupJndi(String name) {
        try {
            javax.naming.Context ctx = new InitialContext();
            for (String prefix : PREFIXES) {
                for (String namespace : NAMESPACES) {
                    try {
                        return ctx.lookup(prefix + namespace + name);
                    } catch (NamingException e) {
                        // do nothing
                    }
                }
            }
        } catch (NamingException e) {
            // do nothing
        }
        return null;
    }

    public static void registerPrefix(String prefix) {
        PREFIXES.add(prefix);
    }

    public static void registerNamespace(String namespace) {
        NAMESPACES.add(namespace);
    }
}
