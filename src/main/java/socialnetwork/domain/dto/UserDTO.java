package socialnetwork.domain.dto;

import socialnetwork.domain.entities.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class UserDTO {
    private List<User> friends;
    private LocalDate birthDate;
    private String firstname;
    private String lastname;
    private String email;
    private boolean info;

    public UserDTO() {
        this.info = false;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public boolean isInfo() {
        return info;
    }

    public void setInfo(boolean info) {
        this.info = info;
    }

    /**
     * Gets the age of an {@code User} by his {@code birthDate}.
     * It calculates the {@code Period} between
     * current LocalDate and the given birthDate.
     *
     * @param birthDate given for computing the age of an User.
     * @return a String value representing the age of the User.
     */
    private String getAge(LocalDate birthDate) {
        Period period = Period.between(birthDate, LocalDate.now());
        return String.valueOf(period.getYears());
    }

    @Override
    public String toString() {
        return (info ? email + "\n" + birthDate.toString() : firstname + " " + lastname);
    }
}
