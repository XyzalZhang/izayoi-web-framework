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
