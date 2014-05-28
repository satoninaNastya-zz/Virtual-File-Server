import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

public class VirtualFileServer {

    private static ArrayList<ClientThread> listClient = new ArrayList<ClientThread>();
    private static VirtualFileSystem virtualFileSystem = new VirtualFileSystem("C:", listClient);

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket;
        serverSocket = null;
        try {
            int port = gerPortFromFile("config.properties");
            if (port == -1) {
                System.err.println("Error config file");
                return;
            }

            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException io) {
                System.err.println("Error, server did not start");
                return;
            }
            System.out.println("Server run");

            while (true) {
                Socket socket = serverSocket.accept();
                ClientThread client = new ClientThread(socket, virtualFileSystem);
                listClient.add(client);
            }

        } finally {
            if (serverSocket != null)
                serverSocket.close();

        }
    }

    private static int gerPortFromFile(String configFile) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
            String port = properties.getProperty("port");
            return Integer.parseInt(port);
        } catch (IOException io) {
            System.out.print("configuration file is not found");
            return -1;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
