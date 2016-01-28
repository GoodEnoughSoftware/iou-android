package com.wrongwaystudios.iou.resources;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * A transaction representing each IOU
 * @author Aaron Vontell
 * @date 1/23/16
 * @version 0.1
 */
public class Transaction implements Parcelable{

    private String id;
    private double amount = 0.00;
    private String recipientUsername = "";
    private String senderUsername = "";
    private String createdBy = "";
    private Date dueDate = null;
    private Date created = null;
    private Date lastReminded = null;
    private IOUStatus iouStatus;
    private String note = "";

    public Transaction(String id, double amount, String recipientUsername, String senderUsername, Date dueDate,
                       IOUStatus iouStatus, String note, Date created, String createdBy, Date lastReminded){

        this.id = id;
        this.amount = amount;
        this.recipientUsername = recipientUsername;
        this.senderUsername = senderUsername;
        this.dueDate = dueDate;
        this.iouStatus = iouStatus;
        this.note = note;
        this.created = created;
        this.createdBy = createdBy;
        this.lastReminded = lastReminded;

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

    public Date getCreated() {
        return created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getLastReminded() {
        return lastReminded;
    }

    protected Transaction(Parcel in) {
        id = in.readString();
        amount = in.readDouble();
        recipientUsername = in.readString();
        senderUsername = in.readString();
        createdBy = in.readString();
        long tmpDueDate = in.readLong();
        dueDate = tmpDueDate != -1 ? new Date(tmpDueDate) : null;
        long tmpCreated = in.readLong();
        created = tmpCreated != -1 ? new Date(tmpCreated) : null;
        long tmpLastReminded = in.readLong();
        lastReminded = tmpLastReminded != -1 ? new Date(tmpLastReminded) : null;
        iouStatus = (IOUStatus) in.readValue(IOUStatus.class.getClassLoader());
        note = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(amount);
        dest.writeString(recipientUsername);
        dest.writeString(senderUsername);
        dest.writeString(createdBy);
        dest.writeLong(dueDate != null ? dueDate.getTime() : -1L);
        dest.writeLong(created != null ? created.getTime() : -1L);
        dest.writeLong(lastReminded != null ? lastReminded.getTime() : -1L);
        dest.writeValue(iouStatus);
        dest.writeString(note);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

}
