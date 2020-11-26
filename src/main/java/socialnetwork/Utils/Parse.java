package socialnetwork.Utils;

import socialnetwork.domain.enums.Gender;
import socialnetwork.service.ServiceException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Safely parsing data from {@code String}
 */
public class Parse {
    /**
     * Parsing String to Long safely
     *
     * @param toLong String
     * @return toLong parsed from String to long
     * @throws ServiceException when id is not a number
     */
    public static long safeParseLong(String toLong) throws ServiceException {
        try {
            return Long.parseLong(toLong);
        } catch (NumberFormatException e) {
            throw new ServiceException("Please insert a numeric value!");
        }
    }

    /**
     * Parsing String to LocalDate safely
     *
     * @param localDate String
     * @return birthdate parsed from String to LocalDate
     * @throws ServiceException when localDate is an invalid LocalDate
     */
    public static LocalDate safeParseLocalDate(String localDate) throws ServiceException {
        try {
            return LocalDate.parse(localDate);
        } catch (DateTimeParseException e) {
            throw new ServiceException("local date is invalid");
        }
    }

    /**
     * Parsing String to Gender safely
     *
     * @param gender String
     * @return gender parsed from String to Gender
     * @throws ServiceException when gender is an invalid Gender
     */
    public static Gender safeParseGender(String gender) throws ServiceException {
        try {
            return Gender.valueOf(gender);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("gender is invalid");
        }
    }
}
