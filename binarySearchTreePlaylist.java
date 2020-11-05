import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * @author Anik Barua
 * @since 11-04-2020
 * @version 4.0
 *
 * Description: Lab #5 - This java program uses binary search tree data
 * structure to make a sorted playlist on song title name and prints out an
 * organized song data of full playlist and subset playlists. To store the songs
 * in a binary search tree, I used the song title as the key for sorting
 * comparisons and the song object as the value that contains the artist and the
 * average number of plays for that song, and the average number of plays for
 * the artist across 4 weeks (1 month) in my analysis. This binary search tree
 * doesn't contain any duplicates, and it has a subset method that takes the
 * name of start and end, and prints out songs with average information that
 * fall alphabetically between the start and end song titles. This program has a
 * song class, songNode class, SongPlayList class, and a data extract method to
 * read the CSV files (4 weeks/1 months).
 */
public class binarySearchTreePlaylist { //class started

    public static void main(String[] args) throws FileNotFoundException { //main

        //CSV files of full weeks of a month in a array.
        String[] myFiles = new String[4];
        //Using relative filename paths instead of absolute filenames
        //4 full weeks of 1 month
        myFiles[0] = "/Users/anikbarua/csv/week1.csv";
        myFiles[1] = "/Users/anikbarua/csv/week2.csv";
        myFiles[2] = "/Users/anikbarua/csv/week3.csv";
        myFiles[3] = "/Users/anikbarua/csv/week4.csv";

        //Binary Search Tree that will contain songs from my csv files. 
        SongPlayList tree = new SongPlayList();

        // Using the dataExtract method read the data in my binary search tree.
        for (int i = 0; i < myFiles.length; i++) {
            dataExtract(tree, myFiles[i]);
        }

        //Prints out the full tree playlist in sorted order of song titles
        tree.inOrderTraverse(tree.root);

        // Prints out song titles that fall alphabetically between start and end.
        tree.subSet("All of Me", "Be Alright");
        tree.subSet("Toosie Slide", "Watermelon Sugar");
        tree.subSet("Sunday Best", "The Box");
    }

    /*
    The data extract method takes the Binary Search Tree and relative path of the csv file, 
    and reads and populates the data in the tree.
     */
    public static void dataExtract(SongPlayList tree, String x) throws FileNotFoundException {
        //Row and Column number for multi-dimensinal array
        int row = 200;
        int column = 5;

        //Multi-Dimensinal array that will contain readings(Songs's data) from the csv file.
        String[][] array = new String[row][column];

        //Read in the csv file part
        try {
            Scanner sc = new Scanner(new File(x));
            for (int i = 0; i < row; i++) {
                String[] line = sc.nextLine().split(",(?=([^\"]|\"[^\"]*\")*$)");
                // Splits words by "," from each line

                //Removes extra '' from the strings so it can sort in right way. 
                for (int eachLine = 0; eachLine < line.length; eachLine++) {
                    if (line[eachLine].charAt(0) == '"') {
                        line[eachLine] = line[eachLine].substring(1, line[eachLine].length() - 1);
                    }
                }

                for (int j = 0; j < column; j++) {
                    array[i][j] = line[j];
                }
            }
            sc.close(); //Scanner closed
        } catch (FileNotFoundException e) {
            e.getMessage();
        } // End of try and catch block

        //Using for loop, songs of binary tree is reading the data from the Multi-Dimensinal Array.
        for (int a = 0; a < 200; a++) {
            // Create a Song Object
            Song song = new Song(Integer.parseInt(array[a][0]), array[a][1], array[a][2],
                    Integer.parseInt(array[a][3]), array[a][4]);
            tree.addNode(song.getSong(), song);//Add the song in my binary search tree
        }
    }
}

/* 
The SongPlayList class is the Binary Search Tree class for the songs. It contains root 
node of my tree and methods like addNode(), inOrderTraverse(), find() and subset(). 
 */
class SongPlayList {

    public songNode root; //root node
    PrintStream report1, report2; // PrintStream object where the ouput will be printed.

    public void addNode(String songName, Song object) throws FileNotFoundException {
        this.report1 = new PrintStream("fullPlaylist.txt"); //Instantiates a new txt file
        this.report2 = new PrintStream("subsetPlaylist.txt"); //Instantiates a new txt file

        songNode node = new songNode(songName, object); //Initialize a new node 

        // If the root node is empty then the new node will be the root node. 
        if (this.root == null) {
            this.root = node;

        } else {
            // If the song was never been added to my binary search tree, then 
            // find() method will return a null and continue to add the node. 
            if (find(songName) == null) {

                // The main node assigned to root because we will be starting from the root.
                songNode mainNode = root;

                //Parent node for the new node. 
                songNode parentNode;

                while (mainNode != null) {
                    parentNode = mainNode; //parent will be our root node

                    // If the value is less then parent node then go to the leftchild. 
                    if (songName.compareToIgnoreCase(mainNode.getSongName()) < 0) {
                        mainNode = mainNode.leftChild; // Now the main is assigned to the main's left child
                        if (mainNode == null) {
                            // If the left node doesn't have any child then put the node to the left of it.
                            parentNode.leftChild = node;
                            return; // thats it to add a new a node on the left
                        }
                    } else {
                        // If the value is grater then parent node then go to the rightchild. 
                        mainNode = mainNode.rightChild; // Now the main is assigned to the main's right child
                        if (mainNode == null) {
                            // If the right child doesn't have any child then put the node to the right of it. 
                            parentNode.rightChild = node;
                            return; // thats it to add a new a node on the right
                        }
                    }
                }
            } else {
                //If the song is already in my Binary Search Tree, then take 
                //the number of streams and add it to total streams
                songNode s = find(songName); //Gets the song node
                s.songObject.addstreams(object.getStreams()); //Adds to total stream
                s.songObject.artistAverage(); //Increments the number of artist by 1
            }
        }
    }

    // We are doing inorder traverse of it to to print the song in alphabetically sorted order.
    // Inorder - left -> root -> right
    // Uses recusrison to call over an over. 
    public void inOrderTraverse(songNode node) {
        if (node != null) { // Takes the root node
            inOrderTraverse(node.leftChild); // Traverse the left subtree first
            report1.println(node); // Print the next node (first one will be the left most node) 
            inOrderTraverse(node.rightChild); // Then traverse the right subtree
        }
    }

    // Find method takes the song name, finds and returns the song node. 
    public songNode find(String songName) {
        songNode mainNode = root; //Start from the root node
        while (!mainNode.songName.equalsIgnoreCase(songName)) { //while the name is not found
            // If the name value is less than parent, then make the main as the  
            // left child of root, and continue.
            if (songName.compareToIgnoreCase(mainNode.getSongName()) < 0) {
                mainNode = mainNode.leftChild;
            } else {
                // If the name value is greater than the parent, then make the main to the 
                // right child of root, and continue.
                mainNode = mainNode.rightChild;
            }
            // If not found return null.
            if (mainNode == null) {
                return null;
            }
        }
        return mainNode; //If found return the node. 
    }

    // The subset method selects song titles that fall alphabetically between start and end.
    public void subSet(String start, String end) throws FileNotFoundException {
        report2.println("Subset from " + start + " to " + end + "->");
        inOrder(root, start, end); //Calls the inOrder method
        report2.println("\n");
    }

    // The inOrder method is to find the start and end node, and print the 
    // song titles that fall alphabetically between start and end.
    public void inOrder(songNode node, String start, String end) throws FileNotFoundException {
        // Following the inorder traverse 
        if (node != null) {
            inOrder(node.leftChild, start, end); // Traverse the left subtree first
            if (node.songName.compareToIgnoreCase(end) > 0) {
                return; // If the node is bigger than the end, don't print
            }
            // If the node is between the start and end node then print. 
            if (node.songName.compareToIgnoreCase(start) >= 0 && node.songName.compareToIgnoreCase(end) <= 0) {
                report2.println(node.songObject);
            }
            inOrder(node.rightChild, start, end); // Then traverse the right subtree
        }
    }
}

/* 
This is a node class for song objects. It has the key and value for my nodes, and a left and 
a right child.
 */
class songNode {

    String songName; //Contain the song title 
    Song songObject; // Contain the song object
    //In a binary search tree each node has two child (left and right)
    songNode leftChild;
    songNode rightChild;

    //Constructor thats takes the key and the value
    public songNode(String songName, Song songObject) {
        this.songName = songName;
        this.songObject = songObject;
    }

    // Returns the songName.
    public String getSongName() {
        return this.songName;
    }

    // Calls the toString method of the song object.
    @Override
    public String toString() {
        return songObject.toString();
    }
} //end of songNode class

/*
Song class takes all the information of a song from the csv file and creates an Song object. 
 */
class Song {

    //The csv files contains the position, artist's name, song name, total streams and the url.
    private int artistPosition;
    private String songTitle;
    private String artistName;
    private int streams;
    private String url;
    private int streamsAverage; //It will contain the average stream
    private int artistAverage;  //It will contain the average artist count

    // Constructor takes the postion, song name, artist's name, total streams, and the url
    public Song(int position, String songTitle, String artistName, int streams, String url) {
        this.artistPosition = position;
        this.songTitle = songTitle;
        this.artistName = artistName;
        this.streams = streams;
        this.url = url;
        this.streamsAverage = streams;
        this.artistAverage = 1;
    }

    //Returns the number of streams
    public int getStreams() {
        return this.streams;
    }

    //Adds number of streams per week
    public void addstreams(int x) {
        this.streamsAverage = this.streamsAverage + x;
    }

    //Adds one to the artistAverage if appeared
    public void artistAverage() {
        this.artistAverage = this.artistAverage + 1;
    }

    // Returns the name of the song.
    public String getSong() {
        return this.songTitle;
    }

    // Returns a string with all the data of the song. 
    // Here, I am dividing by 4 to get the average of all 4 weeks.
    @Override
    public String toString() {
        return "Song: " + this.songTitle + ", Artist: " + this.artistName
                + ", Average Streams: " + this.streamsAverage/4
                + ", Average Artist Count: " + this.artistAverage;
    }
} //end of song class
