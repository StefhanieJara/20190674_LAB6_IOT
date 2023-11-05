package com.example.a20190674_lab6_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

public class StfPuzzle extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static boolean GANASTE;
    private static final int REQUEST_OPEN_GALLERY = 1;
    private Bitmap imageBitmap = null;
    private PuzzleVer boardView;
    private static TextView bestScore;

    private Button solveButton;
    private static TextView score;
    private static SharedPreferences sharedpreferences;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stf_puzzle);

        score = (TextView) findViewById(R.id.puntaje1);
        bestScore = (TextView) findViewById(R.id.mejor1);
        GANASTE=false;
        // This code programmatically adds the PuzzleBoardView to the UI.
        RelativeLayout container = (RelativeLayout) findViewById(R.id.puzzle_container);
        boardView = new PuzzleVer(this);
        // Some setup of the view.
        boardView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        container.addView(boardView);

        solveButton = (Button) findViewById(R.id.solucion);
        sharedpreferences = getSharedPreferences("Mejor Puntaje", Context.MODE_PRIVATE);
        setBestScore(-1);

        container.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.descargar);
                boardView.initialize(bitmap);
                if (GANASTE) {
                    Intent intent = new Intent(StfPuzzle.this, MainActivity.class);
                    GANASTE = false;
                    startActivity(intent);
                    finish();
                }
            }
        });
        Button botonSolve = findViewById(R.id.solucion);
        botonSolve.setEnabled(false);


    }



    public void modifyAttributeFromMyClass(boolean newValue) {
        this.GANASTE = newValue;
    }


    public void dispatchTakePictureIntent(View view) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        startActivityForResult(openGalleryIntent, REQUEST_OPEN_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    boardView.initialize(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } //codigo para recoger imagen de la galeria


    }

    public void shuffleImage(View view) {
        boardView.shuffle();
    }

    public void solve(View view) {

        solveButton.setClickable(false);
        boardView.solve();
        solveButton.setClickable(true);

    }

    public void home(View view){
        Intent in = new Intent(this,MainActivity.class);
        startActivity(in);
        finish();
    }

    public static void setScore(int Score){
        score.setText(""+Score);
        return ;
    }

    public static void setBestScore(int bScore){

        SharedPreferences.Editor editor = sharedpreferences.edit();
        String key = Integer.toString(MainActivity.LEVEL);

        int bestscore = sharedpreferences.getInt(key,-1);
        if(bestscore == -1 ){
            bestScore.setText("--");
        }
        else {
            bestScore.setText("" + bestscore);
        }
        if(bScore == -1){
            return ;
        }

        String tempScore=  bestScore.getText().toString();
        if(tempScore.equals("--")){
            bestScore.setText("" + bScore);
            editor.putInt(key, bScore);
            editor.commit();
            return ;
        }
        int temp = Integer.parseInt(tempScore);
        if(temp > bScore) {
            bestScore.setText("" + bScore);
            editor.putInt(key, bScore);
            editor.commit();
        }
    }
}