package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.yoga.android.YogaLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Locale;

import me.cargoapp.cargo.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Mathieu on 05/05/2017.
 */
@EFragment(R.layout.fragment_navui_message)
public class NavuiMessage extends Fragment {

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @ViewById(R.id.grid_view)
    GridView _gridView;

    String _numberToSendTo;

    @AfterViews
    void afterViews() {
        Cursor contactsCursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "starred=1", null, null);
        final ArrayList<ContactRepresentation> contacts = new ArrayList<>(contactsCursor.getColumnCount());
        while (contactsCursor.moveToNext()) {
            ContactRepresentation representation = new ContactRepresentation();
            representation.name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            representation.phoneNumber = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            representation.photoUri = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            contacts.add(representation);
        }
        contactsCursor.close();

        _gridView.setAdapter(new ContactsAdapter(getActivity(), contacts));

        _gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                _numberToSendTo = contacts.get(position).phoneNumber;
                askForContent();
            }
        });
    }

    private void askForContent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Votre message");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(_numberToSendTo, null, result.get(0), null, null);
                }
                break;
            }

        }
    }

    public static class ContactRepresentation {
        public String photoUri;
        public String name;
        public String phoneNumber;
    }

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
            image.setImageURI(Uri.parse(_contacts.get(position).photoUri));

            return layout;
        }
    }
}
