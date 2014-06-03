import java.io.Serializable;

public class LockCommand implements Command,Serializable {
    private String path;

    public LockCommand(String path) {
        this.path = path;

    }
    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if(clientThread.getUser()==null) {
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
        }
        return virtualFileSystem.lockFile(path,clientThread);

    }
}
