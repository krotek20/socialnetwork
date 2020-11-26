package socialnetwork.domain.enums;

import java.util.HashSet;

public enum Gender {
    MALE, FEMALE, UNKNOWN;

    Gender() {
    }

    public static HashSet<String> getEnum() {
        HashSet<String> values = new HashSet<>();
        for (Gender gender : Gender.values()) {
            values.add(gender.name());
        }
        return values;
    }
}
