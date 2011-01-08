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

package org.withinsea.izayoi.cloister.web.feature.jspscript;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-7-6
 * Time: 5:32:54
 */
class ParamUtils {

    @SuppressWarnings("unchecked")
    public static <T> T cast(T value, Class<?> type) throws ParseException {

        if (value == null) {
            return null;
        }

        Class<?> vtype = value.getClass();

        if (!vtype.isArray() && !type.isArray()) {
            if (!vtype.equals(String.class) || type.equals(String.class)) {
                return value;
            } else {
                String s = (String) value;
                Object ret = value;
                if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                    ret = Boolean.valueOf(s);
                } else if (type.equals(int.class) || type.equals(Integer.class)) {
                    ret = Integer.valueOf(s);
                } else if (type.equals(byte.class) || type.equals(Byte.class)) {
                    ret = Byte.valueOf(s);
                } else if (type.equals(long.class) || type.equals(Long.class)) {
                    ret = Long.valueOf(s);
                } else if (type.equals(float.class) || type.equals(Float.class)) {
                    ret = Float.valueOf(s);
                } else if (type.equals(double.class) || type.equals(Double.class)) {
                    ret = Double.valueOf(s);
                } else if (type.equals(Date.class)) {
                    if (s.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
                        ret = new SimpleDateFormat("yyyy-MM-dd").parse(s);
                    } else if (s.matches("[0-9]+")) {
                        ret = new Date(Long.parseLong(s));
                    }
                } else if (type.isEnum()) {
                    ret = Enum.valueOf((Class<Enum>) type, s);
                }
                return (T) ret;
            }
        }

        if (!vtype.isArray() && type.isArray()) {
            Class<?> ctype = type.getComponentType();
            Object arr = Array.newInstance(ctype, 1);
            Array.set(arr, 0, cast(value, ctype));
            return (T) arr;
        }

        if (vtype.isArray() && type.isArray()) {
            int len = Array.getLength(value);
            Class<?> ctype = type.getComponentType();
            Object arr = Array.newInstance(ctype, len);
            for (int i = 0; i < len; i++) {
                Array.set(arr, i, cast(Array.get(value, i), ctype));
            }
            return (T) arr;
        }

        return value;
    }
}
