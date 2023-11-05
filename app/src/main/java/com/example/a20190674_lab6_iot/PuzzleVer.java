package com.example.a20190674_lab6_iot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleVer extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;

    //public static boolean GANASTE = PuzzleActivity.GANASTE;
    public int Score;

    private Activity activity;
    private PuzzleB puzzleBoard;
    private ArrayList<PuzzleB> animation;
    private Random random = new Random();

    public PuzzleVer(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
        Score = -1;
    }

    public void initialize(Bitmap imageBitmap) {

        this.invalidate();
        int width = getMeasuredWidth();
        puzzleBoard = new PuzzleB(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("TAG","onDraw");
        super.onDraw(canvas);

        StfPuzzle.setScore(Score);

        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                    activity.finish();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            Score = 0;
            Log.d("TAG", "Shuffling completely...");

            int boardSize = puzzleBoard.getBoardSize();

            // Realiza movimientos aleatorios en el mismo tablero
            for (int i = boardSize - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int tileIndexA = i;
                int tileIndexB = j;

                // Intercambia las dos piezas aleatoriamente
                puzzleBoard.swapTiles(tileIndexA, tileIndexB);

                // Actualiza la vista
                invalidate();
            }
        } else {
            Toast.makeText(getContext(), "Take a picture and then try shuffle", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if(Score != -1){
                            Score++;
                        }
                        if (puzzleBoard.resolved()) {
                            StfPuzzle.setBestScore(Score);
                            String msg = "Lo lograste Felicidades \n Movimiento : "+Score;
                            if (Score == -1) {
                                msg = "Dale a inicio y luego continua el juego, Suerte";
                            }
                            Toast toast = Toast.makeText(activity, msg, Toast.LENGTH_LONG);
                            toast.show();
                            Score = -1;
                            activity.finish();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void modifyAttributeInActivity(boolean newValue) {
        StfPuzzle puzzleActivity = (StfPuzzle) activity;
        puzzleActivity.modifyAttributeFromMyClass(newValue);
    }

    private Comparator<PuzzleB> comparator = new Comparator<PuzzleB>() {
        @Override
        public int compare(PuzzleB lhs, PuzzleB rhs) {
            return lhs.priority() - rhs.priority();
        }
    };



    public void solve() {
        if( animation == null && puzzleBoard != null) {

            Score = -1;

            if( puzzleBoard.resolved() ){
                Toast.makeText(getContext(),"Already Solved!",Toast.LENGTH_SHORT).show();
                return ;
            }

            Log.d("TAG", "solve: start");

            int z = 0;
            PriorityQueue<PuzzleB> boardQueue = new PriorityQueue<>(1, comparator);

            PuzzleB current = new PuzzleB(puzzleBoard);
            current.setPreviousBoard(null);
            current.setStep(0);
            boardQueue.add(current);
            HashSet<String> set = new HashSet<>();

            //Time Testing
            long startTime = System.currentTimeMillis();

            while (!(boardQueue.isEmpty())) {

                Log.d("TAG", "Step : " + z);
                z++;
                PuzzleB bestState = boardQueue.poll();

                set.remove(bestState.convertToString());

                if (bestState.resolved()) {

                    ArrayList<PuzzleB> solution = new ArrayList<>();
                    while (bestState.getPreviousBoard() != null) {
                        solution.add(bestState);
                        bestState = bestState.getPreviousBoard();
                    }
                    Collections.reverse(solution);
                    boardQueue.clear();
                    animation = solution;

                    long endTime = System.currentTimeMillis();
                    long timeTaken = endTime-startTime;

                    Toast.makeText(getContext(),"Time : "+timeTaken+"ms",Toast.LENGTH_LONG).show();

                    invalidate(); //update UI
                    break;
                }
                else{
                    for (PuzzleB tempBoard : bestState.neighbours()) {
                        String s = tempBoard.convertToString();
                        if (!(set.contains(s))) {
                            set.add(s);
                            boardQueue.add(tempBoard);
                        }
                    }

                    long endTime = System.currentTimeMillis();
                    long timeTaken = endTime-startTime;
                    if(timeTaken > 10000){
                        boardQueue.clear();
                        Toast.makeText(getContext(),"Toma un poco de tiempo",Toast.LENGTH_LONG).show();
                        invalidate();
                        break;
                    }
                }
            }
        }
        else if (puzzleBoard == null) {
            Toast.makeText(getContext(),"Elige una figura para empezar el juego",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"Da click en solucionar",Toast.LENGTH_SHORT).show();
        }
    }

}
