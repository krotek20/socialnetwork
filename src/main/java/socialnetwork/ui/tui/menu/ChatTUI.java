package socialnetwork.ui.tui.menu;

import socialnetwork.Utils.Constants;
import socialnetwork.service.ChatService;
import socialnetwork.service.MessageService;
import socialnetwork.ui.tui.BaseTUI;

import java.util.HashMap;
import java.util.Map;

public class ChatTUI extends BaseTUI {
    private final MessageService messageService;
    private final ChatService chatService;

    public ChatTUI(MessageService messageService, ChatService chatService) {
        this.messageService = messageService;
        this.chatService = chatService;
        if (loggedUser != null) {
            generateTUI("Chat TUI", new HashMap<String, Runnable>() {{
                put("Display all messages", ChatTUI.this::displayAllMessages);
                put("Display all chats", ChatTUI.this::displayAllChats);
                put("Send new message", ChatTUI.this::sendMessage);
                put("Create new chat", ChatTUI.this::createChat);
            }});
        }
    }

    private void sendMessage() {
        Map<String, String> chatMap = readMap("to", "message");
        chatMap.put("reply", null);
        System.out.println("Sending...");
        System.out.println("Message sent at " +
                messageService.sendMessage(loggedUser, chatMap).getTimestamp()
                        .format(Constants.DATE_TIME_FORMATTER));
    }

    private void displayAllMessages() {
        String chatID = readOne("id");
        System.out.println(messageService.readAllMessages(loggedUser, chatID));
    }

    private void displayAllChats() {
        for (String chat : chatService.readAllChats(loggedUser)) {
            System.out.println(chat);
        }
    }

    private void createChat() {
        String name = readOne("name");

        System.out.println("Select users you want to add to the new chat");
        System.out.println("Type *stop* to exit the loop");
        Map<String, String> chatMap = new HashMap<>();
        String userID;
        int count = 1;
        while (!(userID = readOne("id")).equals("stop")) {
            chatMap.put("id" + count++, userID);
        }

        System.out.println("Operation " +
                (chatService.createChat(name, chatMap, loggedUser) ?
                        "successful" : "failed"));
    }
}
