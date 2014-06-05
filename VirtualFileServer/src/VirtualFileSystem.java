import java.util.ArrayList;


public class VirtualFileSystem {

    private Directory rootDirectory;
    private static ArrayList<ClientThread> clientsThreads;
    private final static ArrayList<User> users = new ArrayList<User>();
    private final static String PATH_DEL = "\\\\";

    private final static String ERROR_DELETE_DISK_C = "Error, you can not remove disk C";
    private final static String ERROR_DELETE_CURRENT_DIRECTORY = "Error, you can not remove current directory";
    private final static String ERROR_THIS_USER = "Error, in this directory are users ";
    private final static String ERROR_NAME_EXIST = "Error, this name is already exist";

    private final static String ERROR_FILE_NOT_FOUND = "Error, file not founded";
    private final static String ERROR_PATH = "Error, incorrect path";
    private final static String ERROR_SOURCE_PATH = "Error, incorrect source path";
    private final static String ERROR_DESTINATION_PATH = "Error, incorrect destination path";

    private final static String ERROR_FILE_LOCKED = "Error, file locked ";
    private final static String ERROR_DELETE_DIRECTORY_WITH_LOCK_FILE = "Error, you can not remove directory which contains locked file";
    private final static String ERROR_FILE_ALREADY_LOCKED = "Error, you already locked this file";
    private final static String ERROR_MOVE_FILE = "You can not move locked file";
    private final static String ERROR_UNLOCK = "Error, you can not unlock this file";

    private final static String ERROR_MOVE_DIRECTORY = "You can not move this directory";
    private final static String ERROR_DELETE_DIRECTORY = "Error, you can not remove directory containing other directories";


    public VirtualFileSystem(String rootDirectoryName, ArrayList<ClientThread> listClientOnline) {
        this.rootDirectory = new Directory(rootDirectoryName);
        clientsThreads = listClientOnline;
    }

    public synchronized Response connectUser(User user, ClientThread clientThread) {
        if (addNewUser(user)) {
            clientThread.setUser(user);
            int numberClientOnline = getNumberClientOnline();
            clientThread.getUser().setCurrentDirectory(getRootDirectory());
            return new ConnectResponse(clientThread, numberClientOnline);
        } else
            return new ErrorResponse(clientThread, ERROR_NAME_EXIST);
    }

    public synchronized Response quitUser(ClientThread clientThread) {
        removeUser(clientThread.getUser());
        removeClientThread(clientThread);
        clientThread.setUser(null);
        clientThread.interrupt();
        return new QuitResponse(clientThread);
    }

    public synchronized Response makeDirectory(String path, ClientThread clientThread) {

        Directory containingDirectory = getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        if (containingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_PATH);
        }

        String nameNewDirectory = getNewObjectName(path);
        if (isExistObjectWithName(containingDirectory, nameNewDirectory)) {
            return new ErrorResponse(clientThread, ERROR_NAME_EXIST);
        }

        Directory newDirectory = new Directory(nameNewDirectory);
        containingDirectory.newDirectory(newDirectory);

        String responseAllUser = "make directory " + nameNewDirectory;
        return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
    }

    public synchronized Response changeUserDirectory(String path, ClientThread clientThread) {
        Directory directory = getDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        if (directory == null) {
            return new ErrorResponse(clientThread, ERROR_PATH);
        }
        clientThread.getUser().getCurrentDirectory().userExit(clientThread.getUser());
        directory.userEnter(clientThread.getUser());
        clientThread.getUser().setCurrentDirectory(directory);
        String responseAllUser = "located in a directory " + directory.getName();
        return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);

    }

    public synchronized Response deleteTreeDirectory(String path, ClientThread clientThread) {
        Directory containingDirectory = getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        Directory directory = getDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());

        String nameRemoveDirectory = getNewObjectName(path);
        String responseError = checkPassRemoveDirectory(directory, clientThread.getUser().getCurrentDirectory());
        if (responseError != null) {
            return new ErrorResponse(clientThread, responseError);
        }

        if (removeDirectory(containingDirectory, nameRemoveDirectory)) {
            String responseAllUser = "delete directory " + nameRemoveDirectory;
            return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
        } else {
            return new ErrorResponse(clientThread, ERROR_DELETE_DIRECTORY_WITH_LOCK_FILE);
        }
    }

    public synchronized Response removeDirectory(String path, ClientThread clientThread) {
        Directory containingDirectory = getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        Directory directory = getDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        String nameRemoveDirectory = getNewObjectName(path);

        String responseError = checkPassRemoveDirectory(directory, clientThread.getUser().getCurrentDirectory());
        if (responseError != null) {
            return new ErrorResponse(clientThread, responseError);
        }

        if (!directory.isNotContainsDirectory()) {
            return new ErrorResponse(clientThread, ERROR_DELETE_DIRECTORY);
        }

        if (removeDirectory(containingDirectory, nameRemoveDirectory)) {
            String responseAllUser = "delete directory " + nameRemoveDirectory;
            return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
        } else {
            return new ErrorResponse(clientThread, ERROR_DELETE_DIRECTORY_WITH_LOCK_FILE);
        }
    }

    public synchronized Response makeFile(String path, ClientThread clientThread) {
        Directory containingDirectory = getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        if (containingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_PATH);
        }
        String nameFile = getNewObjectName(path);
        if (isExistObjectWithName(containingDirectory, nameFile)) {
            return new ErrorResponse(clientThread, ERROR_NAME_EXIST);
        }

        File newFile = new File(nameFile);
        containingDirectory.newFile(newFile);
        String responseAllUser = "create file " + nameFile;
        return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
    }

    public synchronized Response removeFile(String path, ClientThread clientThread) {
        Directory containingDirectory = getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        if (containingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_PATH);
        }
        String nameFile = getNewObjectName(path);
        File file = getFile(containingDirectory, nameFile);

        if (file == null) {
            return new ErrorResponse(clientThread, ERROR_FILE_NOT_FOUND);
        }
        if (!removeFile(containingDirectory, nameFile)) {
            return new ErrorResponse(clientThread, ERROR_FILE_LOCKED);
        } else {
            String responseAllUser = "remove file " + nameFile;
            return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
        }
    }

    public synchronized Response lockFile(String path, ClientThread clientThread) {
        Directory containingDirectory = getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        if (containingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_PATH);
        }
        String nameFile = getNewObjectName(path);

        File file = getFile(containingDirectory, nameFile);
        if (file == null) {
            return new ErrorResponse(clientThread, ERROR_FILE_NOT_FOUND);
        }

        if (!file.lockFile(clientThread.getUser().getName())) {
            return new ErrorResponse(clientThread, ERROR_FILE_ALREADY_LOCKED);
        } else {
            String responseAllUser = "locked file " + nameFile;
            return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
        }
    }


    public synchronized Response unLockFile(String path, ClientThread clientThread) {
        Directory containingDirectory = getContainingDirectoryFromPath(path, clientThread.getUser().getCurrentDirectory());
        if (containingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_PATH);
        }
        String nameFile = getNewObjectName(path);

        File file = getFile(containingDirectory, nameFile);
        if (file == null) {
            return new ErrorResponse(clientThread, ERROR_FILE_NOT_FOUND);
        }

        if (file.unlockFile(clientThread.getUser().getName())) {
            String responseAllUser = "unlocked file " + nameFile;
            return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);

        } else {
            return new ErrorResponse(clientThread, ERROR_UNLOCK);
        }
    }

    public synchronized Response move(String sourcePath, String destinationPath, ClientThread clientThread) {
        Directory destinationDirectory = getDirectoryFromPath(destinationPath, clientThread.getUser().getCurrentDirectory());
        if (destinationDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_DESTINATION_PATH);
        }

        Directory sourceContainingDirectory = getContainingDirectoryFromPath(sourcePath, clientThread.getUser().getCurrentDirectory());
        if (sourceContainingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_SOURCE_PATH);
        }

        String nameSourceObject = getNewObjectName(sourcePath);
        File sourceFile = getFile(sourceContainingDirectory, nameSourceObject);
        if (sourceFile == null) {
            Directory sourceDirectory = getDirectory(sourceContainingDirectory, nameSourceObject);
            String responseError = checkPassRemoveDirectory(sourceDirectory, clientThread.getUser().getCurrentDirectory());

            if (responseError != null) {
                return new ErrorResponse(clientThread, responseError);
            }


            if (removeDirectory(sourceContainingDirectory, nameSourceObject)) {
                while (isExistObjectWithName(destinationDirectory, sourceDirectory.getName())) {
                    sourceDirectory.changeDoubleName();
                }
                destinationDirectory.newDirectory(sourceDirectory);
                String responseAllUser = "move directory " + nameSourceObject;
                return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
            } else {
                return new ErrorResponse(clientThread, ERROR_MOVE_DIRECTORY);
            }
        } else {
            if (removeFile(sourceContainingDirectory, nameSourceObject)) {

                while (isExistObjectWithName(destinationDirectory, sourceFile.getName())) {
                    sourceFile.changeDoubleName();
                }
                destinationDirectory.newFile(sourceFile);
                String responseAllUser = "move file " + nameSourceObject;
                return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
            } else {
                return new ErrorResponse(clientThread, ERROR_MOVE_FILE);
            }
        }
    }

    public synchronized Response copy(String sourcePath, String destinationPath, ClientThread clientThread) {

        Directory destinationDirectory = getDirectoryFromPath(destinationPath, clientThread.getUser().getCurrentDirectory());
        if (destinationDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_DESTINATION_PATH);
        }

        Directory sourceContainingDirectory = getContainingDirectoryFromPath(sourcePath, clientThread.getUser().getCurrentDirectory());
        if (sourceContainingDirectory == null) {
            return new ErrorResponse(clientThread, ERROR_SOURCE_PATH);
        }

        String nameSourceObject = getNewObjectName(sourcePath);
        File sourceFile = getFile(sourceContainingDirectory, nameSourceObject);
        if (sourceFile == null) {
            Directory sourceDirectory = getDirectory(sourceContainingDirectory, nameSourceObject);
            if (sourceDirectory == null) {
                return new ErrorResponse(clientThread, ERROR_SOURCE_PATH);
            }
            Directory copyDirectory = copyDirectory(sourceDirectory);
            while (isExistObjectWithName(destinationDirectory, copyDirectory.getName())) {
                copyDirectory.changeDoubleName();
            }
            destinationDirectory.newDirectory(copyDirectory);
            String responseAllUser = "copy directory " + nameSourceObject;
            return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
        } else {
            File copyFile = copyFile(sourceFile);
            while (isExistObjectWithName(destinationDirectory, copyFile.getName())) {
                copyFile.changeDoubleName();
            }
            destinationDirectory.newFile(copyFile);
            String responseAllUser = "copy file " + nameSourceObject;
            return new ChangeSystemResponse(responseAllUser, getListClient(), clientThread);
        }
    }

    public synchronized void printFileSystem(ClientThread clientThread) {
        rootDirectory.sortDirectory(rootDirectory);
        print(rootDirectory, 0, clientThread);
    }


    private void removeUser(User user) {
        users.remove(user);
    }

    private void removeClientThread(ClientThread client) {
        clientsThreads.remove(client);
    }

    public Directory getRootDirectory() {
        return rootDirectory;
    }

    private ArrayList<ClientThread> getListClient() {
        return clientsThreads;
    }

    private int getNumberClientOnline() {
        return users.size();
    }

    //пользователь с таким именем уже существует
    private boolean isExistUserWithName(String userName) {
        for (User anUsersOnline : users) {
            if (anUsersOnline.getName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    private boolean addNewUser(User newUser) {
        synchronized (users) {
            if (isExistUserWithName(newUser.getName())) {
                return false;
            }
            rootDirectory.userEnter(newUser);
            newUser.setCurrentDirectory(rootDirectory);
            users.add(newUser);
            return true;
        }
    }

    //возвращает дирректорию из пути содержащую конечную либо текущую директорию пользователя
    private Directory getContainingDirectoryFromPath(String path, Directory currentDirectory) {
        if (path.equals("C:")) {
            return rootDirectory;
        }

        String[] pathDirectory = path.split(PATH_DEL);
        int pathLength = pathDirectory.length - 1;
        if (!pathDirectory[0].equals("C:") && pathLength == 0) {
            return currentDirectory;
        }

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
    private Directory getDirectory(Directory dir, String name) {

        for (int i = 0; i < dir.getNumberContainsDirectory(); i++) {
            if (dir.getNameDirectory(i).equals(name)) {
                return dir.getDirectory(i);
            }
        }

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
            if (!isFindDirectory) {
                return null;
            }
        }
        return returnDirectory;
    }

    public boolean isExistDirectoryFromPath(String path) {
        String[] pathDirectory = path.split(PATH_DEL);
        int num = pathDirectory.length;
        boolean isFindDirectory = false;
        Directory returnDirectory = rootDirectory;

        for (int i = 1; i < num; i++) {
            isFindDirectory = false;
            for (int j = 0; j < returnDirectory.getNumberContainsDirectory(); j++) {
                if (returnDirectory.getNameDirectory(j).equals(pathDirectory[i])) {
                    returnDirectory = returnDirectory.getDirectory(j);
                    isFindDirectory = true;
                    break;
                }
            }
        }
        return isFindDirectory;
    }

    public boolean isExistFileFromPath(String path) {
        String[] pathDirectory = path.split(PATH_DEL);
        Directory containsDirectory = getDirectory(pathDirectory, pathDirectory.length - 1);
        if (containsDirectory == null) {
            return false;
        }
        for (int i = 0; i < containsDirectory.getNumberContainsFile(); i++) {
            if (pathDirectory[pathDirectory.length - 1].equals(containsDirectory.getFile(i).getName()))
                return true;
        }
        return false;
    }


    private String getNewObjectName(String path) {
        String[] pathDirectory = path.split(PATH_DEL);
        return pathDirectory[pathDirectory.length - 1];
    }

    // папка или файл с таким именем уже существует
    private Boolean isExistObjectWithName(Directory dir, String nameObject) {
        for (int i = 0; i < dir.getNumberContainsDirectory(); i++) {
            if (dir.getNameDirectory(i).equals(nameObject)) {
                return true;
            }
        }
        for (int i = 0; i < dir.getNumberContainsFile(); i++) {
            if (dir.getNameFile(i).equals(nameObject)) {
                return true;
            }
        }
        return false;
    }

    //Возвращает директорию с именем name, из директории dir\
    private String checkPassRemoveDirectory(Directory removeDirectory, Directory currentDirectory) {
        if (removeDirectory == null) {
            return ERROR_PATH;
        }

        if (removeDirectory == rootDirectory) {
            return ERROR_DELETE_DISK_C;
        }

        if (removeDirectory == currentDirectory) {
            return ERROR_DELETE_CURRENT_DIRECTORY;
        }

        if (!isNotUsersNow(removeDirectory)) {
            return ERROR_THIS_USER;
        }

        return null;
    }

    //ни один пользователь не находится в директории и ее поддиректориях
    private synchronized boolean isNotUsersNow(Directory directory) {
        boolean isNot = directory.isNotUsers();
        for (int i = 0; i < directory.getNumberContainsDirectory(); i++) {
            isNot = isNotUsersNow(directory.getDirectory(i));
            if (!isNot) {
                return false;
            }
        }
        return isNot;
    }

    private Boolean removeDirectory(Directory containingDirectory, String name) {

        Directory removingDirectory = getDirectory(containingDirectory, name);
        if (!isContainsLockedFile(removingDirectory)) {
            containingDirectory.removeDirectory(removingDirectory);
            return true;
        } else
            return false;
    }

    //папка содержит блокированные файлы
    private boolean isContainsLockedFile(Directory directory) {

        for (int i = 0; i < directory.getNumberContainsFile(); i++) {
            if (!directory.isFileNotLock(i)) {
                return true;
            }
        }
        for (int i = 0; i < directory.getNumberContainsDirectory(); i++) {
            if (isContainsLockedFile(directory.getDirectory(i))) {
                return true;
            }
        }

        return false;
    }

    private File getFile(Directory dir, String nameFile) {
        if (dir.isNotContainsFile()) {
            return null;
        }
        for (int i = 0; i < dir.getNumberContainsFile(); i++) {
            if (dir.getNameFile(i).equals(nameFile)) {
                return dir.getFile(i);
            }
        }

        return null;
    }

    private boolean removeFile(Directory dir, String name) {
        for (int i = 0; i < dir.getNumberContainsFile(); i++) {
            if (dir.getNameFile(i).equals(name)) {
                if (!dir.isFileNotLock(i)) {
                    continue;
                }
                dir.removeFile(i);
                return true;
            }
        }
        return false;
    }

    private Directory copyDirectory(Directory dir) {
        Directory newDirectory = new Directory(dir.getName());
        for (int i = 0; i < dir.getNumberContainsFile(); i++) {
            newDirectory.newFile(copyFile(dir.getFile(i)));
        }
        for (int i = 0; i < dir.getNumberContainsDirectory(); i++) {
            newDirectory.newDirectory(copyDirectory(dir.getDirectory(i)));
        }
        return newDirectory;
    }

    public void cleanSystem() {
        users.clear();
        rootDirectory.clean();
    }

    private File copyFile(File file) {
        File newFile = new File(file.getName());
        newFile.cleanLock();
        return newFile;
    }

    public synchronized ArrayList<String> getUserNames() {
        ArrayList<String> userNames = new ArrayList<String>();
        for (User user : users) {
            userNames.add(user.getName());
        }
        return userNames;
    }

    private void print(Directory dir, int level, ClientThread clientThread) {
        String str = "";
        for (int j = 0; j < level; j++) {
            str = str + " |";
        }
        if (level == 0) {
            clientThread.sendMessage(str + dir.getName());
        } else {
            clientThread.sendMessage(str + "_" + dir.getName());
        }
        level++;
        if (!dir.isNotContainsDirectory()) {
            for (int i = 0; i < dir.getNumberContainsDirectory(); i++) {
                print(dir.getDirectory(i), level, clientThread);
            }
        }
        String fileName;
        if (!dir.isNotContainsFile()) {
            for (int a = 0; a < dir.getNumberContainsFile(); a++) {
                fileName = str + " |_" + dir.getNameFile(a);
                if (!dir.isFileNotLock(a)) {
                    fileName = fileName + " [ LOCKED by";
                    for (int p = 0; p < dir.getNumberFileLocks(a); p++) {
                        fileName = fileName + " " + dir.getUserNameFileLock(a, p);
                    }
                    clientThread.sendMessage(fileName + " ]");
                } else clientThread.sendMessage(fileName);
            }
        }
    }
}










