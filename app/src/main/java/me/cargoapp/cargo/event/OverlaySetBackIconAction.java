package me.cargoapp.cargo.event;

/**
 * Created by Marvin on 26/04/2017.
 */

public class OverlaySetBackIconAction {
    boolean _back;

    public OverlaySetBackIconAction(boolean back) {
        _back = back;
    }

    public boolean getBack() {
        return _back;
    }
}
