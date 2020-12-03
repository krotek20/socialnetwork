package socialnetwork.repository.file;

import socialnetwork.Utils.design.Observable;
import socialnetwork.Utils.design.Observer;
import socialnetwork.domain.Entity;
import socialnetwork.domain.entities.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static socialnetwork.repository.file.UserFileRepository.getUser;

/**
 * Abstract file Repository for entities of Type Entity
 *
 * @param <ID> unique identifier type
 * @param <E>  entities type
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>>
        extends InMemoryRepository<ID, E> implements Observer {
    private final String fileName;

    /**
     * Constructor
     * Creates the repository file if it doesn't exist, or reuses it
     * Calls loadData to read the file contents
     * Observes itself to writeToFile after every successful CRUD operation from base class
     *
     * @param fileName  repository file
     * @param validator entity validator
     */
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        try {
            Files.createFile(Paths.get(fileName));
        } catch (FileAlreadyExistsException e) {
            System.out.println("Using existing repository file: " + fileName + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadData();
        register(this);
    }

    /**
     * This method reads data from fileName in memory
     * Called in constructor to initialize the repository
     */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> attributes = Arrays.asList(line.split(";"));
                E entity = extractEntity(attributes);
                super.save(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method write from repository to fileName
     * Completely rewrites fileName
     * Called by update after every successful CRUD operation
     */
    private void writeToFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, false))) {
            for (E entity : findAll()) {
                bufferedWriter.write(createEntityAsString(entity));
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * extract entity - template method design pattern
     * creates an entity of type E having a specified list of attributes
     *
     * @param attributes list of attributes as strings
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);

    /**
     * creates a line in a csv from an entity
     *
     * @param entity the entity to be transformed
     * @return a string referring a new line of data in a csv file
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * Whenever this observable is observe, writeToFile will be called
     *
     * @param observable the observable subject
     */
    @Override
    public void update(Observable observable) {
        if (observable == this) {
            writeToFile();
        }
    }

    /**
     * Loading users locally from .csv / .txt file.
     * This method should be called by {@code NotificationFileRepository}
     * and {@code MessageFileRepository}.
     *
     * @return list of all users.
     */
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> attributes = Arrays.asList(line.split(";"));
                User user = getUser(attributes);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
}