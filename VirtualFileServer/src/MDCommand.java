import java.io.Serializable;


public class MDCommand implements Command, Serializable {
    private String path;

    public MDCommand(String path) {
        this.path = path;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if (clientThread.getUser() == null) {
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
        }
        return  virtualFileSystem.makeDirectory(path, clientThread);
    }
}