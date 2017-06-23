package me.cargoapp.cargo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import me.cargoapp.cargo.event.overlay.SetOverlayVisibilityAction;
import me.cargoapp.cargo.helper.IntentHelper;

@EFragment(R.layout.tab_vehicle)
public class TabHistory extends Fragment {

    DestinationBDD destinationBDD;

    @EventBusGreenRobot
    EventBus _eventBus;

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

                Application_.isJourneyStarted = true;
                Application_.journeyWithSharing = false;

                _eventBus.post(new SetOverlayVisibilityAction(true));

                getActivity().finish();
            }

        });
    }
}
