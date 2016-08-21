package checkers;

/**
 *	This is a pieceType class for the type of piece.
 */

public enum PieceType {
	// All types of pieces are defined.
    RED(1, false, 7), WHITE(-1, false, 0), REDKING(1, true, 7), WHITEKING(1, true, 0);

    // Attributes for direction(int), king(boolean), and oppositeLine(int). 
    final int dir;
    final boolean isKing;
    final int oppoLine;

    PieceType(int dir, boolean isKing, int oppoLine) {
        this.dir = dir;
        this.isKing = isKing;
        this.oppoLine = oppoLine;
    }
}
