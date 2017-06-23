package me.cargoapp.cargo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import me.cargoapp.cargo.helper.IntentHelper;

@EFragment(R.layout.tab_vehicle)
public class TabVehicle extends Fragment {

    DestinationBDD destinationBDD;

    @ViewById(R.id.listView)
    ListView mListView;


    @AfterViews
    void afterViews() {
        destinationBDD = new DestinationBDD(getContext());
        destinationBDD.open();
        final ArrayList<String> mylist = new ArrayList<String>();
        int count = 0;
        final Destination[] destinationFromBDD = destinationBDD.getAllDestinations();
        while (count < destinationFromBDD.length ) {
            mylist.add(destinationFromBDD[count].getAdress());
            count++;
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_list_item_1, mylist);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = IntentHelper.INSTANCE.createNavigationIntent(destinationFromBDD[arg2].getLat(), destinationFromBDD[arg2].getLon(), mylist.get(arg2));
                startActivity(intent);
            }

        });
    }
}
