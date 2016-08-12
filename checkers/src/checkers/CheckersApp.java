package checkers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CheckersApp extends Application {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private static Tile[][] board = new Tile[WIDTH][HEIGHT];
    private static boolean firstPlayerTurn = true;
    private static Group tileGroup = new Group();
    private static Group pieceGroup = new Group();
    private static JPanel contentPane;

    private static Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.RED, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        return root;
    }
    
    private static boolean perimeterCheck(int x, int y) {
    	if (x < 0 || x > 7 || y < 0 || y > 7) {
        	return false;
        }
    	return true;
    }

    private static MoveResult tryMove(Piece piece, int newX, int newY) {
    	if (!perimeterCheck(newX, newY)) {
    		return new MoveResult(MoveType.NONE);
    	}
        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        
        if (!piece.getType().isKing) {
	        if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().dir) {
	        	firstPlayerTurn = !firstPlayerTurn;
	            return new MoveResult(MoveType.NORMAL);
	        } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().dir * 2) {
	
	            int x1 = x0 + (newX - x0) / 2;
	            int y1 = y0 + (newY - y0) / 2;
	
	            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
	                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
	            }
	        }
        }
        else {
        	if (Math.abs(newX - x0) == 1 && Math.abs(newY - y0) == piece.getType().dir) {
        		firstPlayerTurn = !firstPlayerTurn;
	            return new MoveResult(MoveType.NORMAL);
	        } else if (Math.abs(newX - x0) == 2 && Math.abs(newY - y0) == piece.getType().dir * 2) {
	
	            int x1 = x0 + (newX - x0) / 2;
	            int y1 = y0 + (newY - y0) / 2;
	
	            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
	                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
	            }
	        }
        }

        return new MoveResult(MoveType.NONE);
    }

    private static int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    private static Scene createScene() {
    	Scene scene = new Scene(createContent());
    	return scene;
    }
    private static void initAndShowGUI() {
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("Checkers");
        contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setSize(new Dimension(850, 850));
		topBar();
        final JFXPanel fxPanel = new JFXPanel();
        contentPane.add(fxPanel, BorderLayout.CENTER);
        frame.add(contentPane);
        frame.setSize(820, 890);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
       });
    }
    
    private static void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
        //lock();
    }
    /*
    private static void lock() {
    	for(int i = 0; i < WIDTH; i++) {
    		for(int j = 0; j < HEIGHT; j++) {
    			if(board[i][j].getPiece() != null) {
    				if(board[i][j].getPiece()) {
    					board[i][j].setMouseTransparent(true);
    				}
    			}
    		}
    	}
    	
    }
*/
    private static Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result = tryMove(piece, newX, newY);

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    firstPlayerTurn = !firstPlayerTurn;
                    break;
                case KILL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    firstPlayerTurn = !firstPlayerTurn;
                    break;
            }
        });

        return piece;
    }
    
    public static void topBar() {
		JPanel topBar = new JPanel();
		JButton quit = new JButton("QUIT");
		JLabel uniqueId = new JLabel("Unique ID : ");
		
		quit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
		});
		
		topBar.add(quit, BorderLayout.WEST);
		topBar.add(uniqueId, BorderLayout.EAST);
		contentPane.add(topBar, BorderLayout.NORTH);
	}

    public static void main(String[] args) {
        initAndShowGUI();
    }

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
