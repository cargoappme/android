package me.cargoapp.cargo;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Marvin on 10/05/2017.
 */

@SharedPref(SharedPref.Scope.UNIQUE)
public interface ParkingStore {

    @DefaultBoolean(false)
    boolean hasPositionSaved();

    float latitude();
    float longitude();
}
