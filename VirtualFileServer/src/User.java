import java.io.Serializable;


public class User implements Serializable {
    private String name;
    private Directory currentDirectory;

    public String getName() {
        return name;
    }

    public User(String name) {
        this.name = name;
    }

    public void setCurrentDirectory(Directory curDirectory) {
        this.currentDirectory=curDirectory;
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }
}
