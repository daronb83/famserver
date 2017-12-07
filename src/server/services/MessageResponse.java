package server.services;

/**
 * Models a JSON response for any service the requires only a success message, or
 *  for an JSON error response
 */
public class MessageResponse implements Response {

    private String message;

    /**
     * Models a JSON response for any service the requires only a success message, or
     *  for an JSON error response
     *
     * @param message a success message or description of an error
     */
    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
