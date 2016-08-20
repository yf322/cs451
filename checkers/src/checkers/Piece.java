package checkers;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import static checkers.Board.TILE_SIZE;

/**
 * @author Yongqiang Fan (fldclassicblue@gmail.com)
 */
public class Piece extends StackPane {

    private PieceType type;

    private double mouseX, mouseY;
    private double oldX, oldY;
    private javafx.scene.image.Image image;

    public PieceType getType() {
        return type;
    }
    
    public void setType(PieceType type) {
    	this.type = type;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }
    
    public Piece(PieceType type, int x, int y) {
        this.type = type;

        move(x, y);

        Ellipse bg = new Ellipse(TILE_SIZE * 0.3, TILE_SIZE * 0.25);
        bg.setFill(Color.BLACK);

        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(TILE_SIZE * 0.03);

        bg.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3 * 2) / 2);
        bg.setTranslateY((TILE_SIZE - TILE_SIZE * 0.25 * 2) / 2 + TILE_SIZE * 0.07);

        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3, TILE_SIZE * 0.25);
        ellipse.setFill(type == PieceType.RED
                ? Color.valueOf("#ff0000") : Color.valueOf("#ffffff"));
        

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);

        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.25 * 2) / 2);

        getChildren().addAll(bg, ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }
    
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

    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    public void abortMove() {
        relocate(oldX, oldY);
    }
}
