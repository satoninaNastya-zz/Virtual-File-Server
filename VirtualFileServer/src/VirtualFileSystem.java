import java.util.ArrayList;


public class VirtualFileSystem {

    private Directory rootDirectory;
    private static ArrayList<ClientThread> clientsThreads;
    private static ArrayList<User> users = new ArrayList<User>();
    private final static String PATH_DEL = "\\\\";
    private final static String ERROR_PATH = "Error, incorrect path";
    private final static String ERROR_DELETE_DISK_C = "Error, you can not remove disk C";
    private final static String ERROR_DELETE_CURRENT_DIRECTORY = "Error, you can not remove current directory";
    private final static String ERROR_THIS_USER = "Error, in this directory are users ";

    public VirtualFileSystem(String rootDirectoryName, ArrayList<ClientThread> listClientOnline) {
        this.rootDirectory = new Directory(rootDirectoryName);
        clientsThreads = listClientOnline;
    }
    public void removeUser(User user){
        users.remove(user);
    }
    public void removeClientThread(ClientThread client){
        clientsThreads.remove(client);
    }
    public void addClientThread(ClientThread client){
        clientsThreads.add(client);
    }

    public Directory getRootDirectory() {
        return rootDirectory;
    }

    public ArrayList<ClientThread> getListClient() {
        return clientsThreads;
    }


    public int getNumberClientOnline() {
        return users.size();
}

    public void clear(){
        rootDirectory.clear();
    }

//пользователь с таким именем уже существует
    private boolean isExistUserWithName(String userName) {
        for (User anUsersOnline : users) {
            if (anUsersOnline.getName().equals(userName))
                return true;
        }
        return false;
    }

    public boolean addNewUser(User newUser) {
        synchronized (users) {
            if (isExistUserWithName(newUser.getName()))
                return false;
            rootDirectory.userEnter(newUser);
            newUser.setCurrentDirectory(rootDirectory);
            users.add(newUser);
            return true;
        }
    }

    //возвращает дирректорию из пути содержащую конечную либо текущую директорию пользователя
    public Directory getContainingDirectoryFromPath(String path, Directory currentDirectory) {
        if (path.equals("C:"))
            return rootDirectory;

        String[] pathDirectory = path.split(PATH_DEL);
        int pathLength = pathDirectory.length - 1;
        if (!pathDirectory[0].equals("C:") && pathLength == 0)
            return currentDirectory;

        return getDirectory(pathDirectory, pathLength);
    }
//возвращает коннечную дирректорию из пути, либо текущую директорию пользователя
    public Directory getDirectoryFromPath(String path, Directory currentDirectory) {
        String[] pathDirectory = path.split(PATH_DEL);
        int pathLength = pathDirectory.length;

        if (!pathDirectory[0].equals("C:")) {
            if (pathDirectory.length == 1) {
                return getDirectory(currentDirectory, pathDirectory[0]);
            }
            return null;
        }
        return getDirectory(pathDirectory, pathLength);
    }

//возвращает поддиректорию из директории
    public Directory getDirectory(Directory dir, String name) {

        for (int i = 0; i < dir.getNumberContainsDirectory(); i++)
            if (dir.getNameDirectory(i).equals(name))
                return dir.getDirectory(i);

        return null;
    }

    //возвращает директорию по указанному номеру из пути
    private Directory getDirectory(String[] pathDirectory, int num) {
        boolean isFindDirectory = false;
        Directory returnDirectory = rootDirectory;

        for (int i = 1; i < num; i++) {
            for (int j = 0; j < returnDirectory.getNumberContainsDirectory(); j++) {
                if (returnDirectory.getNameDirectory(j).equals(pathDirectory[i])) {
                    returnDirectory = returnDirectory.getDirectory(j);
                    isFindDirectory = true;
                    break;
                } else isFindDirectory = false;
            }
            if (!isFindDirectory)
                return null;
        }
        return returnDirectory;
    }

    public String getNewObjectName(String path) {
        String[] pathDirectory = path.split(PATH_DEL);
        return pathDirectory[pathDirectory.length - 1];
    }

    // папка или файл с таким именем уже существует
    public Boolean isExistObjectWithName(Directory dir, String nameObject) {
        for (int i = 0; i < dir.getNumberContainsDirectory(); i++)
            if (dir.getNameDirectory(i).equals(nameObject))
                return true;

        for (int i = 0; i < dir.getNumberContainsFile(); i++)
            if (dir.getNameFile(i).equals(nameObject))
                return true;

        return false;
    }


    //Возвращает директорию с именем name, из директории dir\
    public String checkPassRemoveDirectory(Directory removeDirectory, Directory currentDirectory) {
        if (removeDirectory == null)
            return ERROR_PATH;

        if (removeDirectory == rootDirectory)
            return ERROR_DELETE_DISK_C;

        if (removeDirectory == currentDirectory)
            return ERROR_DELETE_CURRENT_DIRECTORY;

        if (!isNotUsersNow(removeDirectory))
            return ERROR_THIS_USER;

        return null;
    }

//ни один пользователь не находится в директории и ее поддиректориях
    private boolean isNotUsersNow(Directory directory) {
        boolean isNot = directory.isNotUsers();
            for (int i = 0; i < directory.getNumberContainsDirectory(); i++) {
                isNot = isNotUsersNow(directory.getDirectory(i));
                if (!isNot) {
                    return false;
                }
            }
        return isNot;
    }


    public Boolean removeDirectory(Directory containingDirectory, String name) {

        Directory removingDirectory = getDirectory(containingDirectory, name);
        if (!isContainsLockedFile(removingDirectory)) {
            containingDirectory.removeDirectory(removingDirectory);
            return true;
        } else
            return false;
    }
//папка содержит блокированные файлы
    private boolean isContainsLockedFile(Directory directory) {

        for (int i = 0; i < directory.getNumberContainsFile(); i++)
            if (!directory.isFileNotLock(i))
                return true;

        for (int i = 0; i < directory.getNumberContainsDirectory(); i++)
            return isContainsLockedFile(directory.getDirectory(i));

        return false;
    }

    public File getFile(Directory dir, String nameFile) {
        if (dir.isNotContainsFile()) return null;
        for (int i = 0; i < dir.getNumberContainsFile(); i++)
            if (dir.getNameFile(i).equals(nameFile))
                return dir.getFile(i);

        return null;
    }

    public boolean removeFile(Directory dir, String name) {
        for (int i = 0; i < dir.getNumberContainsFile(); i++)
            if (dir.getNameFile(i).equals(name))
                if (dir.isFileNotLock(i)) {
                    dir.removeFile(i);
                    return true;
                }
        return false;
    }

    public Directory copyDirectory(Directory dir) {
        Directory newDirectory = new Directory(dir.getName());
            for (int i = 0; i < dir.getNumberContainsFile(); i++) {
                newDirectory.newFile(copyFile(dir.getFile(i)));
            }
            for (int i = 0; i < dir.getNumberContainsDirectory(); i++) {
                newDirectory.newDirectory(copyDirectory(dir.getDirectory(i)));
            }
        return newDirectory;
    }

    public File copyFile(File file) {
        File newFile = new File(file.getName());
            newFile.cleanLock();
        return newFile;
    }
}
