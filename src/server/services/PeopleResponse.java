package server.services;

import java.util.List;


/**
 * Modles a JSON response for the PeopleService
 */
public class PeopleResponse implements Response {

    private List<PersonResponse> data;

    /**
     * Models a JSON response for the PeopleService
     *
     * @param data a list of Person objects
     */
    public PeopleResponse(List<PersonResponse> data) {
        this.data = data;
    }

    public List<PersonResponse> getData() {
        return data;
    }
}
