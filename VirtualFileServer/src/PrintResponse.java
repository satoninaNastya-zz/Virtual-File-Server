
public class PrintResponse implements Response {
    VirtualFileSystem virtualFileSystem;
    ClientThread clientThread;

    public PrintResponse(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        this.virtualFileSystem = virtualFileSystem;
        this.clientThread = clientThread;

    }

    @Override
    public void send() {
        virtualFileSystem.printFileSystem(clientThread);
    }
}