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

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: caixin
 * Date: 2010-1-10
 * Time: 12:48:13
 */
@Service
public class UserService {

    public User getById(int id) {
        User ret = new User(id, "name:" + id, true, id + ".jpg");
        return ret;
    }

    public List<User> findUsers(int pageNumber, int pageSize) {
        List<User> ret = new ArrayList<User>();
        for (int i = 0; i < pageSize; i++) {
            ret.add(new User((pageNumber + i), "name:" + (pageNumber + i), true, (pageNumber + i) + ".jpg"));
        }
        return ret;
    }

}