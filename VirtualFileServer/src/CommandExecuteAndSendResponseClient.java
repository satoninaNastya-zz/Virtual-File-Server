
public class CommandExecuteAndSendResponseClient {
    private VirtualFileSystem theVirtualFileSystem;
    private ClientThread theClientThread;

    public CommandExecuteAndSendResponseClient(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        this.theVirtualFileSystem = virtualFileSystem;
        theClientThread = clientThread;
    }

    public void execute(Command command) {
        Response responseClient = command.execute(theVirtualFileSystem, theClientThread);
        if (responseClient != null) {
            responseClient.send();
        }
    }
}
