package com.wrongwaystudios.iou.resources;

import java.util.Date;

/**
 * A transaction representing each IOU
 * @author Aaron Vontell
 * @date 1/23/16
 * @version 0.1
 */
public class Transaction {

    private String id;
    private double amount = 0.00;
    private String recipientUsername = "";
    private String senderUsername = "";
    private Date dueDate = null;
    private IOUStatus iouStatus;
    private String note = "";

    public Transaction(String id, double amount, String recipientUsername, String senderUsername, Date dueDate,
                       IOUStatus iouStatus, String note){

        this.id = id;
        this.amount = amount;
        this.recipientUsername = recipientUsername;
        this.senderUsername = senderUsername;
        this.dueDate = dueDate;
        this.iouStatus = iouStatus;
        this.note = note;

    }

    public String getId() {
        return id;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public double getAmount() {
        return amount;
    }

    public IOUStatus getIouStatus() {
        return iouStatus;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getNote() {
        return note;
    }
}
