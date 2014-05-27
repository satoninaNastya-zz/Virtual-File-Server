
public interface Command {
    final static String ERROR_NOT_CONNECT = "Error, you not connected";
    final static String ERROR_NAME_EXIST = "Error, this name is already exist";

    final static String ERROR_FILE_NOT_FOUND = "Error, file not founded";
    final static String ERROR_PATH = "Error, incorrect path";
    final static String ERROR_SOURCE_PATH = "Error, incorrect source path";
    final static String ERROR_DESTINATION_PATH = "Error, incorrect destination path";

    final static String ERROR_FILE_LOCKED = "Error, file locked ";
    final static String ERROR_DELETE_DIRECTORY_WITH_LOCK_FILE = "Error, you can not remove directory which contains locked file";
    final static String ERROR_FILE_ALREADY_LOCKED = "Error, you already locked this file";
    final static String ERROR_MOVE_FILE = "You can not move locked file";
    final static String ERROR_UNLOCK = "Error, you can not unlock this file";

    final static String ERROR_MOVE_DIRECTORY = "You can not move this directory";
    final static String ERROR_DELETE_DIRECTORY = "Error, you can not remove directory containing other directories";

    Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread);
}
