package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.yoga.android.YogaLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import me.cargoapp.cargo.R;

/**
 * Created by Mathieu on 05/05/2017.
 */
@EFragment(R.layout.fragment_navui_message)
public class NavuiMessage extends Fragment {

    @ViewById(R.id.recycler_view)
    RecyclerView _recyclerView;

    RecyclerView.Adapter _adapter;
    RecyclerView.LayoutManager _layoutManager;

    @AfterViews
    void afterViews() {
        _layoutManager = new GridLayoutManager(getContext(), 2);
        _recyclerView.setLayoutManager(_layoutManager);

        Cursor contactsCursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "starred=1", null, null);
        ArrayList<ContactRepresentation> contacts = new ArrayList<>(contactsCursor.getColumnCount());
        while (contactsCursor.moveToNext()) {
            ContactRepresentation representation = new ContactRepresentation();
            representation.contactName = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            representation.photoUri = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            contacts.add(representation);
        }
        contactsCursor.close();

        // specify an adapter (see also next example)
        _adapter = new ContactsAdapter(contacts);
        _recyclerView.setAdapter(_adapter);
    }

    public static class ContactRepresentation {
        public String photoUri;
        public String contactName;
    }

    public static class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
        private ArrayList<ContactRepresentation> _contacts;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {

            public YogaLayout _layout;


            public ViewHolder(YogaLayout v) {
                super(v);

                _layout = v;
            }
        }

        public ContactsAdapter(ArrayList<ContactRepresentation> contacts) {
            _contacts = contacts;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            YogaLayout v = (YogaLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_navui_contact, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ImageView image = (ImageView) holder._layout.findViewById(R.id.contact_image);
            TextView text = (TextView) holder._layout.findViewById(R.id.contact_text);

            text.setText(_contacts.get(position).contactName);
            image.setImageURI(Uri.parse(_contacts.get(position).photoUri));
        }

        @Override
        public int getItemCount() {
            return _contacts.size();
        }
    }
}
