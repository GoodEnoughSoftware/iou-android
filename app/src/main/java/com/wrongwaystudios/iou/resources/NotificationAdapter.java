package com.wrongwaystudios.iou.resources;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wrongwaystudios.iou.R;

/**
 * Adapter that handles the display of notifications
 * @author Aaron Vontell
 * @date 1/22/16
 * @version 0.1
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private UserNotification[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View notifView;
        public ViewHolder(View v) {
            super(v);
            notifView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotificationAdapter(UserNotification[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ((TextView) holder.notifView.findViewById(R.id.notification_label)).setText(mDataset[position].getMessage());
        holder.notifView.findViewById(R.id.notification_ind).setVisibility(View.VISIBLE);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
