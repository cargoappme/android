package me.cargoapp.cargo;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Marvin on 04/04/2017.
 */

@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface Preferences {

    @DefaultBoolean(true)
    boolean isFirstRun();
}
