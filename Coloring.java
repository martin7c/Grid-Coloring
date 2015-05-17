import java.io.*;
import java.util.ArrayList;
 
public class Coloring {
 
    // This boolean variable is used to create print statements as our program
    // runs, so we can check whether or not the appropriate clauses are created
    // from the number of rows, columns, and colors.
    static boolean debug = true;
 
    // This int variable keeps track of the number of clauses within our
    // program, which gets added to the cnf file after the rest of the clauses.
 
    static ArrayList<String> allClauses = new ArrayList<String>();
 
    /**
     * Main function that starts our program.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(
                System.in));
 
        // rows, columns, and colors are set to 1 arbitrarily.
        int rows = 1;
        int columns = 1;
        int colors = 1;
        // program will terminate (loop will stop) if 0 is entered for rows or
        // columns.
        while (columns != 0 && rows != 0) {
 
            // We ask the user to enter a filename, the number of rows, columns,
            // and colors in the coloring assignment.
            System.out.println("Enter file name: ");
            String fileName = stdin.readLine();
            File saveFile = new File("/Users/Darren/Documents/School/DAA" + fileName
                    + ".cnf");
            PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(saveFile)));
            System.out.println("Enter rows: ");
            columns = Integer.parseInt(stdin.readLine());
            System.out.println("Enter columns: ");
            rows = Integer.parseInt(stdin.readLine());
            System.out.println("Enter number of colors: ");
            colors = Integer.parseInt(stdin.readLine());
            if (columns != 0 && rows != 0) {
                long start = System.currentTimeMillis();
                System.out.println("Rows: " + rows + " Columns: " + columns);
                createSAT(rows, columns, colors, writer);
                finalizeSAT(rows, columns, colors, writer);
                if (writer != null) {
                    writer.close();
                }
                long end = System.currentTimeMillis();
                System.out.println("\nTime: " + (end - start)
                        + " milliseconds\n");
            }
        }
 
    }
 
    /**
     * This method begins the creation of the SAT that corresponds with the
     * coloring grid. It does this by checking to make sure that there is ONLY 1
     * color in each cell.
     * 
     * @param rows
     * @param columns
     * @param coloring
     * @param writer
     */
    private static void createSAT(int rows, int columns, int coloring,
            PrintWriter writer) {
        // int current keeps track of the place we are at in our grid.
        // We begin at 1 because we begin with the first cell, not a cell at 0.
        int current = 1;
 
        // This loop begins at 1 and goes to m, which keeps track of the number
        // of rows.
        // This allows us to loop through each cell in each row.
        for (int i = 1; i <= rows; i++) {
 
            // This loop begins at 1 and ends at n, which keeps track of the
            // number of columns.
            // This allows us to loop through each cell in each column, for each
            // row.
            for (int j = 1; j <= columns; j++) {
 
                // This string variable, row, is a single row of the cnf file.
                // It will be
                // populated with the literals that make up a single clause,
                // which will check
                // that there is one and only one color in each cell.
                String row = "";
 
                // Because each row represents one cell, and each cell could
                // have 1 of 4 colors,
                // we need to loop through 4 colors, starting at the position of
                // each cell.
                // Therefore, we have int k, which beings at the position of
                // each cell.
                int k = current;
 
                // This loop
                while (k < current + coloring) {
                    row += k + " ";
                    k++;
                }
 
                // This adds a 0 to the end of each clause.
                row += "0";
 
                // This code will print out each clause if debug is set to true.
                if (debug) {
                    System.out.println(row);
                }
                allClauses.add(row);
 
                // The variable row is reset to be blank, so that we can add
                // another clause.
                row = "";
 
                // This inner loop helps to check to make sure that there is
                // only one color in
                // each cell.
                // Variable x keeps track of one color.
                for (int x = current; x < current + coloring - 1; x++) {
 
                    // This loop increments variable y, which keeps track of
                    // another color that we
                    // will check against variable x.
                    for (int y = x + 1; y < current + coloring; y++) {
 
                        // We add two variables to row, which represent two
                        // colors that we are checking
                        // against each other to make sure that each cell only
                        // has one color.
                        row += "-" + x + " -" + y + " 0";
 
                        // This code will print out each clause if debug is set
                        // to true.
                        if (debug) {
                            System.out.println(row);
                        }
                        allClauses.add(row);
 
 
                        // The variable row is reset to be blank, so that we can
                        // add another clause.
                        row = "";
                    }
                }
 
                // This increments current by the number of colors, because for
                // each cell, there should
                // be one literal per color.
                current += coloring;
            }
 
        }
 
        // This passes the number of rows, columns, and colors to another
        // method, which checks the corners
        // of each subgrid.
        checkCorners(rows, columns, coloring, writer);
    }
 
    /**
     * This generates the comments for the cnf file and adds them to it.
     * 
     * @param rows
     * @param columns
     * @param colors
     * @param writer
     */
    public static void addComments(int rows, int columns, int colors,
            PrintWriter writer) {
        writer.println("c Grid coloring of size " + rows + " x " + columns
                + ", with " + colors + " colors.");
        writer.println("c Generated by Coloring Project 1.0");
        writer.println("c version 2015 by Joe Savin, Amandeep Singh, Darren Martin");
    }
 
    /**
     * This checks the four corners of all possible subgrids within the coloring
     * problem.
     * 
     * @param rows
     * @param columns
     * @param colors
     * @param writer
     */
    private static void checkCorners(int rows, int columns, int colors,
            PrintWriter writer) {
 
        // The String variable clause that will hold a single clause.
        String clause = "";
 
        // int subY represents the height-1 of the sub grid we are checking.
        for (int subY = 1; subY < rows; subY++) {
 
            // int subX represents the location of the width-1 of the sub grid
            // we are checking
            for (int subX = 1; subX < columns; subX++) {
 
                // now we create the clause for every sub grid of that size.
 
                // int pos is the first position of the top left corner of each
                // sub grid we check.
                int pos = 1;
 
                // int i increments the position of our current cell as we check
                // each subgrid along the y axis.
                for (int i = 1; i + subY <= rows; i++) {
 
                    // int j increments the position of our current cell as we
                    // check each subgrid along hte x axis
                    int j = pos;
                    for (; j + subX < columns + pos; j++) {
 
                        // bbecause the number of colors directly affects how
                        // many clauses we need to check the corners of each sub
                        // grid, we must find the actual corresponding literal
                        // to the first color in the current cell.
                        int position = (j * colors) - (colors - 1);
 
                        // for the current cell and subgrid, we will now check
                        // all colors in all four corners.
                        for (int x = 0; x < colors; x++) {
 
                            // this clause examines corresponding colors in 4
                            // corners of the subgrid.
                            clause += " -"
                                    + position
                                    + " -"
                                    + (position + (colors * subX))
                                    + " -"
                                    + (position + (subY * columns * colors))
                                    + " -"
                                    + (position + (subY * columns * colors) + (subX * colors))
                                    + " 0";
 
                            // if our debug is set to true, the clauses are
                            // printed as they are created.
                            if (debug) {
                                System.out.println(clause);
                            }
 
                            // this writes the clause to our output cnf file.
                            allClauses.add(clause);
 
                            // now we can increment position to the next cell.
                            position++;
 
 
                            // clause is reset to be blank
                            clause = "";
                        }
                    }
 
                    // the variable pos gets incremented by the number of
                    // columns so we can skip to the next row at the end of each
                    // row.
                    pos += columns;
                }
            }
        }
 
        // if debug is set to true, the program prints the number of clauses.
        if (debug) {
            System.out.println("Clauses: " + allClauses.size());
        }
    }
 
    /**
     * Completes the cnf file by adding comments, clauses, and literals.
     * 
     * @throws IOException
     */
    static void finalizeSAT(int rows, int columns, int coloring,
            PrintWriter writer) throws IOException {
 
        // We pass the number of rows, columns, and colors to a method that
        // prints the comments into the cnf file.
        addComments(rows, columns, coloring, writer);
 
        writer.println("p cnf " + (rows * columns * coloring) + " " + allClauses.size());
 
        for (int i = 0; i < allClauses.size(); i++) {
            writer.println(allClauses.get(i));
        }
 
    }
    
}
