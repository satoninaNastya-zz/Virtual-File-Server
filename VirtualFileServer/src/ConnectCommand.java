import java.io.Serializable;

public class ConnectCommand implements Command, Serializable {
    private User user;
    private String address;
    private int port;
    private boolean isCorrect = true;


    public ConnectCommand(String addressServer, String userName) {
        this.user = new User(userName);
        try {
            if ("localhost".equals(addressServer)) {
                this.address = "127.0.0.1";
                this.port = 1255;

            } else {
                String[] address = addressServer.split(":");
                if ("127.0.0.1".equals(address[0]) && "1255".equals(address[1])) {
                    this.address = "127.0.0.1";
                    this.port = 1255;
                } else isCorrect = false;
            }
        } catch (Exception e) {
            isCorrect = false;
        }
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if (virtualFileSystem.addNewUser(user)) {
            clientThread.setUser(user);
            int numberClientOnline = virtualFileSystem.getNumberClientOnline();
            clientThread.getUser().setCurrentDirectory(virtualFileSystem.getRootDirectory());
            return new ConnectResponse(clientThread, numberClientOnline);
        } else
            return new ErrorResponse(clientThread, ERROR_NAME_EXIST);

    }


    public User getUser() {
        return user;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
