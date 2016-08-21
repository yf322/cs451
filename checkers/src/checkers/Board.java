package checkers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    private final boolean side;
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    private int oppoPiece = 12;
    private int urPiece = 12;
    JFrame frame;
    private JPanel contentPane;
    private Server server;
    
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
                    piece = makePiece(PieceType.RED, x, y, server);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.WHITE, x, y, server);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
        lock();
        unlock();
        return root;
    }
    
    private boolean perimeterCheck(int x, int y) {
    	if (x < 0 || x > 7 || y < 0 || y > 7) {
        	return false;
        }
    	return true;
    }
    
    private boolean dectectJumpable(Piece piece, int newX, int newY) {
    	int[][] dir = new int[][]{{2,2}, {-2,2}, {-2,-2}, {2,-2}};
    	for(int i = 0; i < dir.length; i++) {
    		int destX = newX + dir[i][0];
    		int destY = newY + dir[i][1];
    		MoveResult result = tryMove(piece, destX, destY);
    		if(result.getType() == MoveType.KILL) {
    			System.out.println("Multi-Jump detected.");
    			return true;
    		}
    	}
    	System.out.println("Multi-Jump not detected.");
    	return false;
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

    private Piece makePiece(PieceType type, int x, int y, Server server) {
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
                    lock();
                    try {
                    	server.getDos().writeInt(oldX);
                    	server.getDos().writeInt(oldY);
                    	server.getDos().writeInt(newX);
                    	server.getDos().writeInt(newY);
                    	server.getDos().writeInt(100);
                    	server.getDos().writeInt(100);
                    	server.getDos().writeBoolean(!firstPlayerTurn);
                    	server.getDos().flush();
                    	System.out.println("Data sent successfully");
                    } catch (Exception e1) {
                    	System.out.println("Error occured while sending some data");
                    }
                    break;
                case KILL:
                    piece.move(newX, newY);
                    board[oldX][oldY].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    checkKing(newX, newY, piece);
                    checkWin();
                    if(!dectectJumpable(piece, newX, newY)) {
                    	lock();
                    	firstPlayerTurn = !firstPlayerTurn;
                    }
                    try {
                    	server.getDos().writeInt(oldX);
                    	server.getDos().writeInt(oldY);
                    	server.getDos().writeInt(newX);
                    	server.getDos().writeInt(newY);
                    	server.getDos().writeInt(toBoard(otherPiece.getOldX()));
                    	server.getDos().writeInt(toBoard(otherPiece.getOldY()));
                    	server.getDos().writeBoolean(firstPlayerTurn);
                    	server.getDos().flush();
                    	System.out.println("Data sent successfully");
                    } catch (Exception e1) {
                    	System.out.println("Error occured while sending some data");
                    }
                    break;
            }
        });

        return piece;
    }
    
    private void checkKing(int newX, int newY, Piece piece) {
    	if(!piece.getType().isKing && (piece.getType().oppoLine == newY)) {
    		if(piece.getType() == PieceType.RED) {
    			piece.setType(PieceType.REDKING);
    			Platform.runLater(new Runnable(){
    				@Override
    				public void run() {
    					piece.coronation();
    				}
                });
    		}
    		else {
    			piece.setType(PieceType.WHITEKING);
    			Platform.runLater(new Runnable(){
    				@Override
    				public void run() {
    					piece.coronation();
    				}
                });
    		}
    	}
    }
    
    private void checkWin() {
    	oppoPiece--;
    	System.out.println("Opponent's pieces: " + oppoPiece);
    	Object[] array = {"You won!"};
        Object[] options = {"Quit"};
    	if(oppoPiece == 0) {
    		int result = JOptionPane.showOptionDialog(null, array, "", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    		if(result == 0) {
    			System.exit(0);
    		}
    	}
    }
    
    private void checkLose() {
    	urPiece--;
    	System.out.println("Your pieces:" + urPiece);
    	Object[] array = {"You Lost!"};
        Object[] options = {"Quit"};
    	if(oppoPiece == 0) {
    		int result = JOptionPane.showOptionDialog(null, array, "", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    		if(result == 0) {
    			System.exit(0);
    		}
    	}
    }
    
    private void receiveMove(int oldX, int oldY, int newX, int newY, Integer killX, Integer killY) {
    	Piece piece = board[oldX][oldY].getPiece();
    	piece.move(newX, newY);
        board[oldX][oldY].setPiece(null);
        board[newX][newY].setPiece(piece);
        checkKing(newX, newY, piece);
        
        try {
        	Piece otherPiece = board[killX][killY].getPiece();
            board[killX][killY].setPiece(null);
            Platform.runLater(new Runnable(){
				@Override
				public void run() {
					pieceGroup.getChildren().remove(otherPiece);
				}
            });
            
            checkKing(newX, newY, piece);
        } catch(Exception e) {
        	
        }
    }
    
    private void unlock() {
    	if(side && firstPlayerTurn) {
			for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                	if(board[x][y].hasPiece()) {
						if(board[x][y].getPiece().getType() == PieceType.RED || board[x][y].getPiece().getType() == PieceType.REDKING) {
			    			board[x][y].getPiece().setMouseTransparent(false);
			    		}
                	}
                }
			}
		}
		else if (!side && !firstPlayerTurn){
			for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                	if(board[x][y].hasPiece()) {
						if(board[x][y].getPiece().getType() == PieceType.WHITE || board[x][y].getPiece().getType() == PieceType.WHITEKING) {
			    			board[x][y].getPiece().setMouseTransparent(false);
			    		}
                	}
                }
			}
		}
    }
    
    private void lock() {
    	System.out.println("Locked Current player.");
		for (int y = 0; y < HEIGHT; y++) {
		    for (int x = 0; x < WIDTH; x++) {
		    	if(board[x][y].hasPiece()) {
					board[x][y].getPiece().setMouseTransparent(true);
		    	}
		    }
		}
    }
    

    public void topBar(Integer uniqueIdDis) {
		JPanel topBar = new JPanel();
		JButton quit = new JButton("QUIT");
		JLabel uniqueId = new JLabel("Unique ID : " + uniqueIdDis);
		
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

//    public static void main(String[] args) {
//    	EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					new Board();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//    }
    

    public Board(Server server, boolean side, Integer uniqueId) {
    	JFrame frame = new JFrame("Checkers");
        contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.setSize(new Dimension(850, 850));
		topBar(uniqueId);
        final JFXPanel fxPanel = new JFXPanel();
        contentPane.add(fxPanel, BorderLayout.CENTER);
        frame.add(contentPane);
        frame.setSize(820, 890);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.server = server;
        this.side = side;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
       });
    	while(true){
    		try {
    			int oldX = server.getDis().readInt();
    			int oldY = server.getDis().readInt();
    			int newX = server.getDis().readInt();
    			int newY = server.getDis().readInt();
    			Integer KillX = server.getDis().readInt();
    			Integer KillY = server.getDis().readInt();
    			boolean turn = server.getDis().readBoolean();
    			if(KillX == 100 && KillY == 100){
    				KillX = null;
    				KillY = null;
    			}
    			else checkLose();;
    			firstPlayerTurn = turn;
    			unlock();
    			receiveMove(oldX, oldY, newX, newY, KillX, KillY);
    			System.out.println("Data successfully received");
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			System.out.println("error receiving the data");
    		}
    	} 
	}

	@Override
	public void start(Stage arg0) throws Exception {}
	
}
