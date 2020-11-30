package socialnetwork.ui.tui.menu;

import socialnetwork.domain.enums.Role;
import socialnetwork.service.FriendshipService;
import socialnetwork.ui.tui.BaseTUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class FriendshipTUI extends BaseTUI {
    private final FriendshipService friendshipService;

    public FriendshipTUI(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
        if (loggedUser != null) {
            if (loggedUser.getRole() == Role.ADMIN) {
                generateTUI("Friendship TUI", new HashMap<String, Runnable>() {{
                    put("Display all friendships", FriendshipTUI.this::displayAllFriendships);
                    put("Search one friendship", FriendshipTUI.this::searchOneFriendship);
                    put("Number of communities", FriendshipTUI.this::numberOfCommunities);
                    put("Largest community", FriendshipTUI.this::largestCommunity);
                    put("Delete friendship", FriendshipTUI.this::deleteFriendship);
                    put("Add friend", FriendshipTUI.this::requestFriendship);
                }});
            } else {
                generateTUI("Friendship TUI", new HashMap<String, Runnable>() {{
                    put("Search one friendship", FriendshipTUI.this::searchOneFriendship);
                    put("Largest community", FriendshipTUI.this::largestCommunity);
                    put("Remove friend", FriendshipTUI.this::deleteFriendship);
                    put("Add friend", FriendshipTUI.this::requestFriendship);
                }});
            }
        }
    }

    private void requestFriendship() {
        Map<String, String> friendshipMap = readMap("id1");
        friendshipMap.put("id2", loggedUser.getID().toString());
        System.out.println("Friend request " +
                (friendshipService.requestFriendship(friendshipMap) ?
                        "sent!" : "could not be sent!"));
    }

    private void deleteFriendship() {
        Map<String, String> friendshipMap;
        if (loggedUser.getRole() == Role.ADMIN) {
            friendshipMap = readMap("id1", "id2");
        } else {
            friendshipMap = readMap("id1");
            friendshipMap.put("id2", loggedUser.getID().toString());
        }
        System.out.println("Operation " +
                (friendshipService.deleteFriendship(friendshipMap) ?
                        "successful" : "failed"));
    }

    private void largestCommunity() {
        List<Long> largestCommunity = friendshipService.getLargestCommunity();
        System.out.println("Size of community: " + largestCommunity.size());

        StringJoiner joiner = new StringJoiner(", ");
        System.out.print("Community: ");
        for (Long userID : largestCommunity) {
            joiner.add(userID.toString());
        }
        System.out.println(joiner);
    }

    private void searchOneFriendship() {
        Map<String, String> friendshipMap = readMap("id1", "id2");
        System.out.println(friendshipService.readOneFriendship(friendshipMap));
    }

    private void displayAllFriendships() {
        for (String friendship : friendshipService.readAllFriendships()) {
            System.out.println(friendship);
        }
    }

    private void numberOfCommunities() {
        System.out.println(friendshipService.getNumberOfCommunities() + " communities have been found");
    }
}
