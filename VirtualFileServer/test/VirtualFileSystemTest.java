import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class VirtualFileSystemTest {
    private static final ArrayList<ClientThread> userOnline = new ArrayList<ClientThread>();
    private static final VirtualFileSystem fileSystem = new VirtualFileSystem("C:", userOnline);
    private static final String newUserName = "TestUser";
    private static int colUser = 1;

    private ClientThread getNewClientThread() throws Exception {

        return new ClientThread(null, fileSystem) {
            @Override
            public void run() {
                //do nothing
            }
        };
    }

    private User getNewUser() {
        if (colUser == 1) {
            return new User(newUserName);
        }
        return new User(newUserName + colUser);
    }

    @After
    public void cleanSystem() {
        fileSystem.cleanSystem();
        colUser = 1;
    }

    @Test
    public void testConnectUser() throws Exception {
        User newUser = getNewUser();
        fileSystem.connectUser(newUser, getNewClientThread());
        ArrayList<String> onlineUsers = fileSystem.getUserNames();
        assertFalse(!"C:".equals(newUser.getCurrentDirectory().getName()));
        for (String user : onlineUsers) {
            assertTrue(newUserName.equals(user));
        }
    }

    @Test
    public void testQuitUser() throws Exception {
        ClientThread clientThread = getNewClientThread();
        fileSystem.connectUser(getNewUser(), clientThread);
        fileSystem.quitUser(clientThread);
        ArrayList<String> onlineUsers = fileSystem.getUserNames();
        System.out.println(onlineUsers.size());
        for (String user : onlineUsers) {
            assertFalse(newUserName.equals(user));
        }
    }

    private ClientThread connectNewTestUser() throws Exception {
        ClientThread clientThread = getNewClientThread();
        fileSystem.connectUser(getNewUser(), clientThread);
        colUser++;
        return clientThread;
    }

    @Test
    public void testMakeDirectory() throws Exception {
        ClientThread clientThread = connectNewTestUser();
        fileSystem.makeDirectory("testDirectory", clientThread);
        assertTrue(isExistInRootDirectoryDirectory("testDirectory"));
    }

    @Test
    public void testChangeDirectory() throws Exception {
        ClientThread clientThread = connectNewTestUser();
        fileSystem.makeDirectory("testDirectory", clientThread);
        fileSystem.changeUserDirectory("testDirectory", clientThread);
        assertTrue("testDirectory".equals(clientThread.getUser().getCurrentDirectory().getName()));
    }

    private boolean isExistInRootDirectoryDirectory(String nameDirectory) {
        boolean isExist = false;
        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsDirectory(); i++)
            if (nameDirectory.equals(fileSystem.getRootDirectory().getDirectory(i).getName())) {
                isExist = true;
            }
        return isExist;
    }

    private boolean isExistInRootDirectoryFile(String nameFile) {
        boolean isExist = false;
        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsFile(); i++)
            if (nameFile.equals(fileSystem.getRootDirectory().getFile(i).getName())) {
                isExist = true;
            }
        return isExist;
    }

    @Test
    public void testDeleteTreeDirectory() throws Exception {
        ClientThread clientThread = connectNewTestUser();
        fileSystem.makeDirectory("testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));
//удалить директорию
        fileSystem.deleteTreeDirectory("testDirectory", clientThread);
        assertFalse(isExistInRootDirectoryDirectory("testDirectory"));
//удалить дерево директорий
        fileSystem.makeDirectory("testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));
        fileSystem.makeDirectory("C:\\testDirectory\\testDirectory2", clientThread);
        fileSystem.deleteTreeDirectory("testDirectory", clientThread);
        assertFalse(isExistInRootDirectoryDirectory("testDirectory"));
//нельзя удалить дерево деррикторий в котором находится хотя бы один пользователь
        fileSystem.makeDirectory("testDirectory", clientThread);
        fileSystem.makeDirectory("C:\\testDirectory\\testDirectory2", clientThread);
        fileSystem.changeUserDirectory("C:\\testDirectory\\testDirectory2", clientThread);
        fileSystem.deleteTreeDirectory("testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));
//нельзя удалить  деррикторию(дерево дирректорий) в которой находятся блокированные файлы
        cleanSystem();
        clientThread = connectNewTestUser();
        fileSystem.makeDirectory("testDirectory", clientThread);
        fileSystem.makeFile("C:\\testDirectory\\testFile", clientThread);
        fileSystem.lockFile("C:\\testDirectory\\testFile", clientThread);
        fileSystem.deleteTreeDirectory("testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));
//нельзя удалить  дерево дирректорийв котором находятся блокированные файлы
        cleanSystem();
        clientThread = connectNewTestUser();
        fileSystem.makeDirectory("testDirectory", clientThread);
        fileSystem.makeDirectory("C:\\testDirectory\\tesDirectory2", clientThread);
        fileSystem.makeFile("C:\\testDirectory\\tesDirectory2\\testFile", clientThread);
        fileSystem.lockFile("C:\\testDirectory\\tesDirectory2\\testFile", clientThread);
        fileSystem.deleteTreeDirectory("testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));
    }

    @Test
    public void testRemoveDirectory() throws Exception {

        ClientThread clientThread = connectNewTestUser();
        //удалить директорию
        fileSystem.makeDirectory("testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));
        fileSystem.removeDirectory("testDirectory", clientThread);
        assertFalse(isExistInRootDirectoryDirectory("testDirectory"));
        //нельзя удалить дерево деррикторий
        fileSystem.makeDirectory("testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));
        fileSystem.makeDirectory("C:\\testDirectory\\testDirectory2", clientThread);
        fileSystem.removeDirectory("testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));
        cleanSystem();
        clientThread = connectNewTestUser();
        //нельзя удалить  деррикторию в которой находится пользователь
        fileSystem.makeDirectory("testDirectory", clientThread);
        fileSystem.changeUserDirectory("testDirectory", clientThread);
        fileSystem.removeDirectory("testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));
        //нельзя удалить  деррикторию в которой находятся блокированные файлы
        cleanSystem();
        clientThread = connectNewTestUser();
        fileSystem.makeDirectory("testDirectory", clientThread);
        fileSystem.makeFile("C:\\testDirectory\\testFile", clientThread);
        fileSystem.lockFile("C:\\testDirectory\\testFile", clientThread);
        assertFalse(!isExistInRootDirectoryDirectory("testDirectory"));

    }

    @Test
    public void testMakeFile() throws Exception {
        ClientThread clientThread = connectNewTestUser();
        fileSystem.makeFile("testFile", clientThread);
        assertTrue(isExistInRootDirectoryFile("testFile"));
    }


    @Test
    public void testRemoveFile() throws Exception {
        ClientThread clientThread = connectNewTestUser();
        fileSystem.makeFile("testFile", clientThread);
        fileSystem.removeFile("testFile", clientThread);
        assertFalse(isExistInRootDirectoryFile("testFile"));

        fileSystem.makeFile("testFile", clientThread);
        fileSystem.lockFile("testFile", clientThread);
        fileSystem.removeFile("testFile", clientThread);
        assertFalse(!isExistInRootDirectoryFile("testFile"));

        fileSystem.unLockFile("testFile", clientThread);
        fileSystem.removeFile("testFile", clientThread);
        assertFalse(isExistInRootDirectoryFile("testFile"));
    }

    private boolean fileIsLock(String fileName) {
        boolean isLock = false;
        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsFile(); i++) {
            if (fileName.equals(fileSystem.getRootDirectory().getFile(i).getName())) {
                if (!fileSystem.getRootDirectory().getFile(i).isEmptyLocks()) {
                    isLock = true;
                }
            }
        }
        return isLock;
    }

    @Test
    public void testLockFile() throws Exception {
        ClientThread clientThread = connectNewTestUser();
        fileSystem.makeFile("testFile", clientThread);
        fileSystem.lockFile("testFile", clientThread);
        assertTrue(fileIsLock("testFile"));
    }

    @Test
    public void testUnLockFile() throws Exception {
        ClientThread clientThread = connectNewTestUser();
        fileSystem.makeFile("testFile", clientThread);
        fileSystem.lockFile("testFile", clientThread);
        fileSystem.unLockFile("testFile", clientThread);
        assertFalse(fileIsLock("testFile"));
        // другой пользователь не может разблокировать файл
        fileSystem.lockFile("testFile", clientThread);
        ClientThread clientThread2 = connectNewTestUser();
        fileSystem.unLockFile("testFile", clientThread2);
        assertFalse(!fileIsLock("testFile"));

    }

    @Test
    public void testMove() throws Exception {
        ClientThread clientThread = connectNewTestUser();
        fileSystem.makeDirectory("testSourceDirectory", clientThread);
        fileSystem.makeDirectory("C:\\testSourceDirectory\\testDirectory2", clientThread);
        fileSystem.makeDirectory("testDestinationDirectory", clientThread);
        fileSystem.move("testSourceDirectory", "testDestinationDirectory", clientThread);
        assertFalse(!fileSystem.isExistDirectoryFromPath("C:\\testDestinationDirectory\\testSourceDirectory\\testDirectory2"));
        assertFalse(fileSystem.isExistDirectoryFromPath("C:\\testSourceDirectory\\testDirectory2"));
        cleanSystem();
        //нельзя переместить пользователя
        clientThread = connectNewTestUser();
        ClientThread clientThread2 = connectNewTestUser();
        fileSystem.makeDirectory("testSourceDirectory", clientThread);
        fileSystem.makeDirectory("testDestinationDirectory", clientThread);

        fileSystem.changeUserDirectory("testSourceDirectory", clientThread2);

        fileSystem.move("testSourceDirectory", "testDestinationDirectory", clientThread);
        assertFalse(!fileSystem.isExistDirectoryFromPath("C:\\testSourceDirectory"));
        assertFalse(fileSystem.isExistDirectoryFromPath("C:\\testDestinationDirectory\\testSourceDirectory"));

        cleanSystem();
        clientThread = connectNewTestUser();
        fileSystem.makeDirectory("testDirectory", clientThread);
        fileSystem.makeFile("file", clientThread);
        fileSystem.move("file", "testDirectory", clientThread);
        assertFalse(isExistInRootDirectoryFile("file"));
//нельзя переместить заблокированный файл
        fileSystem.makeFile("fileLocked", clientThread);
        fileSystem.lockFile("fileLocked", clientThread);
        fileSystem.move("fileLocked", "testDirectory", clientThread);
        assertFalse(!isExistInRootDirectoryFile("fileLocked"));
//разблокировали и можно
        fileSystem.unLockFile("fileLocked", clientThread);
        fileSystem.move("fileLocked", "testDirectory", clientThread);
        assertFalse(isExistInRootDirectoryFile("fileLocked"));
    }

    @Test
    public void testCopy() throws Exception {
        ClientThread clientThread = connectNewTestUser();
        fileSystem.makeDirectory("testSourceDirectory", clientThread);
        fileSystem.makeDirectory("C:\\testSourceDirectory\\testDirectory2", clientThread);
        fileSystem.makeDirectory("testDestinationDirectory", clientThread);
        fileSystem.copy("testSourceDirectory", "testDestinationDirectory", clientThread);
        assertFalse(!fileSystem.isExistDirectoryFromPath("C:\\testDestinationDirectory\\testSourceDirectory\\testDirectory2"));
        cleanSystem();
        clientThread = connectNewTestUser();
        fileSystem.makeFile("sourceFile", clientThread);
        fileSystem.makeDirectory("destinationDirectory", clientThread);
        fileSystem.copy("sourceFile", "destinationDirectory", clientThread);
        assertFalse(!fileSystem.isExistFileFromPath("C:\\destinationDirectory\\sourceFile"));
        //нет повторяющихся имен
        fileSystem.copy("sourceFile", "destinationDirectory", clientThread);
        assertFalse(!fileSystem.isExistFileFromPath("C:\\destinationDirectory\\sourceFile_1"));
        //нет повторяющихся имен
        fileSystem.copy("sourceFile", "destinationDirectory", clientThread);
        assertFalse(!fileSystem.isExistFileFromPath("C:\\destinationDirectory\\sourceFile_2"));

    }


}

