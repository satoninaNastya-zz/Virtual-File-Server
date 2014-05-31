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

        Directory destinationDirectory = virtualFileSystem.getDirectoryFromPath(destinationPath, clientThread.getUser().getCurrentDirectory());
        if (destinationDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_DESTINATION_PATH);
        }

        Directory sourceContainingDirectory = virtualFileSystem.getContainingDirectoryFromPath(sourcePath, clientThread.getUser().getCurrentDirectory());
        if (sourceContainingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_SOURCE_PATH);
        }

        String nameSourceObject = virtualFileSystem.getNewObjectName(sourcePath);
        File sourceFile = virtualFileSystem.getFile(sourceContainingDirectory, nameSourceObject);
        if (sourceFile == null) {
            Directory sourceDirectory = virtualFileSystem.getDirectory(sourceContainingDirectory, nameSourceObject);

            String responseError = virtualFileSystem.checkPassRemoveDirectory(sourceDirectory, clientThread.getUser().getCurrentDirectory());
            if (responseError != null) {
                return new ErrorResponse(clientThread, responseError);
            }

            if (virtualFileSystem.removeDirectory(sourceContainingDirectory, nameSourceObject)) {
                destinationDirectory.newDirectory(sourceDirectory);
                String responseAllUser = "move directory " + nameSourceObject;
                return new ChangeSystemResponse(responseAllUser, virtualFileSystem.getListClient(), clientThread, clientThread.getUser());
            } else {
                return new ErrorResponse(clientThread, ERROR_MOVE_DIRECTORY);
            }

        } else {

            if (virtualFileSystem.removeFile(sourceContainingDirectory, nameSourceObject)) {
                destinationDirectory.newFile(sourceFile);
                String responseAllUser = "move file " + nameSourceObject;
                return new ChangeSystemResponse(responseAllUser, virtualFileSystem.getListClient(), clientThread, clientThread.getUser());
            } else {
                return new ErrorResponse(clientThread, ERROR_MOVE_FILE);
            }
        }
    }

}
