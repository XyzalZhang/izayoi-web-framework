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

function js2java(obj) {

    if (obj === undefined || obj === null) {
        return null;
    } else if (typeof obj == 'number' || obj instanceof Number) {
        if (Math.floor(obj) == obj) {
            return new java.lang.Integer(obj);
        } else {
            return new java.lang.Double(obj);
        }
    } else if (typeof obj == 'string' || obj instanceof String) {
        return new java.lang.String(obj);
    } else if (typeof obj == 'boolean' || obj instanceof Boolean) {
        return new java.lang.Boolean(obj);
    } else if (obj instanceof Date) {
        return new java.util.Date(obj.getTime());
    } else if (obj instanceof RegExp) {
        return java.util.regex.Pattern.compile(obj.source,
                (obj.ignoreCase ? Pattern.CASE_INSENSITIVE : 0) ||
                (obj.multiline ? Pattern.MULTILINE : 0));
    } else if (typeof obj == 'function' || obj instanceof Function) {
        return null;
    } else if (obj instanceof Array) {
        var lst = new java.util.ArrayList();
        for (var i = 0; i < obj.length; i++) {
            lst.add(arguments.callee(obj[i]));
        }
        return lst;
    } else if (!!obj.constructor && (typeof obj.constructor == 'function' || obj.constructor instanceof Function)) {
        var map = new java.util.LinkedHashMap();
        for (var k in obj) {
            map.put(new java.lang.String(k), arguments.callee(obj[k]));
        }
        return map;
    } else {
        return obj;
    }

}