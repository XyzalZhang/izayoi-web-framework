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

package org.withinsea.izayoi.commons.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 14:24:07
 */
public class StringUtils {

    public static interface Replace {
        public String replace(String... groups);
    }

    public static interface Transform {
        public String transform(String str);
    }

    protected static final Transform RET_TRANSFORM = new Transform() {
        @Override
        public String transform(String str) {
            return str;
        }
    };

    public static String replaceAll(String str, String regexp, String replace) {
        return str.replaceAll(regexp, replace);
    }

    public static String replaceAll(String str, String regexp, int flags, String replace) {
        return Pattern.compile(regexp, flags).matcher(str).replaceAll(replace);
    }

    public static String replaceAll(String str, String regexp, Replace replace) {
        return replaceAll(str, regexp, 0, replace);
    }

    public static String replaceAll(String str, String regexp, int flags, Replace replace) {
        return replaceAll(str, regexp, flags, replace, RET_TRANSFORM);
    }

    public static String replaceAll(String str, String regexp, Replace replace, Transform transformElse) {
        return replaceAll(str, regexp, 0, replace, transformElse);
    }

    public static String replaceAll(String str, String regexp, int flags,
                                    Replace replaceGroups, Transform transformElse) {

        StringBuffer buf = new StringBuffer();

        Pattern pattern = Pattern.compile(regexp, flags);
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            int count = matcher.groupCount();
            String[] groups = new String[count + 1];
            groups[0] = matcher.group();
            for (int i = 1; i <= count; i++) {
                groups[i] = matcher.group(i);
            }
            StringBuffer tbuf = new StringBuffer();
            matcher.appendReplacement(tbuf, "");
            buf.append(transformElse.transform(tbuf.toString()));
            buf.append(replaceGroups.replace(groups));
        }
        StringBuffer tbuf = new StringBuffer();
        matcher.appendTail(tbuf);
        buf.append(transformElse.transform(tbuf.toString()));

        return buf.toString();
    }

    public static String join(String splitter, String... strs) {
        return join(splitter, Arrays.asList(strs));
    }

    public static String join(String splitter, Collection<String> strs) {
        if (strs.size() == 0) {
            return "";
        }
        StringBuffer buf = new StringBuffer();
        for (String str : strs) {
            buf.append(str).append(splitter);
        }
        return buf.substring(0, buf.length() - splitter.length());
    }
}