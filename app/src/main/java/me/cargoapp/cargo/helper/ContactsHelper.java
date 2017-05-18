package me.cargoapp.cargo.helper;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

import me.cargoapp.cargo.navui.adapter.ContactsAdapter;

/**
 * Created by Marvin on 18/05/2017.
 */

public class ContactsHelper {
    public static ArrayList<ContactsAdapter.ContactRepresentation> getStarred(Context context) {
        Cursor contactsCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "starred=1", null, null);
        final ArrayList<ContactsAdapter.ContactRepresentation> contacts = new ArrayList<>(contactsCursor.getColumnCount());
        while (contactsCursor.moveToNext()) {
            ContactsAdapter.ContactRepresentation representation = new ContactsAdapter.ContactRepresentation();
            representation.name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            representation.phoneNumber = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            representation.photoUri = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            contacts.add(representation);
        }
        contactsCursor.close();

        return contacts;
    }
}
