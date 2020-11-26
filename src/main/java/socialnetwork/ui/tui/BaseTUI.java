package socialnetwork.ui.tui;

import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.RepositoryException;
import socialnetwork.service.ServiceException;
import socialnetwork.ui.UI;
import socialnetwork.ui.UIException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeSet;

public class BaseTUI implements UI {
    // CONSTANTS
    /**
     * Constant representing the option to exit the Main TUI
     */
    protected static final String EXIT_OPTION;
    /**
     * Constant representing the option to logout the Login TUI
     */
    protected static final String LOGOUT_OPTION;
    /**
     * Constant representing the option to get back to the Main TUI
     */
    protected static final String BACK_OPTION;
    /**
     * Constant by which the title is preceded
     */
    protected static final String TITLE_PREFIX;
    /**
     * Constant by which the every option in the list of options is preceded
     */
    protected static final String OPTION_PREFIX;
    /**
     * Constant representing the option prompter
     */
    protected static final String OPTION_PROMPT;
    /**
     * Constant representing the text to be displayed after an action is completed
     */
    protected static final String CONTINUE_PROMPT;
    /**
     * Constant representing the text to be displayed when no option is found for the user input
     */
    protected static final String INVALID_OPTION;
    // END OF CONSTANTS

    public static User loggedUser = null;
    private String tui = "";
    private Map<String, UI> tuiMenu = new HashMap<>();

    static {
        EXIT_OPTION = "EXIT";
        BACK_OPTION = "BACK";
        TITLE_PREFIX = "# ";
        OPTION_PREFIX = "- ";
        OPTION_PROMPT = "> ";
        LOGOUT_OPTION = "LOGOUT";
        INVALID_OPTION = "Invalid option...";
        CONTINUE_PROMPT = "Press [ENTER] to continue...";
    }

    /**
     * (Re)Generate the UI text and functionality
     * This can and should be called in the constructor of the subclasses to initialize their specific UI
     * Otherwise the user will be prompted with an empty UI
     *
     * @param tuiTitle text printed before printing all the options
     * @param tuiMenu  associations between options and functionality (command pattern)
     */
    protected void generateTUI(String tuiTitle, Map<String, UI> tuiMenu) {
        this.tuiMenu = tuiMenu;

        StringBuilder tui = new StringBuilder();
        tui.append(TITLE_PREFIX).append(tuiTitle).append(System.lineSeparator());
        for (String option : new TreeSet<>(tuiMenu.keySet())) {
            tui.append(OPTION_PREFIX).append(option).append(System.lineSeparator());
        }
        switch (tuiTitle) {
            case "Main TUI":
                tui.append(OPTION_PREFIX).append(LOGOUT_OPTION).append(System.lineSeparator()).append(OPTION_PROMPT);
                break;
            case "Login TUI":
                tui.append(OPTION_PREFIX).append(EXIT_OPTION).append(System.lineSeparator()).append(OPTION_PROMPT);
                break;
            case "User TUI":
            case "Chat TUI":
            case "Friendship TUI":
            case "Notification TUI":
                tui.append(OPTION_PREFIX).append(BACK_OPTION).append(System.lineSeparator()).append(OPTION_PROMPT);
                break;
        }

        this.tui = tui.toString();
    }

    /**
     * Read one string from the console
     *
     * @param key prompt representing the desired input from the user
     * @return user input
     */
    protected String readOne(String key) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(key + ": ");
        return scanner.nextLine();
    }

    /**
     * Read multiple strings from the console
     * Calls readOne for every key in keys
     *
     * @param keys prompts for every desired input from the user
     * @return a map with values input by user and their corresponding keys
     */
    protected Map<String, String> readMap(String... keys) {
        Map<String, String> map = new HashMap<>();
        for (String key : keys) {
            map.put(key, readOne(key));
        }
        return map;
    }

    /**
     * General loop body displaying the UI, asking for user input
     * and then calling the corresponding functionality for the chosen option
     * The option is chosen by iterating the list of options and checking if an option contains the input string
     * The match is case insensitive and the first option found is called, ignoring the rest
     *
     * @return true if the user didn't input the EXIT_OPTION string; false otherwise
     */
    private Boolean loop() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(tui);
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase(EXIT_OPTION)
                || input.equalsIgnoreCase(LOGOUT_OPTION)
                || input.equalsIgnoreCase(BACK_OPTION)) {
            return false;
        }
        for (String option : tuiMenu.keySet()) {
            if (input.isEmpty()) {
                break;
            }
            if (option.toLowerCase().contains(input.toLowerCase())) {
                System.out.println(OPTION_PROMPT + option);
                tuiMenu.get(option).run();
                return true;
            }
        }
        System.out.println(INVALID_OPTION);
        return true;
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            System.out.println();
            try {
                running = loop();
            } catch (ValidationException | RepositoryException | ServiceException | UIException e) {
                System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            } finally {
                if (running) {
                    System.out.println(CONTINUE_PROMPT);
                    Scanner scanner = new Scanner(System.in);
                    scanner.nextLine();
                }
            }
            System.out.println();
        }
    }
}
