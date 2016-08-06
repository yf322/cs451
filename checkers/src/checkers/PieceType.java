package checkers;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum PieceType {
    RED(1), WHITE(-1), REDKING(2), WHITEKING(-2);

    final int moveDir;

    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }
}
