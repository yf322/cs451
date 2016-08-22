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
/**
 * 
 * @author Yongqiang (fldclassicblue@gmail.com)
 *
 * The Board class is where the game is actually playing.
 */
public class Board extends Application {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private Tile[][] board = new Tile[WIDTH][HEIGHT];
    private boolean firstPlayerTurn = true;
    private final boolean side;
    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    JFrame frame;
    private JPanel contentPane;
    private Server server;
    
    /**
     * This method creates the content with the javafx and initiate the game.
     * @return Parent of the content
     */
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
    
    /**
     * check if the players' move is out of boundary
     * @param x the x coordinate
     * @param y the y coordinate
     * @return
     */
    public boolean perimeterCheck(int x, int y) {
    	if (x < 0 || x > 7 || y < 0 || y > 7) {
        	return false;
        }
    	return true;
    }
    
    /**
     * to see if there is a multiple jump available
     * @param piece the piece made the previoud jump	
     * @param newX the target x coordinate
     * @param newY the target y coordinate
     * @return if there is a jump
     */
    public boolean dectectJumpable(Piece piece, int newX, int newY) {
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

    /**
     * see if you can make a move
     * @param piece the piece make the move
     * @param newX the target x coordinate
     * @param newY the target y coordinate
     * @return the move result
     */
    public MoveResult tryMove(Piece piece, int newX, int newY) {
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
	
	            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType().oppoLine != piece.getType().oppoLine) {
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
	
	            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType().oppoLine != piece.getType().oppoLine) {
	                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
	            }
	        }
        }

        return new MoveResult(MoveType.NONE);
    }

    /**
     * transform pixel to coordinate
     * @param pixel the pixel value
     * @return the coordinate 
     */
    public int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }
    
    /**
     * initiate the fxpanel
     * @param fxPanel
     */
    public void initFX(JFXPanel fxPanel) {
        Scene scene = new Scene(createContent());
        fxPanel.setScene(scene);
    }

    /**
     * put a piece on the board with listener
     * @param type the type of the piece
     * @param x x coordinate 
     * @param y y coordinate
     * @param server the server to transmit the data
     * @return the piece it created
     */
    public Piece makePiece(PieceType type, int x, int y, Server server) {
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
                    checkWin();
                    break;
            }
        });

        return piece;
    }
    
    /**
     * check if there will be a new king
     * @param newX new x coordinate
     * @param newY new y coordinate
     * @param piece the piece is checking
     */
    public void checkKing(int newX, int newY, Piece piece) {
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
    
    /**
     * see if you will win after the move
     */
    public void checkWin() {
    	int i = 0;
    	int j = 0;
    	for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
            	if(board[x][y].hasPiece()) {
            		if(board[x][y].getPiece().getType() == PieceType.RED || board[x][y].getPiece().getType() == PieceType.REDKING) {
            			i++;
            		}
            		else if(board[x][y].getPiece().getType() == PieceType.WHITE || board[x][y].getPiece().getType() == PieceType.WHITEKING){
            			j++;
            		}
            	}
            }
    	}
    	if(side && j == 0) {
    		Object[] array = {"You won!"};
            Object[] options = {"Quit"};
        	if(true) {
        		int result = JOptionPane.showOptionDialog(null, array, "", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        		if(result == 0) {
        			System.exit(0);
        		}
        	}
    	}
    	else if(!side && i == 0) {
    		Object[] array = {"You won!"};
            Object[] options = {"Quit"};
        	if(true) {
        		int result = JOptionPane.showOptionDialog(null, array, "", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        		if(result == 0) {
        			System.exit(0);
        		}
        	}
    	}
    	
    }
    
    /**
     * see if you will lose after a piece being taken out
     */
    public void checkLose() {
    	int i = 0;
    	int j = 0;
    	for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
            	if(board[x][y].hasPiece()) {
            		if(board[x][y].getPiece().getType() == PieceType.RED || board[x][y].getPiece().getType() == PieceType.REDKING) {
            			i++;
            		}
            		else if(board[x][y].getPiece().getType() == PieceType.WHITE || board[x][y].getPiece().getType() == PieceType.WHITEKING){
            			j++;
            		}
            	}
            }
    	}
    	if(!side && j == 0) {
    		Object[] array = {"You Lost!"};
            Object[] options = {"Quit"};
        	if(true) {
        		int result = JOptionPane.showOptionDialog(null, array, "", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        		if(result == 0) {
        			System.exit(0);
        		}
        	}
    	}
    	else if(side && i == 0) {
    		Object[] array = {"You Lost!"};
            Object[] options = {"Quit"};
        	if(true) {
        		int result = JOptionPane.showOptionDialog(null, array, "", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        		if(result == 0) {
        			System.exit(0);
        		}
        	}
    	}
    	
    }
    
    /**
     * receive the data from the server, no need to introduce the parameters
     * @param oldX 
     * @param oldY
     * @param newX
     * @param newY
     * @param killX
     * @param killY
     */
    public void receiveMove(int oldX, int oldY, int newX, int newY, Integer killX, Integer killY) {
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
    
    /**
     * unlock the piece when is your turn
     */
    public void unlock() {
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
    
    /**
     * lock everything!!!
     */
    public void lock() {
    	System.out.println("Locked Current player.");
		for (int y = 0; y < HEIGHT; y++) {
		    for (int x = 0; x < WIDTH; x++) {
		    	if(board[x][y].hasPiece()) {
					board[x][y].getPiece().setMouseTransparent(true);
		    	}
		    }
		}
    }
    

    /**
     * top bar menu
     * @param uniqueIdDis the unique Id of the game
     */
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
    

    /**
     * constructor for this class
     * @param server the server class to transmit the data
     * @param side what is your side
     * @param uniqueId the unique Id of the game
     */
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
    			firstPlayerTurn = turn;
    			unlock();
    			receiveMove(oldX, oldY, newX, newY, KillX, KillY);
    			checkLose();
    			System.out.println("Data successfully received");
    		} catch (IOException e) {
    			System.out.println("error receiving the data");
    			JOptionPane.showMessageDialog(frame, "The opponent got disconnected. You won!");
    			System.exit(0);
    		}
    	} 
	}

	@Override
	public void start(Stage arg0) throws Exception {}
	
}
