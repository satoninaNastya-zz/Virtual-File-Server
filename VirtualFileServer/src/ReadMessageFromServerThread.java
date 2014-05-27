import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ReadMessageFromServerThread extends Thread {
    private BufferedReader inputServer;

    public ReadMessageFromServerThread(Socket socket) throws IOException {
        inputServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        start();
    }

    @Override
    public void run() {
        while (true) {

            try {
                String response;
                if ((response = inputServer.readLine()) != null) {
                    System.out.println(response);
                    if (response.equals("quite")) {
                        break;
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }
}
