package me.cargoapp.cargo.event;

/**
 * Created by Marvin on 26/04/2017.
 */

public class NavuiLaunchEvent {
    Type _type;

    public NavuiLaunchEvent(Type type) {
        _type = type;
    }

    public Type getType() {
        return _type;
    }

    public static enum Type {
        CALL,
        MESSAGE,
        MUSIC,
        OIL,
        PARKING,
        MENU
    }
}
