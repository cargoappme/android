package me.cargoapp.cargo.navui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;

import org.androidannotations.annotations.EFragment;
import me.cargoapp.cargo.components.navui.Main;

@EFragment
public class MainFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ComponentContext c = new ComponentContext(getContext());

        final Component mainLayer = Main.create(c)
                .build();

        return LithoView.create(getContext(), mainLayer);
    }
}