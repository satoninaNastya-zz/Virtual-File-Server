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
        if(clientThread.getUser()==null) {
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
            if (sourceDirectory == null) {
                return new ErrorResponse(clientThread, ERROR_SOURCE_PATH);
            }
            destinationDirectory.newDirectory(virtualFileSystem.copyDirectory(sourceDirectory));
            String responseAllUser = "copy directory " + nameSourceObject;
            return new ChangeSystemResponse(responseAllUser, virtualFileSystem.getListClient(), clientThread, clientThread.getUser());
        } else {
            destinationDirectory.newFile(virtualFileSystem.copyFile(sourceFile));
            String responseAllUser = "copy file " + nameSourceObject;
            return new ChangeSystemResponse(responseAllUser, virtualFileSystem.getListClient(), clientThread, clientThread.getUser());
        }
    }
}
