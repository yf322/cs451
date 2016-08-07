package checkers;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum PieceType {
    RED(1, false), WHITE(-1, false), REDKING(1, true), WHITEKING(1, true);

    final int dir;
    final boolean isKing;

    PieceType(int dir, boolean isKing) {
        this.dir = dir;
        this.isKing = isKing;
    }
}
