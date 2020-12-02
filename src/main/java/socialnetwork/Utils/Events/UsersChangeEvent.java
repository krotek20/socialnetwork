package socialnetwork.Utils.Events;

import socialnetwork.domain.entities.User;

public class UsersChangeEvent implements Event{
    private ChangeEventType type;
    private User data, oldData;

    public UsersChangeEvent(ChangeEventType type, User data){
        this.data = data;
        this.type = type;
    }

    public UsersChangeEvent(ChangeEventType type, User data, User oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}
