import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Directory {
    private String name;

    private ArrayList<Directory> containsDirectory = new ArrayList<Directory>();
    private ArrayList<File> containsFile = new ArrayList<File>();

    private Comparator<Directory> sortDirectory = new Comparator<Directory>() {
        public int compare(Directory o1, Directory o2) {
            return o1.name.compareTo(o2.name);
        }
    };
    private Comparator<File> sortFile = new Comparator<File>() {
        public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private ArrayList<File> getContainsFile() {
        return containsFile;
    }

    private ArrayList<Directory> getContainsDirectory() {
        return containsDirectory;
    }

    private ArrayList<User> usersThisNow = new ArrayList<User>();

    public void userEnter(User user) {
        usersThisNow.add(user);
    }

    public void userExit(User user) {
        usersThisNow.remove(user);
    }

    public boolean isNotUsers() {
        return usersThisNow.isEmpty();
    }

    public void clean() {
        containsDirectory.clear();
        containsFile.clear();
        usersThisNow.clear();
    }

    public Directory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void changeDoubleName() {
        String[] nameMas = this.name.split("_");
        if (nameMas.length == 1) {
            this.name = this.name + "_1";
            return;
        }
        int num = Integer.parseInt(nameMas[1]);
        num++;
        this.name = nameMas[0] + "_" + num;
    }

    public String getNameDirectory(int i) {
        return containsDirectory.get(i).name;
    }

    public String getNameFile(int i) {
        return containsFile.get(i).getName();
    }

    public Directory getDirectory(int i) {
        return containsDirectory.get(i);
    }

    public File getFile(int i) {
        return containsFile.get(i);
    }


    public void newDirectory(Directory dir) {
        containsDirectory.add(dir);
    }


    public void newFile(File newFile) {
        containsFile.add(newFile);
    }

    public void removeDirectory(Directory dir) {
        containsDirectory.remove(dir);
    }

    public void removeFile(int i) {
        containsFile.remove(i);
    }

    public boolean isNotContainsDirectory() {
        return containsDirectory.isEmpty();
    }

    public boolean isNotContainsFile() {
        return containsFile.isEmpty();
    }

    public int getNumberContainsDirectory() {
        return containsDirectory.size();
    }

    public int getNumberContainsFile() {
        return containsFile.size();
    }

    public boolean isFileNotLock(int i) {
        return containsFile.get(i).isEmptyLocks();
    }

    public int getNumberFileLocks(int i) {
        return containsFile.get(i).getLocksSize();
    }

    public String getUserNameFileLock(int i, int j) {
        return containsFile.get(i).getUserLockName(j);
    }

    public void sortDirectory(Directory directory) {
        Collections.sort(directory.getContainsDirectory(), sortDirectory);

        Collections.sort(directory.getContainsFile(), sortFile);

        if (!directory.isNotContainsDirectory()) {
            for (int i = 0; i < directory.getNumberContainsDirectory(); i++) {
                sortDirectory(directory.getDirectory(i));
            }
        }
    }
}



