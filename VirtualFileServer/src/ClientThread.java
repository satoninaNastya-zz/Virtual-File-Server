import java.io.*;
import java.net.Socket;


public class ClientThread extends Thread {

    private Socket socket;
    private PrintWriter outputStreamSocket;
    private ObjectInputStream inputStream;
    private final VirtualFileSystem theVirtualFileSystem;
    private User newUser;

    private QuiteUserCommand quit = new QuiteUserCommand();

    public User getUser() {
        return newUser;
    }

    public void setUser(User user) {
        newUser = user;
    }

    public void sendMessage(String message) {
        if (outputStreamSocket != null)
            outputStreamSocket.println(message);
    }

    public ClientThread(Socket clientSocket, VirtualFileSystem virtualFileSystem) throws IOException {
        socket = clientSocket;
        theVirtualFileSystem = virtualFileSystem;
        start();
    }

    @Override
    public void run() {

        try {
            outputStreamSocket = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            while (true) {
                if (inputStream == null) {
                    inputStream = new ObjectInputStream(socket.getInputStream());
                }
                Command newCommand = (Command) inputStream.readObject();
                Response responseClient = newCommand.execute(theVirtualFileSystem, this);
                responseClient.send();
            }
        } catch (Exception e) {

            this.interrupt();
        } finally {
            Response responseClient = quit.execute(theVirtualFileSystem, this);
            responseClient.send();
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }


}