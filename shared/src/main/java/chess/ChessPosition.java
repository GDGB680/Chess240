package chess;

import java.util.Objects;

public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {return row;}
    public int getColumn() {return col;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChessPosition position = (ChessPosition) obj;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {return Objects.hash(row, col);}
}
