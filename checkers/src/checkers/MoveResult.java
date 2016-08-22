package checkers;

/**
 * This is MoveResult class
 */

public class MoveResult {

	// Attribute for MoveType type to determine the move result
	private MoveType type;

	// Attribute for Piece piece to determine the move result
	private Piece piece;

	// Get method for MoveType type
	public MoveType getType() {
		return type;
	}

	// Get method for Piece piece
	public Piece getPiece() {
		return piece;
	}

	// Constructor with PieceType type
	public MoveResult(MoveType type) {
		this(type, null);
	}

	// Constructor with PieceType type and Piece piece
	public MoveResult(MoveType type, Piece piece) {
		this.type = type;
		this.piece = piece;
	}
}
