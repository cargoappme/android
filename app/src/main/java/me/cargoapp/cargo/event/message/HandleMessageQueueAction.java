package me.cargoapp.cargo.event.message;

/**
 * Created by Marvin on 10/05/2017.
 */

public class HandleMessageQueueAction {
    Type _type;

    public HandleMessageQueueAction(Type type) {
        _type = type;
    }

    public Type getType() {
        return _type;
    }

    public enum Type {
        RECEIVED,
        DONE
    }
}
