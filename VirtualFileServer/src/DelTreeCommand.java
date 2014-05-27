import java.io.Serializable;


public class DelTreeCommand implements Command,Serializable {

    private String path;


    public DelTreeCommand(String path) {
        this.path = path;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if(clientThread.getUser()==null)
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);

        Directory containingDirectory = virtualFileSystem.getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        Directory directory = virtualFileSystem.getDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());

        String nameRemoveDirectory = virtualFileSystem.getNewObjectName(path);
        String responseError = virtualFileSystem.checkPassRemoveDirectory(directory, clientThread.getUser().getCurrentDirectory());
        if (responseError != null)
            return new ErrorResponse(clientThread, responseError);

        if (virtualFileSystem.removeDirectory(containingDirectory, nameRemoveDirectory)) {
            String responseAllUser = "delete directory " + nameRemoveDirectory;
            return new ChangeSystemResponse(responseAllUser, virtualFileSystem.getListClient(), clientThread, clientThread.getUser());
        } else
            return new ErrorResponse(clientThread, ERROR_DELETE_DIRECTORY_WITH_LOCK_FILE);
    }
}
