import java.io.Serializable;


public class CDCommand implements Command,Serializable {
    private String path;

    public CDCommand(String path) {
        this.path = path;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if(clientThread.getUser()==null)
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
        Directory directory = virtualFileSystem.getDirectoryFromPath(path,clientThread.getUser().getCurrentDirectory());
        if (directory == null)
            return new ErrorResponse(clientThread, ERROR_PATH);

        clientThread.getUser().getCurrentDirectory().userExit(clientThread.getUser());
        directory.userEnter(clientThread.getUser());
        clientThread.getUser().setCurrentDirectory(directory);
        return null;
    }
}
