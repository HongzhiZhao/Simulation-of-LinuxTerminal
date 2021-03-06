package jshell;

import java.util.Arrays;
import java.util.Scanner;

public class JShell {

    // Boolean will control the prompt; stop on false.
    private boolean j;
    private DirTrie dirtrie;

    /**
     * Create the JShell.
     */
    JShell() {
        j = true;
        dirtrie = new DirTrie();
    }

    /**
     * Return the DirTrie representing our file system.
     * @return DirTrie The tree that contains our virtual file system.
     */
    public DirTrie getDirTrie() {
        return this.dirtrie;
    }

    /**
     * Return the boolean that controls the prompt.
     * @return boolean True if the prompt is still running and false if "exit" 
     * command used.
     */
    public boolean getJ() {
        return this.j;
    }

    public static void main(String[] args) {

        // On CDF computers cursor sometimes gets stuck behind command which
        // causes the command to show up after our output. We haven't been able
        // to find any fix.

        JShell jshell = new JShell();

        // Continuously take and execute commands typed by user. 
        Scanner scan = new Scanner(System.in);
        String cmd;
        while (jshell.j) {

            String currentPath = jshell.dirtrie.getPath(jshell.dirtrie
                    .getCurrent());

            // Prompt for input and pass command to execute.
            System.out.print(currentPath + "# ");
            cmd = scan.nextLine();
            jshell.execute(cmd);
        }
    }

    /**
     * Parse a string into a command and its arguments and execute command.
     * @param cmd The inputted command from the shell prompt.
     */
    void execute(String cmd) {
        // Split cmd on whitespace.
        String[] args = cmd.split("\\s+");


        // First element of array is the command.
        // eg: if someone keys in "cd a2", subcmd = "cd"
        String subcmd = args[0];

        // Make new array with only command's arguments.
        String[] cmdArgs = new String[args.length - 1];
        cmdArgs = Arrays.copyOfRange(args, 1, args.length);


        // The following are the commands to be executed.

        if ("mkdir".equals(subcmd)) {

            this.dirtrie.makeDir(cmdArgs);

        } else if ("cd".equals(subcmd)) {
            this.dirtrie.changeDir(cmdArgs);


        } else if ("pwd".equals(subcmd)) {

            System.out.println(dirtrie.getPath(dirtrie.getCurrent()));

        } else if ("ls".equals(subcmd)) {

            System.out.println(this.dirtrie.listFile(cmdArgs));

        } else if ("cat".equals(subcmd)){
            
            System.out.println(this.dirtrie.catFile(cmdArgs));
            
        } else if ("ln".equals(subcmd)){
            
            System.out.println(this.dirtrie.linkFile(cmdArgs)); 
            
        } else if ("exit".equals(subcmd)) {
            j = false;

        } else {
            System.out.println("Not a valid command, please try again.");
        }


    }
}
