import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    private static BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
    private static ObjectOutputStream outputServer;
    private static CommandGetter getCommandFromUser;

    public static void main(String[] args) {
        String commandString;
        Command newCommand;
        boolean isConnect = false;
        while (!isConnect) {
            isConnect = connectClientWithServer();
        }
        try {
            while (true) {
                commandString = keyboardReader.readLine();
                if (commandString != null) {
                    if ("quit".compareTo(commandString) == 0) {
                        QuiteUserCommand quit = new QuiteUserCommand();
                        outputServer.writeObject(quit);
                        outputServer.flush();
                        System.exit(0);
                        break;
                    }
                    newCommand = getCommandFromUser.getCommand(commandString);
                    if (newCommand != null) {
                        outputServer.writeObject(newCommand);
                        outputServer.flush();
                    } else System.out.println("Incorrect command");
                }
            }
        } catch (SocketException se) {
            outputServer = null;
            System.err.println("Server is not running");
            System.exit(0);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            try {
                keyboardReader.close();
                if (outputServer != null) {
                    outputServer.close();
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    private static boolean connectClientWithServer() {
        Socket socket;
        String connectString;
        BufferedReader inputServer;
        String response;


            try {
                connectString = keyboardReader.readLine();
            } catch (IOException io) {
                io.printStackTrace();
                return false;
            }
            if (connectString == null) {
                return false;
            }
            String words[] = connectString.split(" ");
            if (words.length != 3) {
                return false;
            }
            if (words[0].compareToIgnoreCase("connect") != 0) {
                return false;
            }
            ConnectCommand connectCommand = new ConnectCommand(words[1], words[2]);

            try {
                socket = new Socket(connectCommand.getAddress(), connectCommand.getPort());   // 127.0.0.1:1255 или localhost:1255
            } catch (UnknownHostException uh) {
                System.err.println("incorrect address");
                return false;
            } catch (ConnectException c) {
                System.err.println("incorrect port or server is not running");
                return false;
            } catch (IOException io) {
                System.out.print("You not connect");
                io.printStackTrace();
                return false;
            }
            getCommandFromUser = new CommandGetter();

            try {
                outputServer = new ObjectOutputStream(socket.getOutputStream());
                outputServer.writeObject(connectCommand);
                outputServer.flush();
                inputServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                response = inputServer.readLine();
                System.out.println(response);
                String wordsResponse[] = response.split(" ");
                if ("Online".equals(wordsResponse[0])) {
                    ReadMessageFromServerThread readFromServerThread = new ReadMessageFromServerThread(socket);
                    return true;
                }
            } catch (IOException io) {
                System.out.print("You not connect");
                io.printStackTrace();
                return false;
            }
            return false;

    }

}