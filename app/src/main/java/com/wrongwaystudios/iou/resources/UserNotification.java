package com.wrongwaystudios.iou.resources;

/**
 * Class to represent notifications for that user from the server
 * @author Aaron Vontell
 * @date 1/21/16
 * @version 0.1
 */
public class UserNotification {

    private String id;
    private String message;

    /**
     * Creates a notification with an id and message
     * @param id
     * @param message
     */
    public UserNotification(String id, String message){

        this.id = id;
        this.message = message;

    }

    /**
     * Creates a notification from a previous toString representation
     * @param encoded The encoded UserNotification string
     */
    public UserNotification(String encoded){

    }

    /**
     * Creates a string to save for this notification
     * @return the current notification encoded as a string
     */
    public String toString(){

        return "" + id + ":" + message;

    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
