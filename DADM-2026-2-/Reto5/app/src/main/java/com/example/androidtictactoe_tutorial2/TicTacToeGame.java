package com.example.androidtictactoe_tutorial2;

import java.util.Random;

public class TicTacToeGame {

    // Niveles de dificultad de la IA
    public enum DifficultyLevel {Easy, Harder, Expert};

    // Nivel de dificultad actual
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    public static final int BOARD_SIZE = 9;

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';

    private char mBoard[] = new char[BOARD_SIZE];
    private Random mRand;

    public TicTacToeGame() {
        mRand = new Random();
        clearBoard();
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }

    /** Limpia el tablero colocando espacios en blanco. */
    public void clearBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            mBoard[i] = OPEN_SPOT;
        }
    }

    /**
     * Registra la jugada en la posición dada.
     * Devuelve false si la casilla ya está ocupada o fuera del tablero.
     */
    public boolean setMove(char player, int location) {
        if (location >= 0 && location < BOARD_SIZE && mBoard[location] == OPEN_SPOT) {
            mBoard[location] = player;
            return true;
        }
        return false;
    }

    /** Devuelve lo que hay en la casilla: HUMAN_PLAYER, COMPUTER_PLAYER u OPEN_SPOT. */
    public char getBoardOccupant(int location) {
        return mBoard[location];
    }

    /** Verifica si hay un ganador (0: sigue el juego, 1: empate, 2: gana X, 3: gana O). */
    public int checkForWinner() {
        // Filas horizontales
        for (int i = 0; i <= 6; i += 3) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i+1] == HUMAN_PLAYER && mBoard[i+2] == HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER && mBoard[i+1] == COMPUTER_PLAYER && mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Columnas verticales
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i+3] == HUMAN_PLAYER && mBoard[i+6] == HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER && mBoard[i+3] == COMPUTER_PLAYER && mBoard[i+6] == COMPUTER_PLAYER)
                return 3;
        }

        // Diagonales
        if ((mBoard[0] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER && mBoard[4] == COMPUTER_PLAYER && mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER && mBoard[4] == COMPUTER_PLAYER && mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Comprobar empate
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        return 1;
    }

    /**
     * Obtiene el movimiento de la IA según el nivel de dificultad.
     * No modifica el tablero: hay que llamar a setMove() para aplicarlo.
     */
    public int getComputerMove() {
        int move = -1;

        if (mDifficultyLevel == DifficultyLevel.Easy) {
            move = getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.Expert) {
            // Intenta ganar; si no puede, bloquea; si tampoco, juega al azar.
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }

        return move;
    }

    /** Devuelve una casilla libre al azar. */
    private int getRandomMove() {
        int move;
        do {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] != OPEN_SPOT);
        return move;
    }

    /** Devuelve la casilla con la que gana la IA, o -1 si no existe. */
    private int getWinningMove() {
        return findCompletingMove(COMPUTER_PLAYER, 3);
    }

    /** Devuelve la casilla con la que ganaría el humano, o -1 si no existe. */
    private int getBlockingMove() {
        return findCompletingMove(HUMAN_PLAYER, 2);
    }

    /** Prueba cada casilla libre y deja el tablero como estaba. */
    private int findCompletingMove(char player, int winnerCode) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] == OPEN_SPOT) {
                mBoard[i] = player;
                int winner = checkForWinner();
                mBoard[i] = OPEN_SPOT;
                if (winner == winnerCode) {
                    return i;
                }
            }
        }
        return -1;
    }
}
