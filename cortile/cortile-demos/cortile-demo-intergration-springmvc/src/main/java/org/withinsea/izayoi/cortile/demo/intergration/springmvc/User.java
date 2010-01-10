package org.withinsea.izayoi.cortile.demo.intergration.springmvc;

/**
 * User: caixin
 * Date: 2010-1-10
 * Time: 12:48:32
 */
public class User {

    int id;
    String name;
    boolean sex;
    String pictureId;

    public User() {
    }

    public User(int id, String name, boolean sex, String pictureId) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.pictureId = pictureId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }
}
