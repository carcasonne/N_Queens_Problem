
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