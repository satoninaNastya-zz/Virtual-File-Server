import java.io.Serializable;

public class QuiteUserCommand implements Command, Serializable {

    public QuiteUserCommand() {
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if (clientThread.getUser() == null) {
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
        }
        return virtualFileSystem.quitUser(clientThread);
    }

}
