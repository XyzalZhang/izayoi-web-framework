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

package org.withinsea.izayoi.cortile.demo.intergration.springmvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-8
 * Time: 18:53:53
 */
@Controller
public class IndexController {

    @Resource
    UserService userService;

    @Resource
    PicService picService;


    @RequestMapping("/index.html")
    public ModelAndView index(ModelAndView mv) {
        mv.addObject(userService);
        mv.addObject(picService);
        mv.setViewName("/index");
        return mv;
    }

    @RequestMapping("/detail.html")
    public ModelAndView detail(@RequestParam(required = false, defaultValue = "1") Integer id,
                               ModelAndView mv) {
        User user = userService.getById(id);
        mv.addObject("user", user);
        mv.addObject(picService);
        mv.setViewName("/detail");
        return mv;
    }

    @RequestMapping("/list.html")
    public ModelAndView list(@RequestParam(required = false, defaultValue = "1") Integer pageNumber,
                             @RequestParam(required = false, defaultValue = "30") Integer pageSize,
                             ModelAndView mv) {
        List<User> userList = userService.findUsers(pageNumber, pageSize);
        mv.addObject("userList", userList);
        mv.addObject(picService);
        mv.setViewName("/list");
        return mv;
    }

}
