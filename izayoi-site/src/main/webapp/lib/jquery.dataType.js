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

(function ($) {

    var _httpData = $.httpData;

    var dataParsers = {};

    $.extend({

        dataType: function (type, dataType) {
            var dataTypes = $.ajaxSettings.accepts;
            if (!type) {
                return dataTypes;
            } else if (typeof type == 'string') {
                if (!dataType) {
                    return dataTypes[type];
                } else {
                    dataTypes[type] = dataType;
                }
            } else {
                $.extend(dataTypes, type);
            }
        },

        dataParser: function (type, parser) {
            if (!type) {
                return dataParsers;
            } else if (!parser || !(parser instanceof Function)) {
                return dataParsers[type];
            } else {
                dataParsers[type] = parser;
            }
        },

        httpData: function (xhr, type, s) {
            if (type && $.dataParser(type)) {
                return $.dataParser(type)(xhr, type, s);
            } else {
                var dataTypes = $.ajaxSettings.accepts;
                var contentTypes = $.trim(xhr.getResponseHeader('content-type').replace(/;.+/g, '')).split(/\s*,\s*/);
                for (var i = 0; i < contentTypes.length; i++) {
                    for (var t in dataTypes) {
                        if (dataTypes[t].indexOf(contentTypes[i]) >= 0 && $.dataParser(t)) {
                            return $.dataParser(t)(xhr, type, s);
                        }
                    }
                }
            }
            return _httpData(xhr, type, s);
        }

    });

})(jQuery);

(function ($) {

    $.dataType('properties', 'text/properties');

    $.dataParser('properties', function (xhr) {
        var text = xhr.responseText;
        var props = { };
        var name = null, multi = false;
        $.each($.trim(text).split(/[\r\n]+/g), function (i, line) {
            if (line.charAt(0) != '#') {
                var value = line.replace(/\\\s*$/, '');
                var nextmulti = !!line.match(/.*\\\s*$/);
                if (!!line.match(/^[\w\.]+\s*=/)) {
                    var split = $.trim(value).split(/\s*=\s*/, 2);
                    name = split[0],value = $.trim(split[1]);
                    props[name] = value;
                    if (value != '' && nextmulti) {
                        props[name] += '\n';
                    }
                } else if (multi) {
                    props[name] += value;
                    if (nextmulti) {
                        props[name] += '\n';
                    }
                }
                multi = nextmulti;
            }
        });
        return props;
    });

})(jQuery);
