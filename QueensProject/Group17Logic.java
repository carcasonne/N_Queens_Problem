import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

import java.util.*;

public class Group17Logic implements IQueensLogic
{
    private int size;		// Size of quadratic game board (i.e. size = #rows = #columns)
    private int[][] board;	// Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)
    private BDD[] variables; //The BDD [] with the amount of variables
    private final BDDFactory factory = JFactory.init(5_000_000, 500_000); //The BDD Factory to initialize nodes and cache
    private BDD True = factory.one(); // the True BDD - makes life easier to define as a field
    private BDD False = factory.zero(); //the False BDD - makes life easier to define as a field
    BDD root; // The root BDD

    //The function for initializing the board. It takes the board size as a parameter.

    public void initializeBoard(int size) {
        this.size = size;
        this.board = new int[size][size];
        this.variables = new BDD[size * size];
        this.factory.setVarNum(size * size);
        True = factory.one();
        False = factory.zero();
        root = True;
        for (int i = 0; i < size * size; i++) {
            this.variables[i] = factory.ithVar(i);
        }

        // Add the constraints to the root tree
        // Since root is initialized to True,
        // this means the BDD's we apply the AND operator with must also be true for root to be true.
        // So the entire tree is only true if each and every of the constraints are true
        root = root.and(verticalAndHorisontalConstraint());
        root = root.and(queenInEveryRowConstraint());
        root = root.and(diagonalConstraint());

        System.out.println("is the logic unsatisfiable (always false)? : " + root.isZero());
        System.out.println("is the logic always true? : " + root.isOne());

        updatePositions();
    }

    /**
    * @return the current board
    */
    public int[][] getBoard() {
        return board;
    }

    // The function to insert a queen on the board
    public void insertQueen(int column, int row) {
        if (this.board[column][row] != 0) return;
        BDD queen = variables[getIndexOfCoordinates(row, column)];
        root = root.restrict(queen);
        updatePositions();
        this.board[column][row] = 1;
    }

    /**
     * @return positions with values indicating if it is possible to place a queen.
     */
    private int[] getPositions(){
        int[] positions = new int[size * size];
        //allsat() finds all the possible configurations given the currently assigned variables
        List<byte[]> configs = new ArrayList<byte[]>(root.allsat());
        for(byte[] positionsInSpecificConfig : configs){
            for(byte i = 0; i<positionsInSpecificConfig.length; i++){
                if (positionsInSpecificConfig[i] != 0)
                    positions[i] = 1;
            }
        }
        return positions;
    }


    private void updatePositions(){
        int[] positions = getPositions();
        for(int i = 0; i<positions.length; i++){
            if(positions[i] == 0){
                this.board[getColumnOfIndex(i)][getRowOfIndex(i)] = -1;
            } else if (positions[i] == 1 && root.allsat().size() == 1) {
                //Auto-fill the rest of the queens if there is only one option left
                this.board[getColumnOfIndex(i)][getRowOfIndex(i)] = 1;
            }
        }
    }

    // Converts a row, column into an index (since BDD is defined as 1-dimensional array)
    public int getIndexOfCoordinates(int row, int column){
        return row * this.size + column;
    }

    // Gives just the column of an index
    public int getColumnOfIndex(int index){
        return index % this.size;
    }

    // Gives just the row of an index
    public int getRowOfIndex(int index){
        return index / this.size;
    }

    /**
    *
    * @return a BDD which imposes the vertical and horizontal constraints required for the problem
    */
    private BDD verticalAndHorisontalConstraint(){
        // Initialized to false. This means that when we add bdd.or(...),
        // that the evaluation of the function is dependent on the clause given.
        BDD bdd = False;
        // Look through every row or column
        for (int x = 0; x<size; x++){
            for(int i = 0; i < size; i++){
                for(int j = i + 1; j < size; j++){
                    if (i != j) {
                        bdd = bdd.or(variables[getIndexOfCoordinates(i, x)].and(variables[getIndexOfCoordinates(j, x)]));
                        bdd = bdd.or(variables[getIndexOfCoordinates(x, i)].and(variables[getIndexOfCoordinates(x, j)]));
                    }
                }
            }
        }
        return bdd.not();
    }
    /**
    *
    * @return a BDD which imposes the diagonal constraints required for the problem
    */
    private BDD diagonalConstraint(){
        BDD bdd = False;

        for(int diagonal = 0; diagonal < size; diagonal++){

            int spaces = diagonal + 1;

            if (spaces <= 1) continue;

            for(int i = 0; i < spaces; i++){
                for(int j = i + 1; j < spaces; j++){
                    if (i != j){
                        // / diagonals
                        //Upper half
                        bdd = bdd.or(variables[getIndexOfCoordinates(diagonal - i, i)].and(variables[getIndexOfCoordinates(diagonal - j, j)]));
                        //Lower half
                        bdd = bdd.or(variables[getIndexOfCoordinates(size - 1 - i, size - 1 - diagonal + i)].and(variables[getIndexOfCoordinates(size - 1 - j, size - 1 - diagonal + j)]));

                        // \ diagonals
                        //Upper half
                        bdd = bdd.or(variables[getIndexOfCoordinates(diagonal - i, size - 1 - i)].and(variables[getIndexOfCoordinates(diagonal - j, size - 1 - j)]));
                        //Lower half
                        bdd = bdd.or(variables[getIndexOfCoordinates(size - 1 - diagonal + i, i)].and(variables[getIndexOfCoordinates(size - 1 - diagonal + j, j)]));
                    }
                }
            }
        }
        return bdd.not();
    }
    /**
     *
     * @return a BDD which imposes the constraint of there being a queen on every row on the board.
     */
    private BDD queenInEveryRowConstraint(){
        BDD sumbdd = True;
        for(int row = 0; row < size; row++){
            BDD bdd = False;
            for(int col = 0; col < size; col++){
                bdd = bdd.or(variables[getIndexOfCoordinates(row, col)]);
            }
            sumbdd = sumbdd.and(bdd);
        }
        return sumbdd;
    }
}