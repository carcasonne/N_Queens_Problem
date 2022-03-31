import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

import java.util.*;

public class Group17Logic implements IQueensLogic
{
    private int size;		// Size of quadratic game board (i.e. size = #rows = #columns)
    private int[][] board;	// Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)
    private BDD[] variables;
    private final BDDFactory factory = JFactory.init(2000000, 200000);
    private BDD True = factory.one(); //makes life easier to define as a field
    private BDD False = factory.zero();
    BDD root;

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
        // Since root is initialzied to True,
        // this means the BDD's we apply the AND operator with must also be true for root to be true.
        // So the entire tree is only true if each and every of the constraints are true
        root = root.and(verticalConstraint());
        //root = root.and(horisontalConstraint());
        //root = root.and(queenInEveryColumnConstraint());
        //root = root.and(diagonalConstraint());

        System.out.println("is the logic unsatisfiable (always false)? : " + root.isZero());
        System.out.println("is the logic always true? : " + root.isOne());

        updatePositions();
    }

    public int[][] getBoard() {
        return board;
    }



//TODO: Ændre
    private int[] getAllowedPositions(){
        int[] allowedPositions = new int[size * size];
        //allsat() "Finds all satisfying variable assignments."
        //allsat() has no implementation in the jar file, it is literally just a generic list with no type??????
        List<byte[]> configs = new ArrayList<byte[]>(root.allsat());
        for(byte[] by : configs){
            for(byte i = 0; i<by.length; i++){
                if(by[i] != 0){
                    allowedPositions[i] = 1;
                }
            }
        }
        return allowedPositions;
    }

   private void updatePositions(){
       int[] allowedPositions = getAllowedPositions();
       for(int i = 0; i<allowedPositions.length; i++){
           if(allowedPositions[i] == 0){
               this.board[getColumnOfIndex(i)][getRowOfIndex(i)] = -1;
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

    private BDD verticalConstraint(){
        // Initialized to false. This means that when we add bdd.or(...),
        // that the evaluation of the function is dependent on the clause given.
        BDD bdd = False;
        // Look through every column
        for (int col = 0; col<size; col++){
            for(int i = 0; i < size; i++){
                for( int j = i + 1; j < size; j++){
                    if (i != j) bdd = bdd.or(variables[getIndexOfCoordinates(i, col)].and(variables[getIndexOfCoordinates(j, col)]));
                }
            }
        }
        return bdd.not();
    }

    private BDD horisontalConstraint(){
        BDD bdd = False;
        for(int row = 0; row < size; row++){
            for(int i = 0; i < size; i++){
                for(int j = i + 1; j < size; j++){
                    if (i != j) bdd = bdd.or(variables[getIndexOfCoordinates(row, i)].and(variables[getIndexOfCoordinates(row, j)]));
                }
            }
        }
        return bdd.not();
    }

    private BDD diagonalConstraint(){
        BDD bdd = False;

        for(int diagonal = 0; diagonal < size; diagonal++){

            int spaces = diagonal + 1;

            if (spaces <= 1) continue;

            for(int i = 0; i < spaces; i++){
                for(int j = i + 1; j < spaces; j++){
                    if (i != j){
                        // / diagonals
                        bdd = bdd.or(variables[getIndexOfCoordinates(diagonal - i, i)].and(variables[getIndexOfCoordinates(diagonal - j, j)]));
                        bdd = bdd.or(variables[getIndexOfCoordinates(size - 1 - i, size - 1 - diagonal + i)].and(variables[getIndexOfCoordinates(size - 1 - j, size - 1 - diagonal + j)]));
                        // \ diagonals
                        bdd = bdd.or(variables[getIndexOfCoordinates(diagonal - i, size - 1 - j)].and(variables[getIndexOfCoordinates(diagonal - j, size - 1 - j)]));
                        bdd = bdd.or(variables[getIndexOfCoordinates(diagonal - i, size - 1 - j)].and(variables[getIndexOfCoordinates(diagonal - j, size - 1 - j)]));
                    }
                }
            }
        }

        return bdd.not();
    }


    //TODO: Ændre
    private BDD queenInEveryColumnConstraint(){
        BDD bdd = False;
        BDD bdd2 = True;
        BDD bdd3 = True;
        for(int i = 0; i < size; i++){
            for(int j = i*size; j < size+i*size; j++){
                bdd = bdd.or(variables[i]);
            }
            bdd2 = True;
            bdd2 = bdd2.and(bdd);
            bdd = False;
            bdd3 = bdd3.and(bdd2);
        }
        root = root.and(bdd3);
        return bdd;
    }

    public void insertQueen(int column, int row) {
        if (this.board[column][row] != 0) return;
        this.board[column][row] = 1;
        updatePositions();
    }
}