import java.util.ArrayList;

public class ChangeSystemResponse implements Response {
    private String changeString;
    private final ArrayList<ClientThread> clientsThreadsOnline;
    private ClientThread userThread;
    private User userChange;

    public ChangeSystemResponse(String changeString, ArrayList<ClientThread> clientsOnline, ClientThread clientThread) {
        this.clientsThreadsOnline = clientsOnline;
        this.userThread = clientThread;
        this.changeString = changeString;
        this.userChange = clientThread.getUser();
    }

    @Override
    public void send() {
        String message = "User " + userChange.getName() + " " + changeString;
        synchronized (clientsThreadsOnline) {
            for (ClientThread aListClientThread : clientsThreadsOnline)
                if (aListClientThread != userThread)
                    aListClientThread.sendMessage(message);
        }
    }
}
