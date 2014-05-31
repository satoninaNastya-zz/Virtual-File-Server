import java.io.Serializable;


public class MFCommand implements Command,Serializable {
    private String path;

    public MFCommand(String path) {
        this.path = path;
    }

    @Override
    public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        if(clientThread.getUser()==null) {
            return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
        }

        Directory containingDirectory = virtualFileSystem.getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        if (containingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_PATH);
        }
        String nameFile = virtualFileSystem.getNewObjectName(path);
        if (virtualFileSystem.isExistObjectWithName(containingDirectory, nameFile)) {
            return new ErrorResponse(clientThread, ERROR_NAME_EXIST);
        }

        File newFile = new File(nameFile);
        containingDirectory.newFile(newFile);
        String responseAllUser = "create file " + nameFile;
        return new ChangeSystemResponse(responseAllUser, virtualFileSystem.getListClient(), clientThread, clientThread.getUser());
    }
}
