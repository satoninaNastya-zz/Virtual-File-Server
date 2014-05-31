import java.io.Serializable;


public class UnLockCommand implements Command, Serializable {
    private String path;

    public UnLockCommand(String path) {
        this.path = path;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if (clientThread.getUser() == null) {
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
        }

        Directory containingDirectory = virtualFileSystem.getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        if (containingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_PATH);
        }
        String nameFile = virtualFileSystem.getNewObjectName(path);

        File file = virtualFileSystem.getFile(containingDirectory, nameFile);
        if (file == null) {
            return new ErrorResponse(clientThread, ERROR_FILE_NOT_FOUND);
        }

        if (file.unlockFile(clientThread.getUser().getName())) {
            String responseAllUser = "unlocked file " + nameFile;
            return new ChangeSystemResponse(responseAllUser, virtualFileSystem.getListClient(), clientThread, clientThread.getUser());

        } else {
            return new ErrorResponse(clientThread, ERROR_UNLOCK);
        }

    }
}
