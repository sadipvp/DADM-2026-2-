package com.example.androidtictactoe_tutorial2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AndroidTicTacToeActivity extends Activity {

    private TicTacToeGame mGame;
    private Button mBoardButtons[];
    private TextView mInfoTextView;
    private TextView mHumanScoreTextView;
    private TextView mTiesScoreTextView;
    private TextView mComputerScoreTextView;
    private boolean mGameOver; // Controla si la partida finalizó
    private boolean mHumanGoesFirst = true; // Se alterna en cada partida nueva
    private int mHumanWins = 0;
    private int mTies = 0;
    private int mComputerWins = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanScoreTextView = (TextView) findViewById(R.id.human_score);
        mTiesScoreTextView = (TextView) findViewById(R.id.ties_score);
        mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);
        mGame = new TicTacToeGame();

        updateScores();
        startNewGame();
    }

    private void startNewGame() {
        mGame.clearBoard();
        mGameOver = false; // Reiniciamos el estado del juego

        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        if (mHumanGoesFirst) {
            mInfoTextView.setText(R.string.first_human);
        } else {
            setMove(TicTacToeGame.COMPUTER_PLAYER, mGame.getComputerMove());
            mInfoTextView.setText(R.string.first_computer);
        }
        mHumanGoesFirst = !mHumanGoesFirst;
    }

    private void updateScores() {
        mHumanScoreTextView.setText(getString(R.string.score_human, mHumanWins));
        mTiesScoreTextView.setText(getString(R.string.score_ties, mTies));
        mComputerScoreTextView.setText(getString(R.string.score_computer, mComputerWins));
    }

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(R.string.new_game);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
    }

    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            // Solo permite jugar si el juego NO ha terminado y el botón está activo
            if (!mGameOver && mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_human);
                } else {
                    mGameOver = true; // Bloquea más movimientos
                    if (winner == 1) {
                        mTies++;
                        mInfoTextView.setText(R.string.result_tie);
                    } else if (winner == 2) {
                        mHumanWins++;
                        mInfoTextView.setText(R.string.result_human_wins);
                    } else {
                        mComputerWins++;
                        mInfoTextView.setText(R.string.result_computer_wins);
                    }
                    updateScores();
                }
            }
        }
    }
}