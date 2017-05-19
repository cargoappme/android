package me.cargoapp.cargo.event.navui;

/**
 * Created by Marvin on 26/04/2017.
 */

public class HandleNavuiActionAction {
    Type _type;

    public HandleNavuiActionAction(Type type) {
        _type = type;
    }

    public Type getType() {
        return _type;
    }

    public enum Type {
        CALL,
        MESSAGE,
        MUSIC,
        OIL,
        PARKING,
        MENU,
        QUIT
    }
}
