package socialnetwork.Utils;

import socialnetwork.domain.entities.User;

import java.util.*;

/**
 * Graph of users
 * - Node - ID of an user
 * - Edge - Friendship between two users
 */
public class Graph {
    private final Map<Long, List<Long>> edges;
    private final Map<Long, Boolean> checked;

    /**
     * Constructor
     * It must call initializeEdges of all users
     *
     * @param users all users from repository
     */
    public Graph(Iterable<User> users) {
        edges = new HashMap<>();
        initializeEdges(users);
        checked = new HashMap<>();
    }

    /**
     * Initializing adjacency list of every user (linked by his ID)
     *
     * @param users all users from repository
     */
    public void initializeEdges(Iterable<User> users) {
        for (User user : users) {
            edges.put(user.getID(), new LinkedList<Long>());
        }
    }

    /**
     * Add new edge in Graph
     *
     * @param firstNode  first user ID
     * @param secondNode second user ID
     */
    public void addEdge(long firstNode, long secondNode) {
        edges.get(firstNode).add(secondNode);
        edges.get(secondNode).add(firstNode);
    }

    /**
     * Compute the list of connected components
     * sorted descending by their size
     *
     * @return descending sorted list of connected components
     */
    private List<List<Long>> connectedComponents() {
        List<List<Long>> components = new ArrayList<>();

        for (Long userID : edges.keySet()) {
            checked.put(userID, false);
        }
        for (Long userID : edges.keySet()) {
            if (!checked.get(userID)) {
                List<Long> component = DFS(userID);
                components.add(component);
            }
        }

        components.sort((o1, o2) -> o2.size() - o1.size());
        return components;
    }

    private List<Long> DFS(Long root) {
        Stack<Long> stack = new Stack<>();
        List<Long> component = new ArrayList<>();

        stack.add(root);
        while (!stack.empty()) {
            Long currentID = stack.pop();
            component.add(currentID);
            checked.put(currentID, true);
            for (Long friendID : edges.get(currentID)) {
                if (!checked.get(friendID)) {
                    stack.add(friendID);
                }
            }
        }

        return component;
    }

    /**
     * Count connected components
     *
     * @return The number of connected components in current Graph (long)
     */
    public long countConnectedComponents() {
        return connectedComponents().size();
    }

    /**
     * Get the largest component of the current Graph
     *
     * @return IDs list of users from the largest component
     */
    public List<Long> getLargestComponent() {
        return connectedComponents().get(0);
    }
}
