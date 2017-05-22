package me.cargoapp.cargo.event.message

/**
 * Created by Marvin on 10/05/2017.
 */

data class HandleMessageQueueAction(val type: Type) {
    enum class Type {
        RECEIVED,
        DONE
    }
}
