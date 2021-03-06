package jshell;

/**
 *  The basic structure of our File system.
 */
public class DirTrie {

    /**
     * Define node root as the root of our virtual file system.
     */
    private final Node root = new Node(null, "");
    /**
     * Define node curNode as the current working directory of virtual
     * file system.
     */
    private Node curNode = root;

    /**
     * Return the current working directory of DirTrie.
     * 
     * @return Node the current working directory of DirTrie.
     */
    public Node getCurrent() {
        return curNode;
    }

    /**
     * Return the root directory of DirTrie.
     * @return Node the root of the virtual file system. 
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Return the absolute path of a given node (file or directory).
     * 
     * @param pathNode the file or directory for which a path string is desired.
     * @return String the path in the virtual file system of pathNode.
     */
    public String getPath(Node pathNode) {
        String path = pathNode.getName() + "/";

        while (pathNode.getParent() != null) {
            path = pathNode.getParent().getName() + "/" + path;
            pathNode = pathNode.getParent();
        }
        return path;
    }

    /**
     * Add a new directory to the file system at the designated path.
     * 
     * @param path A String representing the desired name and file system 
     * location of the directory to be created.
     */
    private void makeDirHelp(String path) {

        Node parent;
        String dirName;

        if (path.contains("/")) {
            int last_slash = path.lastIndexOf("/");
            String parentpath = path.substring(0, last_slash);

            // Ensure last_slash isn't preceding the path.
            if (parentpath.isEmpty()) {
                parentpath = "/";
            }

            parent = this.checkExist(parentpath);
            dirName = path.substring(last_slash + 1);

        } // Path is relative to the current directory.
        else {
            parent = this.curNode;
            dirName = path;
        }

        // Check that proposed parent exists.
        if (parent != null) {
            // Ensure no duplicate file/directory names.
            if (this.checkExist(path) != null) {
                System.out.println("mkdir: cannot create directory \'" + dirName
                        + "\': File exists");
            } else // No directory with dirName exists in parent.
            {
                Node new_dir = new Node(parent, dirName);
                new_dir.attachNode();
            }
        } else // Proposed parent does not exist. 
        {
            System.out.println("mkdir: cannot create directory \"" + path
                    + "\": No such file or directory");
        }
    }

    public void makeDir(String[] pathArray) {
        for (String path : pathArray) {
            this.makeDirHelp(path);
        }
    }

    /**
     * Check whether path represents the location of a file or directory
     * in the file system and if so return the specified file/directory.
     * 
     * @param path A String representing the file system location of a file or 
     * directory.
     * @return The directory or file object at path; null if none is found.
     */
    public Node checkExist(String path) {

        Node temp;
        if (path.startsWith("/")) { // Absolute path.
            temp = this.root;
            path = path.substring(1);
        } else { // Relative path.
            temp = this.curNode;
        }

        String[] result = path.split("/");

        if ("".equals(result[0])) {
            result = new String[0];
        }

        return this.helpCheckExist(temp, result, 0);
    }

    /**
     * Recursively move through the DirTrie file system to find file/directory 
     * from a given path.
     * 
     * @param temp The node from which to search
     * @param result Array containing proposed path to node
     * @param j Track number of nodes that have been checked.  
     * @return Node The node at the path designated in result; null if 
     * non-existent.
     */
    private Node helpCheckExist(Node temp, String[] result, int j) {
        if (j < result.length) {
            if ("..".equals(result[j])) { // Move up to parent directory.
                if (temp == this.root) { // Block checking parent of root.
                    return temp;
                } else {
                    temp = temp.getParent();
                    return this.helpCheckExist(temp, result, ++j);
                }
            } else if (".".equals(result[j])) { // Keep current directory.
                return this.helpCheckExist(temp, result, ++j);
            } else {
                // Recursively search children for designated file/directory.
                for (int i = 0; i < temp.getChildren().size(); i++) {
                    if (((Node) temp.getChildren().get(i)).getName().
                            equals(result[j])) {
                        temp = ((Node) temp.getChildren().get(i));
                        return this.helpCheckExist(temp, result, ++j);
                    }
                }
            }
            return null;
        } else {
            return temp;
        }
    }

    /**
     * Navigate the file system by changing to a new working directory.
     * 
     * @param path StringArray representing the desired working directory.
     */
    public void changeDir(String[] path) {

        // Throw an error message with invalid input (multiple paths).
        if (path.length != 1) {
            System.out.println("cd : Command takes only 1 argument, "
                    + "please try again.");
        } else {
            Node dest = checkExist(path[0]);
            if (dest != null) {
                // Update current node.
                this.curNode = dest;
            } else {
                System.out.println("cd : " + path[0] + ": No such file or "
                        + "directory");
            }
        }
    }

    /** 
     * List any files or directories at given path.
     * 
     * @param pathArray Array with each item representing a node in file system.
     * @return A list of files and/or directories at the designated path.
     */
    public String listFile(String[] pathArray) {

        String filenames = "\r"; // Attempt to fix cursor jumping bug.

        // First case: List contents of current directory.
        if (pathArray.length == 0) {
            for (int i = 0; i < this.getCurrent().getChildren().size(); i++) {
                Node child = (Node) this.getCurrent().getChildren().get(i);
                filenames += child.getName() + "\n";
            }
            return filenames;
        } else {
            // Second case: Given path(s) list the contents of path(s).
            for (String path : pathArray) {
                Node directory = this.checkExist(path);
                if (directory == null) {
                    filenames += ("ls: " + path + ": Path does not exist\n");
                } else {
                    // Give directory name and list contents.
                    filenames += (directory.getName() + ": ");
                    for (int i = 0; i < directory.getChildren().size(); i++) {
                        Node child = ((Node) directory.getChildren().get(i));
                        filenames += (child.getName() + " ");
                    }
                    filenames += "\n";
                }
            }
            return filenames;
        }
    }
    
    public String catFile(String[] filePath){
        Node file = this.checkExist(filePath[0]);
        if (file != null) {
            if (!file.getContent().isEmpty()) {
                return file.getContent();
            }
        } else {
            return "file not exist or the file do not contain any content";
        }
        return null;
    }
    
    public String linkFile (String[] twoPath) {
        
        if (twoPath.length != 2) {
            System.out.println("ln : Command takes only 2 argument, "
                    + "please try again.");
        } else {
            
        }
        return null;
    }
}