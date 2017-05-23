package me.cargoapp.cargo.helper

import android.content.Context
import android.provider.ContactsContract
import me.cargoapp.cargo.navui.adapter.ContactsAdapter
import java.util.*

/**
 * Created by Marvin on 18/05/2017.
 */

object ContactsHelper {
    fun getStarred(context: Context): ArrayList<ContactsAdapter.ContactRepresentation> {
        val contactsCursor = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "starred=1", null, null)
        val contacts = ArrayList<ContactsAdapter.ContactRepresentation>(contactsCursor!!.columnCount)
        while (contactsCursor.moveToNext()) {
            val representation = ContactsAdapter.ContactRepresentation()
            representation.name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            representation.phoneNumber = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            representation.photoUri = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))

            contacts.add(representation)
        }
        contactsCursor.close()

        return contacts
    }
}
