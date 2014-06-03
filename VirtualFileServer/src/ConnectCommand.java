import java.io.Serializable;

public class ConnectCommand implements Command, Serializable {
    private User user;
    private String address;
    private int port;

    public ConnectCommand(String addressServer, String userName) {
        this.user = new User(userName);
        String[] address = addressServer.split(":");
        if(address.length==2){
        this.address=address[0];
        this.port=Integer.parseInt(address[1]);}
    }


    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        return virtualFileSystem.connectUser(user, clientThread);
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
