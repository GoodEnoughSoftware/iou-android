package com.wrongwaystudios.iou.resources;

/**
 * A transaction representing each IOU
 * @author Aaron Vontell
 * @date 1/23/16
 * @version 0.1
 */
public class Transaction {

    private double amount = 0.00;
    private String recipientId = "";
    private String senderId = "";
    private String recipientName = "";
    private String senderName = "";

    public Transaction(double amount, String recipientId, String senderId){

        this.amount = amount;
        this.recipientId = recipientId;
        this.senderId = senderId;

    }



}
