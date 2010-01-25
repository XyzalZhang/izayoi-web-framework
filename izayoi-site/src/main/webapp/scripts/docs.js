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

    var LOADING = '<p>loading...</p>';
    var NOT_FOUND = '<h2>Under construction<sup><br />I promise it finished. someday.</sup></h2>';

    var FORMAT_TYPES = {
        'js': 'javascript',
        'json': 'javascript',
        'xml': 'xml',
        'html': 'html',
        'jsp' : 'java',
        'properties' : 'plain'
    };

    var breadcrumb = $('.breadcrumb');
    var catalog = $('#doc_catalog');
    var content = $('#doc_content');

    var select = function (hash) {
        $('.selected', catalog).removeClass('selected');
        var a = $('a[href=#' + hash + ']', catalog).addClass('selected');
        var path = [];
        do {
            path.push({ text: a.text(), href: a.attr('href') });
            a = a.parent().parent().parent().find('> a:eq(0)');
        } while (a.length > 0);
        breadcrumb.html('<a href="index.html">izayoi web framework</a> &gt;\n' +
                        '<a href="docs.html">documentation</a> &gt;\n');
        for (var i = path.length - 1; i > 0; i--) {
            breadcrumb.html(breadcrumb.html() + '<a href="' + path[i].href + '">' + path[i].text + '</a> &gt;\n');
        }
        breadcrumb.html(breadcrumb.html() + '<span>' + path[0].text + '</span>');
    }

    var failed = function () {
        content.html(NOT_FOUND);
    };

    $('a:last-child', catalog).css({ 'font-weight': 'normal' });
    $('a', catalog).click(function () {
        select($(this).attr('href').replace(/^#/, ''));
    });

    var pageload = function (hash) {

        hash = hash || 'faq';
        select(hash);

        $.ajax({
            type: 'GET',
            url: 'docs/' + hash + '.html',
            dataType: 'text',
            error: failed,
            timeout: failed,
            success: function (data) {
                if (data.length == 0) {
                    failed();
                } else {
                    content.html(data);
                    $('pre.code > code[src]', content).each(function () {
                        var code = $(this), pre = code.parent();
                        var codesrc = code.attr('src');
                        var xhr = $.get(codesrc, function () {
                            // godaddy trick start
                            /*
                             var data = xhr.responseText;
                             var loc = data.indexOf('</iframe');
                             if (loc >= 0) {
                             data = data.substring(0, loc);
                             }
                             */
                            // godaddy trick end
                            var content = data.replace(/</g, '&lt;').replace(/>/g, '&gt;');
                            var type = FORMAT_TYPES[codesrc.replace(/^.*\./, '')] || 'plain';
                            $('<pre class="code"><code>' + content + '</code></pre>').insertAfter(pre).beautifyCode(type);
                            pre.remove();
                        }, 'text');
                    });
                }
            }
        });

    };

    $.historyInit(pageload);

    if (window.location.toString().indexOf('#') < 0) {
        pageload();
    }

});
