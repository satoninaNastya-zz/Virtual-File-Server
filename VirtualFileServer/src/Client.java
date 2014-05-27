import java.io.*;
import java.net.Socket;

public class Client {
    private static BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
    private static ObjectOutputStream outputServer;
    private static CommandGetter getCommandFromUser;

    public static void main(String[] args) throws IOException {
        String commandString;
        Command newCommand;

        connectClientWithServer();
        try {
            while (true) {

                commandString = keyboardReader.readLine();
                if (commandString != null) {
                    if ("quit".compareTo(commandString) == 0) {
                        QuiteUserCommand quit = new QuiteUserCommand();
                        outputServer.writeObject(quit);
                        outputServer.flush();
                        break;
                    }
                        newCommand = getCommandFromUser.getCommand(commandString);
                        if (newCommand != null) {
                            outputServer.writeObject(newCommand);
                            outputServer.flush();
                        } else System.out.println("Incorrect command");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error command");
        } finally {
            QuiteUserCommand quit = new QuiteUserCommand();
            outputServer.writeObject(quit);
            outputServer.flush();
            System.out.println("closing...");
        }
    }

    private static void connectClientWithServer() {
        Socket socket;
        String connectString;
        BufferedReader inputServer;
        String response;
        try {
            while (true) {
                connectString = keyboardReader.readLine();
                String words[] = connectString.split(" ");
                if (words.length == 3) {
                    if (words[0].compareToIgnoreCase("connect") == 0) {
                        ConnectCommand connectCommand = new ConnectCommand(words[1], words[2]);
                        if (connectCommand.isCorrect()) {
                            socket = new Socket(connectCommand.getAddress(), connectCommand.getPort());   // 127.0.0.1:1255
                            getCommandFromUser = new CommandGetter();

                            outputServer = new ObjectOutputStream(socket.getOutputStream());
                            outputServer.writeObject(connectCommand);
                            outputServer.flush();

                            inputServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            response = inputServer.readLine();
                            System.out.println(response);
                            String wordsResponse[] = response.split(" ");
                            if ("Online".equals(wordsResponse[0])) {
                                ReadMessageFromServerThread readFromServerThread = new ReadMessageFromServerThread(socket);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}