
public interface Command {
    final static String ERROR_NOT_CONNECT = "Error, you not connected";

    Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread);
}
