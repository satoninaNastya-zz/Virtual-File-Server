
public class QuitResponse implements Response {
    private ClientThread theClientThread;

    public QuitResponse(ClientThread clientThread) {
        this.theClientThread = clientThread;
    }

    @Override
    public void send() {
        theClientThread.sendMessage("quit");
    }
}
