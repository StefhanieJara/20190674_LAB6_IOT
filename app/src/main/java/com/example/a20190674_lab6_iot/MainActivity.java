package com.example.a20190674_lab6_iot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static int LEVEL=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LEVEL = 3;
        this.LEVEL = chooseRandomSize();

        Button btnIrAActivity1 = findViewById(R.id.button1);

        btnIrAActivity1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StfPuzzle.class);
                startActivity(intent);
            }
        });


        Button btnIrAActivity2 = findViewById(R.id.button2);

        btnIrAActivity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StfMemory.class);
                startActivity(intent);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        this.LEVEL = chooseRandomSize();
    }

    public static int chooseRandomSize() {
        // Crear una instancia de la clase Random
        Random random = new Random();

        // Crear un array con los números 3, 4 y 5
        int[] numbers = {3, 4, 5};

        // Elegir aleatoriamente un índice del array
        int randomIndex = random.nextInt(numbers.length);

        // Devolver el número correspondiente al índice elegido
        return numbers[randomIndex];
    }


}