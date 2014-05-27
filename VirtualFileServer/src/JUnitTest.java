import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JUnitTest {
    private static ArrayList<ClientThread> userOnline = new ArrayList<ClientThread>();
    private static VirtualFileSystem fileSystem = new VirtualFileSystem("C:", userOnline);


    private User testUser = new User("TestUser");
    private ClientThread client;
    private MDCommand mdTest = new MDCommand("test");
    private MDCommand mdTest2 = new MDCommand("test2");
    private MDCommand mdTest2InsideTest = new MDCommand("C:\\test\\test2");

    private CopyCommand copyTestToTest2 = new CopyCommand("test", "test2");
    private MoveCommand moveTestIntoTest2 = new MoveCommand("test", "test2");

    private CDCommand cdToTest = new CDCommand("test");

    private RDCommand rdTest = new RDCommand("test");
    private DelTreeCommand delTreeTest = new DelTreeCommand("C:\\test");

    private MFCommand mfTestTxt = new MFCommand("test.txt");

    private DELCommand delTest = new DELCommand("test.txt");
    private LockCommand lockFileTestTxt = new LockCommand("test.txt");
    private UnLockCommand unlockFileTest = new UnLockCommand("test.txt");



    public void newClient() throws IOException {

        client = new ClientThread(null, fileSystem) {
            @Override
            public void run() {
                //do nothing
            }
        };
        client.setUser(testUser);
        client.getUser().setCurrentDirectory(fileSystem.getRootDirectory());
    }

    @After
    public void clearFileSystem() {
        fileSystem.clear();
        userOnline.clear();
    }

    //создать дерикторию
    @Test
    public void mdCommandTest() throws IOException {
        newClient();
        mdTest.execute(fileSystem, client);
        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsDirectory(); i++)
            assertTrue("test".equals(fileSystem.getRootDirectory().getDirectory(i).getName()));
    }

    //сменить дерикторию
    @Test
    public void cdCommandTest() throws IOException {
        newClient();

        mdTest.execute(fileSystem, client);
        cdToTest.execute(fileSystem, client);
        assertTrue("test".equals(client.getUser().getCurrentDirectory().getName()));
    }

    //удалить дерикторию
    @Test
    public void rdCommandTest1() throws IOException {
        newClient();

        mdTest.execute(fileSystem, client);
        rdTest.execute(fileSystem, client);

        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsDirectory(); i++)
            assertFalse("test".equals(fileSystem.getRootDirectory().getDirectory(i).getName()));
    }


    //нельзя удалить дерикторию у которой есть поддериктории
    @Test
    public void rdCommandTest2() throws IOException {
        newClient();

        mdTest.execute(fileSystem, client);
        mdTest2InsideTest.execute(fileSystem, client);
        rdTest.execute(fileSystem, client);

        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsDirectory(); i++)
            assertTrue("test".equals(fileSystem.getRootDirectory().getDirectory(i).getName()));
    }

    //нельзя удалить дерикторию в которой находится пользователь
    @Test
    public void rdCommandTest3() throws IOException {
        newClient();

        mdTest.execute(fileSystem, client);
        cdToTest.execute(fileSystem, client);
        rdTest.execute(fileSystem, client);

        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsDirectory(); i++)
            assertTrue("test".equals(fileSystem.getRootDirectory().getDirectory(i).getName()));
    }

    // удалить дерево дерикторий
    @Test
    public void delTreeCommandTest() throws IOException {
        newClient();

        mdTest.execute(fileSystem, client);
        mdTest2InsideTest.execute(fileSystem, client);
        delTreeTest.execute(fileSystem, client);
        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsDirectory(); i++)
            assertFalse("test".equals(fileSystem.getRootDirectory().getDirectory(i).getName()));
    }

    //нельзя удалить директорию в поддерикториях которой находится пользователь
    @Test
    public void delTreeCommandTest2() throws IOException {
        newClient();
        mdTest.execute(fileSystem, client);
        cdToTest.execute(fileSystem, client);
        mdTest.execute(fileSystem, client);
        cdToTest.execute(fileSystem, client);
        delTreeTest.execute(fileSystem, client);

        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsDirectory(); i++)
            assertTrue("test".equals(fileSystem.getRootDirectory().getDirectory(i).getName()));
    }

    //создать файл
    @Test
    public void mfCommandTest() throws IOException {
        newClient();
        mfTestTxt.execute(fileSystem, client);
        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsFile(); i++)
            assertTrue("test.txt".equals(fileSystem.getRootDirectory().getFile(i).getName()));
    }

    //удалить файл
    @Test
    public void delCommandTest() throws IOException {
        newClient();
        mfTestTxt.execute(fileSystem, client);
        delTest.execute(fileSystem, client);
        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsFile(); i++)
            assertFalse("test.txt".equals(fileSystem.getRootDirectory().getFile(i).getName()));
    }

    //блокировать файл
    @Test
    public void lockCommandTest() throws IOException {
        newClient();
        mfTestTxt.execute(fileSystem, client);
        lockFileTestTxt.execute(fileSystem, client);
        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsFile(); i++)
            if ("test.txt".equals(fileSystem.getRootDirectory().getFile(i).getName()))
                assertTrue(!fileSystem.getRootDirectory().getFile(i).isEmptyLocks());
    }

    //разблокировать файл
    @Test
    public void unLockCommandTest() throws IOException {
        newClient();
        mfTestTxt.execute(fileSystem, client);
        lockFileTestTxt.execute(fileSystem, client);
        unlockFileTest.execute(fileSystem, client);

        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsFile(); i++)
            if ("test.txt".equals(fileSystem.getRootDirectory().getFile(i).getName()))
                assertTrue(fileSystem.getRootDirectory().getFile(i).isEmptyLocks());
    }

    //копировать директорию c всем содержимым
    @Test
    public void copyCommandTest() throws IOException {
        newClient();
        mdTest.execute(fileSystem, client);
        mdTest2InsideTest.execute(fileSystem, client);
        mdTest2.execute(fileSystem, client);
        copyTestToTest2.execute(fileSystem, client);
        assertTrue("test2".equals(fileSystem.getDirectoryFromPath("C:\\test2\\test\\test2", client.getUser().getCurrentDirectory()).getName()));
    }
    //переместить директорию c всем содержимым
    @Test
    public void moveCommandTest1() throws IOException {
        newClient();
        mdTest.execute(fileSystem, client);
        mdTest2InsideTest.execute(fileSystem, client);
        mdTest2.execute(fileSystem, client);
        moveTestIntoTest2.execute(fileSystem, client);

        assertFalse(!"test2".equals(fileSystem.getDirectoryFromPath("C:\\test2\\test\\test2", client.getUser().getCurrentDirectory()).getName()));
        for (int i = 0; i < fileSystem.getRootDirectory().getNumberContainsDirectory(); i++)
            assertFalse("test".equals(fileSystem.getRootDirectory().getDirectory(i).getName()));
    }
}

