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

package org.withinsea.izayoi.demo.samples.intergration.springmvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: caixin
 * Date: 2010-1-11
 * Time: 11:28:33
 */
@Controller
public class RestSampleController {

    @RequestMapping("/user/{id}.html")
    @ResponseBody
    public String index(@PathVariable Integer id) {
        return "user index" + id;
    }

    //静态的index匹配优先级比 {blogid} 高

    @RequestMapping("/user/{userid}/blog/index.html")
    public String firstBlogDetail(@PathVariable int userid) {
        return String.format("first user[%s] blog detail\r\n", userid);
    }

    @RequestMapping("/user/{userid}/blog/{blogid}.html")
    public String blogDetail(@PathVariable int userid, @PathVariable String blogid) {
        return String.format("user[%s] blog[%s] detail\r\n", userid, blogid);
    }

    @RequestMapping("/user/{userid}/blogs/index.html")
    @ResponseBody
    public String blogList(@PathVariable Integer userid) {
        return String.format("user[%s] blog index\r\n", userid);
    }

    @RequestMapping("/user/{userid}/blogs/{page}.html")
    @ResponseBody
    public String blogList(@PathVariable int userid, @RequestParam(required = false, defaultValue = "1") Integer page) {
        return String.format("user[%s] list at page[%s]\r\n", userid, page);
    }

}
