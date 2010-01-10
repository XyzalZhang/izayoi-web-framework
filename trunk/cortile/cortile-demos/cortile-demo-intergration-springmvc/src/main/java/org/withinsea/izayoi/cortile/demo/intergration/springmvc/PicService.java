package org.withinsea.izayoi.cortile.demo.intergration.springmvc;

import org.springframework.stereotype.Service;

/**
 * User: caixin
 * Date: 2010-1-10
 * Time: 12:56:25
 */
@Service
public class PicService {

    public String getUrl(String picid) {
        return "http://img.sina.com/" + picid;
    }
}
