
public class ConnectResponse implements Response {
    private ClientThread theClientThread;
    private int numberClientOnline;

    public ConnectResponse(ClientThread clientThread, int numberClient) {
        this.numberClientOnline = numberClient;
        this.theClientThread = clientThread;
    }

    @Override
    public void send() {
        String response;
        if (numberClientOnline == 1) {
            response = "Online " + numberClientOnline + " user now";
        } else {
            response = "Online " + numberClientOnline + " users now";
        }
        theClientThread.sendMessage(response);
    }
}
