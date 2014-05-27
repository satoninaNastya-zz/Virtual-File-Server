
public class ErrorResponse implements Response {
    private ClientThread theClientThread;
    private String errorString;

    public ErrorResponse(ClientThread clientThread, String error) {
        this.errorString = error;
        this.theClientThread = clientThread;
    }

    @Override
    public void send() {
        theClientThread.sendMessage(errorString);
    }
}
