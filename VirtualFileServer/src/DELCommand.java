import java.io.Serializable;


public class DELCommand implements Command, Serializable {
    private String path;

    public DELCommand(String path) {
        this.path = path;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if (clientThread.getUser() == null) {
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
        }
        return virtualFileSystem.removeFile(path,clientThread);
}}
