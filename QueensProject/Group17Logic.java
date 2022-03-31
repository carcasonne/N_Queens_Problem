import net.sf.javabdd.*;
public class Group17Logic implements IQueensLogic
{
    private int size;		// Size of quadratic game board (i.e. size = #rows = #columns)
    private int[][] board;	// Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)
    private BDDFactory fact;
    private int nVars;

    @Override
    public void initializeBoard(int size) {
        this.size = size;
        this.board = new int[size][size];

        //it would be nice to dependecy inject this :DDDD
        fact = JFactory.init(2000000,200000);  
		nVars = size*size;

		fact.setVarNum(nVars);

        System.out.println("The node table: "); // A row contains nodeID, variable number, low_nodeID, high_nodeID
												// The nodeID is the first number in the square brackets
		fact.printAll(); // Here, BDDs (nodes) for the requested number of variables (unnegated and negated) are already included  
    }

    @Override
    public int[][] getBoard() {
        return board;
    }

    @Override
    public void insertQueen(int column, int row) {

        if (this.board[column][row] == 0) {
            for(int i = 0; i < this.size; i++){
                this.board[column][i] = -1;
                this.board[i][row] = -1;
                if (column + i < size && row + i < size){
                    this.board[column + i][row + i] = -1;
                }
                if (column - i >= 0 && row - i >= 0){
                    this.board[column - i][row - i] = -1;
                }
                if (column - i >= 0 && row + i < size ){
                    this.board[column - i][row + i] = -1;
                }
                if(column + i < size && row - i >= 0){
                    this.board[column + i][row - i] = -1;
                }
            }
            this.board[column][row] = 1;
        }
    }
}