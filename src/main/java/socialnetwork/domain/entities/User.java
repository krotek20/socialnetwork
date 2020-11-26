package socialnetwork.domain.entities;

import socialnetwork.domain.Entity;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.enums.Role;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

public class User implements Entity<Long> {
    private static long count = 1;
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDate;
    private Gender gender;
    private Role role;

    public User(String firstName, String lastName, String email, String password, LocalDate birthDate, Gender gender) {
        this.id = count;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
        this.role = Role.USER;
    }

    // begin of GETTERS and SETTERS
    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Setting the current count on the next maximum id.
     * This should be called specifically after every setID call and User instantiation.
     *
     * @param id id of the current entity instance
     */
    public void setCount(long id) {
        if (id >= count) count = id + 1;
    }

    /**
     * Resetting count to 1.
     * This should be use only for tests.
     */
    public void resetCount() {
        count = 1;
    }
    // end of GETTERS and SETTERS

    /**
     * Cast User entity to string.
     * This should be used to export User entities data
     * in .csv and/or .txt files as well as in databases.
     *
     * @return a string of User attributes separated by semicolons [;]
     */
    @Override
    public String toString() {
        // TODO: Encrypt password
        StringJoiner joiner = new StringJoiner(";");
        joiner.add(String.valueOf(id))
                .add(firstName)
                .add(lastName)
                .add(email)
                .add(password)
                .add(birthDate.toString())
                .add(gender.toString())
                .add(role.toString());
        return joiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) || Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}