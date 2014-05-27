import java.io.Serializable;


public class PrintCommand implements Command,Serializable {

     @Override
     public Response execute(VirtualFileSystem virtualFileSystem, ClientThread clientThread) {
         if(clientThread.getUser()==null)
             return new ErrorResponse(clientThread, ERROR_NOT_CONNECT);
         Directory sort=new Directory("sort");
         sort.sortDirectory(virtualFileSystem.getRootDirectory());
         return new PrintResponse(virtualFileSystem,clientThread);
     }

 }
