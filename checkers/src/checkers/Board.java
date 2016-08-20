package checkers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
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

public class Board extends Application {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private Tile[][] board = new Tile[WIDTH][HEIGHT];
    private boolean firstPlayerTurn = true;
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private JPanel contentPane;

    private Parent createContent() {
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
    
    private boolean perimeterCheck(int x, int y) {
    	if (x < 0 || x > 7 || y < 0 || y > 7) {
        	return false;
        }
    	return true;
    }

    private MoveResult tryMove(Piece piece, int newX, int newY) {
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

    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }
    
    private void initFX(JFXPanel fxPanel) {
        Scene scene = new Scene(createContent());
        fxPanel.setScene(scene);
    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result = tryMove(piece, newX, newY);

            int oldX = toBoard(piece.getOldX());
            int oldY = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    board[oldX][oldY].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    checkKing(newX, newY, piece);
                    firstPlayerTurn = !firstPlayerTurn;
                    lock(firstPlayerTurn);
                    break;
                case KILL:
                    piece.move(newX, newY);
                    board[oldX][oldY].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    checkKing(newX, newY, piece);
                    firstPlayerTurn = !firstPlayerTurn;
                    break;
            }
        });

        return piece;
    }
    
    public void checkKing(int newX, int newY, Piece piece) {
    	if(!piece.getType().isKing && (piece.getType().oppoLine == newY)) {
    		if(piece.getType() == PieceType.RED) {
    			piece.setType(PieceType.REDKING);
    			piece.coronation();
    		}
    		else {
    			piece.setType(PieceType.WHITEKING);
    			piece.coronation();
    		}
    	}
    }
    
    public void receiveMove(int oldX, int oldY, int newX, int newY, int killX, int killY, boolean turn) {
    	Piece piece = board[oldX][oldY].getPiece();
    	piece.move(newX, newY);
        board[oldX][oldY].setPiece(null);
        board[newX][newY].setPiece(piece);
        checkKing(newX, newY, piece);
        
        try {
        	Piece otherPiece = board[killX][killY].getPiece();
            board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
            pieceGroup.getChildren().remove(otherPiece);
            checkKing(newX, newY, piece);
        } catch(Exception e) {
        	
        }
    }
    
    public void lock(boolean turn) {
    	if(firstPlayerTurn == turn) {
    		for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                	if(board[x][y].hasPiece()) {
                		if((board[x][y].getPiece().getType() == PieceType.RED || board[x][y].getPiece().getType() == PieceType.REDKING) && !firstPlayerTurn) {
                			board[x][y].getPiece().setMouseTransparent(true);
                		}
//                		else if((board[x][y].getPiece().getType() == PieceType.WHITE || board[x][y].getPiece().getType() == PieceType.WHITEKING) && !firstPlayerTurn) {
//                			board[x][y].getPiece().setMouseTransparent(true);
//                		}
                	}
                }
    		}
    	}
    	else {
    		for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                	if(board[x][y].hasPiece()) {
                		board[x][y].getPiece().setMouseTransparent(true);
                	}
                }
    		}
    	}
    }
    
    public void topBar() {
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
    	EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Board();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
    
    public Board() {
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

	@Override
	public void start(Stage arg0) throws Exception {}
	
}
