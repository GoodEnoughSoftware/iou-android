package com.wrongwaystudios.iou.resources;

/**
 * Class that represents the users obtained from the server
 * @author Aaron Vontell
 * @date 1/26/16
 * @version 0.1
 */
public class UserSearchObject {

    private String username;
    private String fullName;

    public UserSearchObject(String username, String fullName){

        this.username = username;
        this.fullName = fullName;

    }

    public String getUsername() {
        return username;
    }

    public String getFullName(){
        return fullName;
    }

    public String toString(){
        return username;
    }

}
