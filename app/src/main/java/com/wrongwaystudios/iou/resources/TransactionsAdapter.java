package com.wrongwaystudios.iou.resources;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wrongwaystudios.iou.R;

import java.util.ArrayList;

/**
 * Adapter that handles the display of ious
 * @author Aaron Vontell
 * @date 1/22/16
 * @version 0.1
 */
public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder>{

    private ArrayList<Transaction> mDataset;
    private IOUStatus desiredStatus;
    private int layoutId;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View transactionView;
        public ViewHolder(View v) {
            super(v);
            transactionView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TransactionsAdapter(ArrayList<Transaction> myDataset, int layoutId) {
        mDataset = myDataset;
        this.layoutId = layoutId;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TransactionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TextView senderView = (TextView) holder.transactionView.findViewById(R.id.sender_text);
        TextView recipientView = (TextView) holder.transactionView.findViewById(R.id.recipient_text);
        TextView statusView = (TextView) holder.transactionView.findViewById(R.id.status_text);
        TextView amountView = (TextView) holder.transactionView.findViewById(R.id.amount_text);
        ImageView dueIcon = (ImageView) holder.transactionView.findViewById(R.id.due_icon);
        ImageView importantIcon = (ImageView) holder.transactionView.findViewById(R.id.important_icon);
        ImageView noteIcon = (ImageView) holder.transactionView.findViewById(R.id.note_icon);
        FrameLayout checkFrame = (FrameLayout) holder.transactionView.findViewById(R.id.check_frame);
        FrameLayout editFrame = (FrameLayout) holder.transactionView.findViewById(R.id.edit_frame);
        FrameLayout notifyFrame = (FrameLayout) holder.transactionView.findViewById(R.id.notify_frame);

        Transaction iou = Globals.mainUser.allIOUs.get(position);

        senderView.setText(iou.getSenderUsername());
        recipientView.setText(iou.getRecipientUsername());
        statusView.setText(Globals.statusString(iou.getIouStatus()));
        amountView.setText("$" + iou.getAmount());
        dueIcon.setVisibility(iou.getDueDate() == null ? View.GONE : View.VISIBLE);
        importantIcon.setVisibility(View.GONE);
        noteIcon.setVisibility(iou.getNote() == null || iou.getNote().equals("") ? View.GONE : View.VISIBLE);

        checkFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHelper.startIouCheckOperation(position);
            }
        });
        editFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHelper.startIouEditOperation(position);
            }
        });
        notifyFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHelper.startIouNotifyOperation(position);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
