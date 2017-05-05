package me.cargoapp.cargo.navui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import me.cargoapp.cargo.R;

/**
 * Created by Mathieu on 05/05/2017.
 */
@EFragment(R.layout.fragment_navui_call)
public class NavuiCall extends ListFragment {

    ListView lv;
    Cursor cursor1;

    @AfterViews
    void afterViews() {
        Toast.makeText(getContext(),"DDDDDDDDDDDDDD", Toast.LENGTH_SHORT).show();
        cursor1 = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "starred=?", new String[] {"1"}, null);
        getActivity().startManagingCursor(cursor1);
        String[] from = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone._ID};
        int[] to = {android.R.id.text1,android.R.id.text2};
        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_2, cursor1, from, to);
        setListAdapter(listAdapter);
        lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                TextView textView = (TextView) view.findViewById(android.R.id.text2);
                String text = textView.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ text));
                startActivity(callIntent);
            }
        });
    }

}
