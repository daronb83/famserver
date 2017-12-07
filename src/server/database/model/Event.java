package server.database.model;

/**
 * Models an object from the Events table in the database
 */
public class Event {
    private String descendant;
    private String eventID;
    private String personID;
    private String eventType;
    private double latitude;
    private double longitude;
    private String country;
    private String city;
    private String year;


    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }


    /**
     * Creates an Event, which represents an event in a Person's life.
     *
     * @param eid a unique Event identifier
     * @param pid the id of the Person this event belongs to
     * @param uid the id of the User this event belongs to
     * @param eventType the type of even (marriage, death, census, etc.)
     * @param latitude the latitude coordinate of the event's location
     * @param longitude the longitude coordinate of the event's location
     * @param country the country where the event took place
     * @param city the city where the event took place
     * @param year the year the event took place
     */
    public Event( String eid, String pid, String uid, String eventType, double latitude, double longitude,
                  String country, String city, String year) {
        this.eventID = eid;
        this.personID = pid;
        this.descendant = uid;
        this.eventType = eventType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.year = year;
    }

    public String getEventID() {
        return eventID;
    }

    public String getPersonID() {
        return personID;
    }

    public String getDescendant() {
        return descendant;
    }

    public String getYear() {
        return year;
    }

    public String getEventType() {
        return eventType;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
