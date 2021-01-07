package socialnetwork.ui.gui.thread;

import javafx.application.Platform;
import javafx.concurrent.Task;
import socialnetwork.domain.entities.Event;
import socialnetwork.service.EventService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventNotificationTask extends Task<Void> {
    private final EventService eventService;
    private List<Event> events;

    public EventNotificationTask(EventService eventService) {
        this.eventService = eventService;
        this.events = eventService.readAllEvents();
    }

    @Override
    protected Void call() throws Exception {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException ex) {
                break;
            }
            List<Event> notifiedEvents = getNotifiedEvents();
            for (Event event : notifiedEvents) {
                Platform.runLater(() -> eventService.notifyEventUsers(event, event.getTargetTime()));
            }
        }
        return null;
    }

    private List<Event> getNotifiedEvents() {
        long daySeconds = 86400, hourSeconds = 3600, tenMinutesSeconds = 600;
        List<Event> notifiedEvents = new ArrayList<>();
        events = eventService.readAllEvents();

        for (Event event : events) {
            long time = LocalDateTime.now().until(event.getStartAt(), ChronoUnit.SECONDS);
            event.setTargetTime((short) (time == daySeconds ? 0
                    : (time == hourSeconds ? 1
                    : (time == tenMinutesSeconds ? 2
                    : -1))));
            if (event.getTargetTime() != -1) {
                notifiedEvents.add(event);
            }
        }

        return notifiedEvents;
    }
}
