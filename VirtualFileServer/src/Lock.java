
public class Lock {
    private String lockUserName;

    public Lock(String userName) {
        this.lockUserName = userName;
    }

    public boolean deleteLockPass(String userName) {
        return userName.equals(lockUserName);
    }
    public String getLockUserName() {
        return lockUserName;
    }
}
