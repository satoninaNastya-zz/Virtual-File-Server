import java.util.ArrayList;

public class File {
    private String name;
    private ArrayList<Lock> locks = new ArrayList<Lock>();

    public File(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void cleanLock() {
        locks.clear();
    }

    public boolean lockFile(String userName) {
        for (Lock lock : locks) {
            if (lock.getLockUserName().equals(userName)) {
                return false;
            }
        }

        Lock newLock = new Lock(userName);
        locks.add(newLock);
        return true;
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

    public boolean unlockFile(String userName) {
        for (int i = 0; i < locks.size(); i++) {
            if (locks.get(i).deleteLockPass(userName)) {
                locks.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean isEmptyLocks() {
        return locks.isEmpty();
    }

    public int getLocksSize() {
        return locks.size();
    }

    public String getUserLockName(int i) {
        return locks.get(i).getLockUserName();

    }
}
