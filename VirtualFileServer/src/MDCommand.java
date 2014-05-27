import java.io.Serializable;


public class MDCommand implements Command, Serializable {
    private String path;

    public MDCommand(String path ) {
        this.path=path;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if(clientThread.getUser()==null)
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);

        Directory containingDirectory = virtualFileSystem.getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        if (containingDirectory == null)
            return new ErrorResponse(clientThread, ERROR_PATH);

        String nameNewDirectory = virtualFileSystem.getNewObjectName(path);
        if (virtualFileSystem.isExistObjectWithName(containingDirectory, nameNewDirectory))
            return new ErrorResponse(clientThread, ERROR_NAME_EXIST);

        Directory newDirectory = new Directory(nameNewDirectory);
        containingDirectory.newDirectory(newDirectory);

        String responseAllUser = "create directory " + nameNewDirectory;
        return new ChangeSystemResponse(responseAllUser, virtualFileSystem.getListClient(), clientThread, clientThread.getUser());
    }
}
