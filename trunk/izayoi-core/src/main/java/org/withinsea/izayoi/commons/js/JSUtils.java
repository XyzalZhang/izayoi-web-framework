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

package org.withinsea.izayoi.commons.js;

import org.withinsea.izayoi.commons.util.IOUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 8:11:47
 */
public class JSUtils {

    @SuppressWarnings("unchecked")
    public static <T> T js2java(Object jsObj) throws ScriptException, NoSuchMethodException {
        return (T) ScriptHelper.JS2JAVA.invokeFunction("js2java", jsObj);
    }

    @SuppressWarnings("unchecked")
    public static <T> T json2java(String json) throws ScriptException, NoSuchMethodException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
        return (T) js2java(engine.eval("(" + json + ")"));
    }

    // lazy load Script Engine

    protected static class ScriptHelper {

        protected static final Invocable JS2JAVA; static {
            try {
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
                String script = IOUtils.toString(JSUtils.class.getResourceAsStream("js2java.js"), "UTF-8");
                engine.eval(script);
                JS2JAVA = (Invocable) engine;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* from: org.json, org.json.JSONObject.quote(String string) */

    public static String quote(String string) {

        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char b;
        char c = 0;
        int i;
        int len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        String t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    if (b == '<') {
                        sb.append('\\');
                    }
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                            || (c >= '\u2000' && c < '\u2100')) {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
