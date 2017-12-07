package server.services;


/**
 * Models a JSON response for the EventService
 */
public class EventResponse implements Response {

    private String descendant;
    private String eventID;
    private String personID;
    private double latitude;
    private double longitude;
    private String country;
    private String city;
    private String eventType;
    private String year;

    /**
     * Models a JSON request for the EventService
     *
     * @param descendent the username of the User the event belongs to
     * @param eventID the event's id
     * @param personID the id of the Person the event belongs to
     * @param latitude the latitude of the event's location
     * @param longitude the longitude of the event's location
     * @param country the country of the event's location
     * @param city the city of the event's location
     * @param eventType the type of event\\
     * @param year the year the event took place (String)
     */
    public EventResponse (String descendent, String eventID, String personID, double latitude,
                         double longitude, String country, String city, String eventType,
                         String year) {
        this.descendant = descendent;
        this.eventID = eventID;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    public String getDescendant() {
        return descendant;
    }

    public String getEventID() {
        return eventID;
    }

    public String getPersonID() {
        return personID;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getEventType() {
        return eventType;
    }

    public String getYear() {
        return year;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
