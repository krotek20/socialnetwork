package socialnetwork.domain.dto;

import socialnetwork.domain.enums.Gender;

import java.time.LocalDate;
import java.time.Period;

public class UserDTO {
    private LocalDate birthDate;
    private String firstname;
    private String lastname;
    private Gender gender;
    private String email;

    public UserDTO() {

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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return String.format(
                "%-15s%-15s%-30s%-5s%s",
                firstname,
                lastname,
                email,
                getAge(birthDate),
                gender.toString());
    }
}
