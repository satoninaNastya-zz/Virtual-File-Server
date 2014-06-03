import java.io.Serializable;


public class CopyCommand implements Command, Serializable {

    String sourcePath;
    String destinationPath;

    public CopyCommand(String sourcePath, String destinationPath) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if (clientThread.getUser() == null) {
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
        }
        return virtualFileSystem.copy(sourcePath, destinationPath, clientThread);
    }
}
