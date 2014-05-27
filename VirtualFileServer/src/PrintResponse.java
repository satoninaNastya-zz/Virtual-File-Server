
public class PrintResponse implements Response {
    VirtualFileSystem virtualFileSystem;
    ClientThread clientThread;

    public PrintResponse(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
        this.virtualFileSystem = virtualFileSystem;
        this.clientThread = clientThread;

    }

    @Override
    public void send() {
        print(virtualFileSystem.getRootDirectory(), 0);
    }

    public void print(Directory dir, int level) {
        String str = "";
        for (int j = 0; j < level; j++)
            str = str + " |";
        if (level == 0) clientThread.sendMessage(str + dir.getName());
        else
            clientThread.sendMessage(str + "_" + dir.getName());
        level++;
        if (!dir.isNotContainsDirectory()) {
            for (int i = 0; i < dir.getNumberContainsDirectory(); i++)
                    print(dir.getDirectory(i), level);
        }
        String fileName;
        if (!dir.isNotContainsFile()) {
            for (int a = 0; a < dir.getNumberContainsFile(); a++) {
                fileName = str + " |_" + dir.getNameFile(a);
                if (!dir.isFileNotLock(a)) {
                    fileName = fileName + " [ LOCKED by";
                    for (int p = 0; p < dir.getNumberFileLocks(a); p++)
                        fileName = fileName + " " + dir.getUserNameFileLock(a, p);
                    clientThread.sendMessage(fileName + " ]");
                } else clientThread.sendMessage(fileName);
            }
        }
    }
}