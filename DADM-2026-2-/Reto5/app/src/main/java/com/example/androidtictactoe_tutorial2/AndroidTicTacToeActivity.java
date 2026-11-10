package com.example.androidtictactoe_tutorial2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidTicTacToeActivity extends Activity {

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    private TicTacToeGame mGame;
    // Espera antes de que juegue Android (extra challenge)
    static final int COMPUTER_DELAY_MS = 1000;

    private BoardView mBoardView;
    private TextView mInfoTextView;
    private TextView mHumanScoreTextView;
    private TextView mTiesScoreTextView;
    private TextView mComputerScoreTextView;
    private boolean mGameOver; // Controla si la partida finalizó
    private boolean mHumanGoesFirst = true; // Se alterna en cada partida nueva
    private int mHumanWins = 0;
    private int mTies = 0;
    private int mComputerWins = 0;
    private boolean mHumanTurn; // Impide que el humano juegue mientras Android "piensa"

    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanScoreTextView = (TextView) findViewById(R.id.human_score);
        mTiesScoreTextView = (TextView) findViewById(R.id.ties_score);
        mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);
        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        buildBottomMenu();
        updateScores();
        startNewGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human_move);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.computer_move);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
        mHumanMediaPlayer = null;
        mComputerMediaPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void startNewGame() {
        mHandler.removeCallbacksAndMessages(null); // Cancela una jugada de Android pendiente
        mGame.clearBoard();
        mBoardView.invalidate(); // Redibuja el tablero vacío
        mGameOver = false;

        if (mHumanGoesFirst) {
            mHumanTurn = true;
            mInfoTextView.setText(R.string.first_human);
        } else {
            mHumanTurn = false;
            mInfoTextView.setText(R.string.first_computer_thinking);
            scheduleComputerMove();
        }
        mHumanGoesFirst = !mHumanGoesFirst;
    }

    /** Android juega después de una espera, sin bloquear la interfaz. */
    private void scheduleComputerMove() {
        mHandler.postDelayed(new Runnable() {
            public void run() {
                setMove(TicTacToeGame.COMPUTER_PLAYER, mGame.getComputerMove());
                mHumanTurn = true;
                if (!checkGameOver()) {
                    mInfoTextView.setText(R.string.turn_human);
                }
            }
        }, COMPUTER_DELAY_MS);
    }

    /** Revisa si alguien ganó o hubo empate; si es así, actualiza marcador y mensaje. */
    private boolean checkGameOver() {
        int winner = mGame.checkForWinner();
        if (winner == 0) {
            return false;
        }

        mGameOver = true;
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
        return true;
    }

    private void updateScores() {
        mHumanScoreTextView.setText(getString(R.string.score_human, mHumanWins));
        mTiesScoreTextView.setText(getString(R.string.score_ties, mTies));
        mComputerScoreTextView.setText(getString(R.string.score_computer, mComputerWins));
    }

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate(); // Redibuja el tablero
            MediaPlayer sound = (player == TicTacToeGame.HUMAN_PLAYER)
                    ? mHumanMediaPlayer : mComputerMediaPlayer;
            if (sound != null) {
                sound.start();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    /** Oculta el menú de la barra superior: las opciones se muestran abajo, como en el tutorial. */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * Android ya no muestra el menú inferior con iconos de las versiones 2.x, así que se
     * reconstruye con una vista por cada opción de options_menu.xml.
     */
    private void buildBottomMenu() {
        LinearLayout bar = (LinearLayout) findViewById(R.id.bottom_menu);
        Menu menu = new PopupMenu(this, bar).getMenu();
        onCreateOptionsMenu(menu);

        float density = getResources().getDisplayMetrics().density;
        int iconSize = Math.round(32 * density);
        int padding = Math.round(8 * density);

        TypedValue ripple = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, ripple, true);

        for (int i = 0; i < menu.size(); i++) {
            final MenuItem item = menu.getItem(i);

            Drawable icon = item.getIcon();
            icon.setBounds(0, 0, iconSize, iconSize);

            TextView option = new TextView(this);
            option.setText(item.getTitle());
            option.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            option.setGravity(Gravity.CENTER);
            option.setCompoundDrawablesRelative(null, icon, null, null);
            option.setPadding(0, padding, 0, padding);
            option.setBackgroundResource(ripple.resourceId);
            option.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    onOptionsItemSelected(item);
                }
            });

            // Todas las opciones ocupan el mismo ancho
            bar.addView(option, new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Se usa if/else porque con el AGP actual los R.id no son constantes y no sirven en un switch.
        int id = item.getItemId();
        if (id == R.id.new_game) {
            startNewGame();
            return true;
        } else if (id == R.id.ai_difficulty) {
            showDialog(DIALOG_DIFFICULTY_ID);
            return true;
        } else if (id == R.id.quit) {
            showDialog(DIALOG_QUIT_ID);
            return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);

                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // El orden de los niveles coincide con el del enum DifficultyLevel.
                int selected = mGame.getDifficultyLevel().ordinal();

                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss();

                                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);

                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;

            case DIALOG_QUIT_ID:
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }

        return dialog;
    }

    // Escucha los toques sobre el tablero
    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determina qué celda se tocó
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;

            if (!mGameOver && mHumanTurn && col < 3 && row < 3
                    && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
                if (!checkGameOver()) {
                    mHumanTurn = false;
                    mInfoTextView.setText(R.string.turn_computer);
                    scheduleComputerMove();
                }
            }

            // Así no recibimos los eventos de mover o levantar el dedo
            return false;
        }
    };
}
