public class CommandGetter {


    public Command getCommand(String command) {
        try {
            String words[] = command.split(" ");
            if (words.length == 1)
                if (words[0].compareToIgnoreCase("print") == 0)
                    return new PrintCommand();

            if (words.length == 2) {
                if (words[0].compareToIgnoreCase("md") == 0)
                    return new MDCommand(words[1]);

                if (words[0].compareToIgnoreCase("cd") == 0)
                    return new CDCommand(words[1]);

                if (words[0].compareToIgnoreCase("rd") == 0)
                    return new RDCommand(words[1]);

                if (words[0].compareToIgnoreCase("deltree") == 0)
                    return new DelTreeCommand(words[1]);

                if (words[0].compareToIgnoreCase("mf") == 0)
                    return new MFCommand(words[1]);

                if (words[0].compareToIgnoreCase("del") == 0)
                    return new DELCommand(words[1]);

                if (words[0].compareToIgnoreCase("lock") == 0)
                    return new LockCommand(words[1]);

                if (words[0].compareToIgnoreCase("unlock") == 0)
                    return new UnLockCommand(words[1]);
            }

            if (words.length == 3) {

                if (words[0].compareToIgnoreCase("copy") == 0) {
                    return new CopyCommand(words[1], words[2]);
                }
                if (words[0].compareToIgnoreCase("move") == 0) {
                    return new MoveCommand(words[1], words[2]);
                }
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error");
            return null;
        }
    }
}




