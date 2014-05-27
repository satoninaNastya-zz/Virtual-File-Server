import java.util.ArrayList;
public class ChangeSystemResponse implements Response {
    private String changeString;
    private ArrayList<ClientThread> clientsThreadsOnline;
    private ClientThread userThread;
    private User userChange;

    public ChangeSystemResponse(String changeString, ArrayList<ClientThread> clientsOnline, ClientThread clientThread, User user) {
        this.clientsThreadsOnline = clientsOnline;
        this.userThread = clientThread;
        this.changeString = changeString;
        this.userChange = user;
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
