package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import me.cargoapp.cargo.R;
import me.cargoapp.cargo.helper.ContactsHelper;
import me.cargoapp.cargo.helper.IntentHelper;
import me.cargoapp.cargo.navui.adapter.ContactsAdapter;

/**
 * Created by Mathieu on 05/05/2017.
 */
@EFragment(R.layout.fragment_navui_contacts)
public class NavuiCall extends Fragment {

    @ViewById(R.id.grid_view)
    GridView _gridView;

    @AfterViews
    void afterViews() {
        final ArrayList<ContactsAdapter.ContactRepresentation> contacts = ContactsHelper.getStarred(getActivity());

        _gridView.setAdapter(new ContactsAdapter(getActivity(), contacts));

        _gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                startActivity(IntentHelper.createCallIntent(contacts.get(position).phoneNumber));
            }
        });
    }
}
