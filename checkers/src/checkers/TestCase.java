package checkers;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestCase {

	@Test
	public void test() {
		Server server = new Server();
		/*
		 * testing some methods inside Board class
		 */
		Board board = new Board(server, true, 12345);
		assertTrue(!board.perimeterCheck(8, 3));
		assertTrue(board.perimeterCheck(3, 4));
		assertTrue(board.toBoard(500) == 5);
		assertTrue(board.toBoard(300) == 3);
		
	}

}
