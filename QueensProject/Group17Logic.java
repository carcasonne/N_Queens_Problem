import net.sf.javabdd.*;

public class Group17Logic implements IQueensLogic
{
    private int size;		// Size of quadratic game board (i.e. size = #rows = #columns)
    private int[][] board;	// Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)

    @Override
    public void initializeBoard(int size) {
        this.size = size;
        this.board = new int[size][size];
    }

    @Override
    public int[][] getBoard() {
        return board;
    }

    @Override
    public void insertQueen(int column, int row) {
        BDDFactory factory = JFactory.init(20,20); 
        
    }
}
