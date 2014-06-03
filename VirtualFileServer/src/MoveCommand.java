import java.io.Serializable;

public class MoveCommand implements Command, Serializable {
    private String sourcePath;
    private String destinationPath;

    public MoveCommand(String sourcePath, String destinationPath) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if (clientThread.getUser() == null) {
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
        }
        return virtualFileSystem.move(sourcePath, destinationPath, clientThread);
    }
}


