package com.wrongwaystudios.iou.resources;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wrongwaystudios.iou.R;

/**
 * An adapter for the simple dropdown in user searches
 * @author Aaron Vontell
 * @date 1/24/16
 * @version 0.1
 */
public class UserAdapter extends ArrayAdapter<UserSearchObject> {

    private Context context;
    private int resource;
    private UserSearchObject[] objects;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public UserAdapter(Context context, int resource, UserSearchObject[] objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        this.objects = objects;

    }

    /**
     * {@inheritDoc}
     *
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(resource, null);
        }

        UserSearchObject user = objects[position];
        if(user != null){
            TextView fullNameField = (TextView) v.findViewById(R.id.fullname);
            TextView usernameField = (TextView) v.findViewById(R.id.username);
            fullNameField.setText(user.getFullName());
            usernameField.setText(user.getUsername());
        }

        return v;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return objects.length;
    }

    /**
     * {@inheritDoc}
     *
     * @param position
     */
    @Override
    public UserSearchObject getItem(int position) {
        return objects[position];
    }

}
