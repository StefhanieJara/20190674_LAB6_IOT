package com.example.a20190674_lab6_iot;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PuzzleTitulo {

    private Bitmap bitmap;
    private int number;

    public PuzzleTitulo(Bitmap bitmap, int number){
        this.bitmap = bitmap;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void draw(Canvas canvas, int x, int y) {
        canvas.drawBitmap(bitmap, x * bitmap.getWidth(), y * bitmap.getHeight(), null);
    }


    public boolean isClicked(float clickX, float clickY, int tileX, int tileY) {
        int tileX0 = tileX * bitmap.getWidth();
        int tileX1 = (tileX + 1) * bitmap.getWidth();
        int tileY0 = tileY * bitmap.getWidth();
        int tileY1 = (tileY + 1) * bitmap.getWidth();
        return (clickX >= tileX0) && (clickX < tileX1) && (clickY >= tileY0) && (clickY < tileY1);
    }
}
