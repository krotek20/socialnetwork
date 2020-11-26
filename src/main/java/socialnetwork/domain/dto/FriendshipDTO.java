package socialnetwork.domain.dto;

import java.time.LocalDateTime;

public class FriendshipDTO {
    private String firstname;
    private String lastname;
    private LocalDateTime date;

    public FriendshipDTO() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format(
                "%-15s%-15s%s",
                firstname,
                lastname,
                date.toString());
    }
}
