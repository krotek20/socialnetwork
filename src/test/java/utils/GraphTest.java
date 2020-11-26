package utils;

import org.junit.Before;
import org.junit.Test;
import socialnetwork.domain.enums.Gender;
import socialnetwork.domain.entities.User;
import socialnetwork.Utils.Graph;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class GraphTest {
    private Graph graph;
    private List<User> dummyData;
    private User dummyUser;

    @Before
    public void setUp() {
        dummyData = new ArrayList<>();
        dummyUser = new User("", "", "", "", null, null);
        dummyUser.resetCount();
        // Initialize 15 DUMMY users
        for (int i = 1; i <= 15; i++) {
            dummyUser = new User("User", "Name", "user1@yahoo.com",
                    "UserName1", LocalDate.of(2001, 1, 1), Gender.FEMALE);
            dummyUser.setCount(dummyUser.getID());
            dummyData.add(dummyUser);
        }
        graph = new Graph(dummyData);
        // Initialize 10 DUMMY friendships
        // Expected communities:
        // ||   1   ||   2, 3   ||   4, 6, 5   ||   7, 10, 9, 8   ||   11, 15, 14, 13, 12   ||
        for (long i = 1, j = 1; i <= 15; i += j, j++) {
            for (long k = 1; k < j; k++) {
                graph.addEdge(i, i + k);
            }
        }
    }

    @Test
    public void countConnectedComponents() {
        assertEquals(5L, graph.countConnectedComponents());
    }

    @Test
    public void getLargestComponent() {
        List<Long> largestComponent = graph.getLargestComponent();
        assertEquals(5, largestComponent.size());
        List<Long> comparingList = new ArrayList<>(Arrays.asList(11L, 15L, 14L, 13L, 12L));
        assertEquals(comparingList, largestComponent);
    }
}
