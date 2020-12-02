package socialnetwork.Utils.Events;

import socialnetwork.domain.entities.Friendship;
import socialnetwork.domain.entities.User;

public class FriendshipChangeEvent implements Event{
    private ChangeEventType type;
    private Friendship data, oldData;

    public FriendshipChangeEvent(ChangeEventType type, Friendship data){
        this.data = data;
        this.type = type;
    }

    public FriendshipChangeEvent(ChangeEventType type, Friendship data, Friendship oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public Friendship getData() {
        return data;
    }

    public Friendship getOldData() {
        return oldData;
    }
}

