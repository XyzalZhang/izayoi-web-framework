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

$(function () {


    // external links

    $('a[href^=http://], a[href^=https://], a[href^=ftp://]').addClass('external').attr('target', "_blank");
    $('a[href^=mailto:]').addClass('mailto');


    // code beautify

    var FORMAT_TYPES = {
        'js': 'javascript',
        'json': 'javascript',
        'xml': 'xml',
        'html': 'html',
        'jsp' : 'java'
    };

    $('pre.code > code[src]').each(function () {
        var code = $(this), pre = code.parent();
        var split = code.attr('src').split(':');
        var type = FORMAT_TYPES[split[1].toString().replace(/^.*\./, '')] || 'plain';
        $.get(split[0], {}, function (props) {
            var content = props[split[1]].replace(/</g, '&lt;').replace(/</g, '&gt;');
            $('<pre class="code"><code>' + content + '</code></pre>').insertAfter(pre).beautifyCode(type);
            pre.remove();
        }, 'properties');
    });
});
