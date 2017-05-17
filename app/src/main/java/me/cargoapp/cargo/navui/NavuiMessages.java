package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SimpleCursorAdapter;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.Locale;

import me.cargoapp.cargo.event.NavuiLaunchEvent;
import me.cargoapp.cargo.R;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Mathieu on 17/05/2017.
 */
@EFragment(R.layout.fragment_navui_messages)
public class NavuiMessages extends ListFragment {
    String text;
    ListView lv;
    Cursor cursor1;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @AfterViews
    void afterViews(){
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
                 text = textView.getText().toString();
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                askSpeechInput();
            }
        });
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
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
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(text, null, result.get(0), null, null);
                }
                break;
            }

        }
    }
}
