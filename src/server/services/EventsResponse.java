package server.services;

import java.util.List;

/**
 * Models a JSON response for the EventsService
 */
public class EventsResponse implements Response {

    private List<EventResponse> data;

    /**
     * Models a JSON response for the EventsService
     *
     * @param data a list of event objects
     */
    public EventsResponse(List<EventResponse> data) {
        this.data = data;
    }

    public List<EventResponse> getData() {
        return data;
    }
}
