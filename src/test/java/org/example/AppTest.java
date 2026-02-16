package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Scanner;

import  static org.junit.jupiter.api.Assertions.*;
class AppTest{

    @BeforeEach
    void setup() {
        App.initBoard();
    }
    @ParameterizedTest
    @ValueSource(strings = {"user","easy","medium","hard"})
    void shouldReturnTrueForValidPlayers(String player){
        assertTrue(App.isPlayer(player));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "USER", "impossible", "123"})
    void shouldReturnFalseForInvalidPlayers(String player) {
        assertFalse(App.isPlayer(player));
    }
    @Test
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(NullPointerException.class, () -> {
            App.isPlayer(null);
        });
    }

    @Test
    void shouldReturnTrueForValidCommand() {
        String[] cmd = {"start", "user", "easy"};
        assertTrue(App.validParameters(cmd));
    }

    @Test
    void shouldReturnFalseWhenLengthIsWrong() {
        String[] cmd = {"start", "user"};
        assertFalse(App.validParameters(cmd));
    }
    @Test
    void shouldReturnFalseWhenFirstWordIsNotStart() {
        String[] cmd = {"begin", "user", "easy"};
        assertFalse(App.validParameters(cmd));
    }
    @Test
    void shouldReturnFalseWhenFirstPlayerIsInvalid() {
        String[] cmd = {"start", "invalid", "easy"};
        assertFalse(App.validParameters(cmd));
    }
    @Test
    void shouldReturnFalseWhenSecondPlayerIsInvalid() {
        String[] cmd = {"start", "user", "invalid"};
        assertFalse(App.validParameters(cmd));
    }
    @Test
    void shouldThrowExceptionWhenCommandIsNull() {
        assertThrows(NullPointerException.class, () -> {
            App.validParameters(null);
        });
    }
    @Test
    void shouldDetectHorizontalWin() {
        App.board[0][0] = 'X';
        App.board[0][1] = 'X';
        App.board[0][2] = 'X';

        assertTrue(App.checkWin('X'));
    }
    @Test
    void shouldDetectVerticalWin() {
        App.board[0][0] = 'X';
        App.board[1][0] = 'X';
        App.board[2][0] = 'X';

        assertTrue(App.checkWin('X'));
    }
    @Test
    void shouldDetectDiagonalWin() {
        App.board[0][0] = 'X';
        App.board[1][1] = 'X';
        App.board[2][2] = 'X';

        assertTrue(App.checkWin('X'));
    }
    @Test
    void shouldReturnFalseWhenNoWin() {
        App.board[0][0] = 'X';
        App.board[0][1] = 'O';
        App.board[0][2] = 'X';

        assertFalse(App.checkWin('X'));
    }

    @Test
    void shouldReturnTrueWhenBoardIsFull() {
        char[][] fullBoard = {
                {'X','O','X'},
                {'O','X','O'},
                {'O','X','O'}
        };

        App.board = fullBoard;

        assertTrue(App.isDraw());
    }

    @Test
    void shouldReturnFalseWhenBoardIsNotFull() {
        char[][] fullBoard = {
                {'X',' ','X'},
                {'O','X','O'},
                {'O','X','O'}
        };

        App.board = fullBoard;

        assertFalse(App.isDraw());
    }
    @Test
    void shouldFindWinningMoveForX() {
        App.initBoard();

        App.board[0][0] = 'X';
        App.board[0][1] = 'X';
        App.board[0][2] = App.EMPTY;

        App.board[1][0] = 'O';

        int[] winningMove = App.findWinningMove('X');

        assertNotNull(winningMove);
        assertEquals(0, winningMove[0]);
        assertEquals(2, winningMove[1]);
    }
    @Test
    void shouldReturnNullWhenNoWinningMove() {
        App.initBoard();
        App.board[0][0] = 'X';
        App.board[1][1] = 'O';

        int[] winningMove = App.findWinningMove('X');

        assertNull(winningMove);
    }
    @Test
    void mediumMoveShouldPlayWinningMove() {
        App.initBoard();
        App.board[0][0] = 'X';
        App.board[0][1] = 'X';
        App.board[0][2] = App.EMPTY;
        App.board[1][1] = 'O';

        App.mediumMove('X');

        assertEquals('X', App.board[0][2]); // X should complete the win
    }
    @Test
    void mediumMoveShouldBlockOpponentWinningMove() {
        App.initBoard();

        App.board[0][0] = 'O';
        App.board[1][0] = 'O';
        App.board[2][0] = App.EMPTY;

        App.board[1][1] = 'X';

        App.mediumMove('X');

        assertEquals('X', App.board[2][0]); // X blocks O
    }
    @Test
    void mediumMoveShouldMakeRandomMoveWhenNoWinOrBlock() {
        App.initBoard();

        // No win/block exists
        App.board[0][0] = 'X';
        App.board[1][1] = 'O';

        App.mediumMove('X');

        boolean moveMade = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (App.board[i][j] == 'X' && !(i == 0 && j == 0)) {
                    moveMade = true;
                }
            }
        }

        assertTrue(moveMade, "X should have placed a move somewhere");
    }
    @Test
    void hardMoveShouldPlayWinningMove() {
        App.initBoard();

        App.board[0][0] = 'X';
        App.board[0][1] = 'X';
        App.board[0][2] = App.EMPTY;

        App.hardMove('X');

        assertEquals('X', App.board[0][2]); // X takes winning move
    }
    @Test
    void hardMoveShouldBlockOpponentWinningMove() {
        App.initBoard();

        App.board[0][0] = 'O';
        App.board[0][1] = 'O';
        App.board[0][2] = App.EMPTY;

        App.board[1][1] = 'X';

        App.hardMove('X');

        assertEquals('X', App.board[0][2]); // X blocks O
    }

    @Test
    void minimaxShouldReturn10IfAICanWin() {
        App.initBoard();
        App.board[0][0] = 'X';
        App.board[0][1] = 'X';
        App.board[0][2] = App.EMPTY;

        int score = App.minimax(true, 'X');
        assertEquals(10, score);
    }
    @Test
    void minimaxShouldReturnMinus10IfOpponentCanWin() {
        App.initBoard();

        App.board[0][0] = 'O';
        App.board[0][1] = 'O';
        App.board[0][2] = App.EMPTY;

        int score = App.minimax(false, 'X'); // AI is X, opponent O
        assertEquals(-10, score);
    }
    @Test
    void minimaxShouldReturnZeroIfDraw() {
        App.initBoard();

        App.board[0] = new char[]{'X', 'O', 'X'};
        App.board[1] = new char[]{'X', 'O', 'O'};
        App.board[2] = new char[]{'O', 'X', 'X'};

        int score = App.minimax(true, 'X');
        assertEquals(0, score);
    }
    @Test
    void makeMoveEasyShouldPlaceSymbol() {
        App.initBoard();

        App.makeMove("easy", 'X');

        boolean moveMade = false;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (App.board[i][j] == 'X') moveMade = true;

        assertTrue(moveMade, "Easy move should place a symbol");
    }
    @Test
    void makeMoveMediumShouldWinIfPossible() {
        App.initBoard();
        App.board[0][0] = 'X';
        App.board[0][1] = 'X';
        App.board[0][2] = App.EMPTY;

        App.makeMove("medium", 'X');

        assertEquals('X', App.board[0][2]);
    }
    @Test
    void makeMoveHardShouldWinIfPossible() {
        App.initBoard();
        App.board[0][0] = 'X';
        App.board[0][1] = 'X';
        App.board[0][2] = App.EMPTY;

        App.makeMove("hard", 'X');

        assertEquals('X', App.board[0][2]);
    }
    @Test
    void makeMoveShouldDoNothingForInvalidPlayer() {
        App.initBoard();
        App.makeMove("invalid", 'X');

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                assertEquals(App.EMPTY, App.board[i][j]);
    }
    @Test
    void userMoveShouldPlaceSymbolAtGivenCoordinates() {
        App.initBoard();
        App.scanner = new Scanner("1 1\n");

        App.userMove('X');

        assertEquals('X', App.board[0][0]);
    }
    @Test
    void userMoveShouldRejectOccupiedCellAndRetry() {
        App.initBoard();
        App.board[0][0] = 'O'; // already occupied

        App.scanner = new Scanner("1 1\n1 2\n");

        App.userMove('X');

        assertEquals('X', App.board[0][1]); // symbol placed in second attempt
        assertEquals('O', App.board[0][0]); // first cell unchanged
    }
    @Test
    void userMoveShouldRejectNonNumberInput() {
        App.initBoard();


        App.scanner = new Scanner("a b\n1 1\n");

        App.userMove('X');

        assertEquals('X', App.board[0][0]); // symbol placed after valid input
    }
    @Test
    void userMoveShouldRejectOutOfRangeCoordinates() {
        App.initBoard();
        App.scanner = new Scanner("4 5\n2 2\n");

        App.userMove('X');

        assertEquals('X', App.board[1][1]); // correct placement
    }
    @Test
    void gameOverShouldReturnTrueWhenXWins() {
        App.initBoard();
        App.board[0][0] = 'X';
        App.board[0][1] = 'X';
        App.board[0][2] = 'X';

        boolean result = App.gameOver();
        assertTrue(result);
    }
    @Test
    void gameOverShouldReturnTrueWhenOWins() {
        App.initBoard();
        App.board[0][0] = 'O';
        App.board[1][0] = 'O';
        App.board[2][0] = 'O';

        boolean result = App.gameOver();
        assertTrue(result);
    }
    @Test
    void gameOverShouldReturnTrueWhenDraw() {
        App.initBoard();
        App.board[0] = new char[]{'X', 'O', 'X'};
        App.board[1] = new char[]{'O', 'X', 'O'};
        App.board[2] = new char[]{'O', 'X', 'X'};

        boolean result = App.gameOver();
        assertTrue(result);
    }
    @Test
    void gameOverShouldReturnFalseWhenGameNotOver() {
        App.initBoard();
        App.board[0][0] = 'X';

        boolean result = App.gameOver();
        assertFalse(result);
    }
}