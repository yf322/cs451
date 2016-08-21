package checkers;

/**
 * @author Yongqiang Fan (fldclassicblue@gmail.com)
 */
public enum PieceType {
    RED(1, false, 7), WHITE(-1, false, 0), REDKING(1, true, 7), WHITEKING(1, true, 0);

    final int dir;
    final boolean isKing;
    final int oppoLine;

    PieceType(int dir, boolean isKing, int oppoLine) {
        this.dir = dir;
        this.isKing = isKing;
        this.oppoLine = oppoLine;
    }
}
