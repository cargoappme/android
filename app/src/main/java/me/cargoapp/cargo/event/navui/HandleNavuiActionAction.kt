package me.cargoapp.cargo.event.navui

/**
 * Created by Marvin on 26/04/2017.
 */

data class HandleNavuiActionAction(val type: Type) {
    enum class Type {
        CALL,
        MESSAGE,
        MUSIC,
        OIL,
        PARKING,
        MENU,
        QUIT
    }
}
