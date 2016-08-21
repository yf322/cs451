package checkers;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This is a Tile class for the board.
 */

public class Tile extends Rectangle {

	// Piece attribute to determine whether the tile has a piece.
    private Piece piece;

    // Method to determine the tile has a piece. It returns a boolean value.
    public boolean hasPiece() {
        return piece != null;
    }

    // Get/Set method for Piece piece
    public Piece getPiece() {
        return piece;
    }
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    // Public constructor for tile to initialize in the beginning.
    public Tile(boolean light, int x, int y) {
    	
    	// Setting the tile size.
        setWidth(Board.TILE_SIZE);
        setHeight(Board.TILE_SIZE);

        relocate(x * Board.TILE_SIZE, y * Board.TILE_SIZE);

        setFill(light ? Color.valueOf("#feb") : Color.valueOf("#582"));
    }
}
