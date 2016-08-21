package checkers;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import static checkers.Board.TILE_SIZE;

/**
 * This is a Piece class that extends StackPane.
 */
public class Piece extends StackPane {

	// Attribute type from PieceType class
    private PieceType type;

    // Attributes for mouse input(new position)
    private double mouseX, mouseY;
    
    // Attributes for current positions
    private double oldX, oldY;
    
    // Attribute for Image
    private javafx.scene.image.Image image;

    // Get/Set method for PieceType type
    public PieceType getType() {
        return type;
    }
    public void setType(PieceType type) {
    	this.type = type;
    }

    // Get/Set method for double oldX, and oldY
    public double getOldX() {
        return oldX;
    }
    public double getOldY() {
        return oldY;
    }
    
    // Piece constructor with type, and its position x and y
    public Piece(PieceType type, int x, int y) {
        this.type = type;

        // Move the piece to x and y position
        move(x, y);

        // Setting the Background.
        Ellipse background = new Ellipse(TILE_SIZE * 0.3, TILE_SIZE * 0.25);
        background.setFill(Color.BLACK);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(TILE_SIZE * 0.03);
        background.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3 * 2) / 2);
        background.setTranslateY((TILE_SIZE - TILE_SIZE * 0.25 * 2) / 2 + TILE_SIZE * 0.07);
        
        // Setting the piece. 
        Ellipse piece = new Ellipse(TILE_SIZE * 0.3, TILE_SIZE * 0.25);
        piece.setFill(type == PieceType.RED
                ? Color.valueOf("#ff0000") : Color.valueOf("#ffffff"));
        piece.setStroke(Color.BLACK);
        piece.setStrokeWidth(TILE_SIZE * 0.03);
        piece.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3 * 2) / 2);
        piece.setTranslateY((TILE_SIZE - TILE_SIZE * 0.25 * 2) / 2);

        // Adding both Background and pieces defined above in pane.
        getChildren().addAll(background, piece);

        // Getting from mouse input from piece moving
        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        // Getting the drag input and relocates the piece
        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }
    
    // Method to decorating the piece to king piece. 
    public void coronation() {
    	if(type == PieceType.REDKING){
    		image = new javafx.scene.image.Image(ClassLoader.getSystemResourceAsStream("crownRed.png"));
    	}
    	else {
    		image = new javafx.scene.image.Image(ClassLoader.getSystemResourceAsStream("crownWhite.png"));
    	}
    	ImageView imageView = new ImageView();
    	imageView.setImage(image);
    	imageView.setFitWidth(TILE_SIZE * 0.35);
    	imageView.setFitHeight(TILE_SIZE * 0.35);
    	imageView.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3 * 2) / 2);
    	imageView.setTranslateY((TILE_SIZE - TILE_SIZE * 0.25 * 2) / 2);
    	getChildren().add(imageView);
    }

    // Method that relocates its piece to new position
    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    // Method that aborts move when the move is invalid
    public void abortMove() {
        relocate(oldX, oldY);
    }
}
