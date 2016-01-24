package com.wrongwaystudios.iou.resources;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
