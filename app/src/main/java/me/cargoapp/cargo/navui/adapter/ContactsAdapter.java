package me.cargoapp.cargo.navui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.yoga.android.YogaLayout;

import java.util.ArrayList;

import me.cargoapp.cargo.R;

/**
 * Created by Marvin on 18/05/2017.
 */

public class ContactsAdapter extends BaseAdapter {
    private Context _context;

    private ArrayList<ContactRepresentation> _contacts;

    public ContactsAdapter(Context c, ArrayList<ContactRepresentation> contacts) {
        _context = c;

        _contacts = contacts;
    }

    public int getCount() {
        return _contacts.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        YogaLayout layout;
        // set the view's size, margins, paddings and layout parameters
        if (convertView == null) {
            layout = (YogaLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_navui_contact, parent, false);
        } else {
            layout = (YogaLayout) convertView;
        }

        ImageView image = (ImageView) layout.findViewById(R.id.contact_image);
        TextView text = (TextView) layout.findViewById(R.id.contact_text);

        text.setText(_contacts.get(position).name);
        image.setImageURI(Uri.parse(_contacts.get(position).photoUri != null ? _contacts.get(position).photoUri : ""));

        return layout;
    }
    public static class ContactRepresentation {
        public String photoUri;
        public String name;
        public String phoneNumber;
    }
}