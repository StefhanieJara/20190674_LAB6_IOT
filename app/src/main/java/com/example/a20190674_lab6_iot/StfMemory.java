package com.example.a20190674_lab6_iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;


public class StfMemory extends AppCompatActivity {



    Button botonReiniciar, botonSalir;
    TextView textoPuntuacion;

    private Button agregarImagenButton;




    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    private static final int PICK_IMAGE_REQUEST = 1;
    int numeroDeImagenesSubidas = 0;
    int numeroDeImagenesSeleccionadas = 0;

    private Handler handler = new Handler();



    private ArrayList<Uri> imageUrisLista = new ArrayList<>();

    private int numRows, numColumns;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stf_memory);

        setContentView(R.layout.activity_stf_memory);

        ImageView imageView = findViewById(R.id.imageView);

        agregarImagenButton = findViewById(R.id.AgregarImagen);

        agregarImagenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numeroDeImagenesSubidas < 15) {
                    // Abre el explorador de archivos para seleccionar una imagen
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                } else {
                    // Muestra un mensaje al usuario indicando que ha alcanzado el límite máximo de imágenes.
                    Toast.makeText(StfMemory.this, "Ha alcanzado el límite máximo de imágenes (15)", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Obtiene la URI de la imagen seleccionada
            if (numeroDeImagenesSubidas < 15) {
                Uri imageUri = data.getData();

                // Oculta el botón "AgregarImagen" durante 10 segundos
                agregarImagenButton.setVisibility(View.INVISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Muestra nuevamente el botón después de 10 segundos
                        agregarImagenButton.setVisibility(View.VISIBLE);
                    }
                }, 10000); // 10000 milisegundos = 10 segundos

                // Sube la imagen a Firebase Storage
                uploadImageToFirebaseStorage(imageUri);

                numeroDeImagenesSubidas++;
                numeroDeImagenesSeleccionadas++;
                TextView seleccionarImagenes = findViewById(R.id.SeleccionImagenes);
                seleccionarImagenes.setText("Total de imágenes seleccionadas: " + numeroDeImagenesSeleccionadas);

                // Agregar un retraso de 15 segundos antes de agregar las imágenes al tablero
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Luego, puedes agregar las imágenes al tablero y continuar con la lógica del juego de memoria
                        addImagesToGridLayout(imageUrisLista);
                    }
                }, 10000); // 15000 milisegundos = 15 segundos
            } else {
                Toast.makeText(this, "Ha alcanzado el límite máximo de imágenes (15)", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void uploadImageToFirebaseStorage(Uri imageUri) {

        ImageView imageView = findViewById(R.id.imageView);

        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // La imagen se ha subido exitosamente
                        // Puedes obtener la URL de la imagen subida
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                String imageUrl = downloadUri.toString();
                                //agregamos las referencias
                                imageUrisLista.add(downloadUri);

                                // Puedes mostrar la imagen en el ImageView
                                Picasso.get().load(imageUrl).into(imageView);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al subir la imagen
                    }
                });

    }

    private void addImagesToGridLayout(ArrayList<Uri> imageUris) {
        GridLayout gridLayout = findViewById(R.id.gridLayout);

        // Elimina todas las vistas existentes en el GridLayout
        gridLayout.removeAllViews();

        // Duplica las imágenes para que haya dos de cada una
        ArrayList<Uri> duplicatedImageUris = new ArrayList<>();
        for (Uri imageUri : imageUris) {
            duplicatedImageUris.add(imageUri);
            duplicatedImageUris.add(imageUri); // Agrega una copia de la imagen
        }
        Collections.shuffle(duplicatedImageUris);

        int totalImages = duplicatedImageUris.size();


        // Establece el número de filas y columnas en función del número de elementos en la lista
        int numRows, numColumns;

        switch (totalImages) {
            case 4:
                numRows = 2;
                numColumns = 2;
                break;
            case 6:
                numRows = 2;
                numColumns = 3;
                break;
            case 12:
                numRows = 4;
                numColumns = 3;
                break;
            case 16:
                numRows = 4;
                numColumns = 4;
                break;
            case 20:
                numRows = 4;
                numColumns = 5;
                break;
            case 30:
                numRows = 6;
                numColumns = 5;
                break;
            default:
                // Si no se cumple ninguna de las condiciones anteriores, muestra un mensaje de error o realiza otra acción según lo necesites.
                Toast.makeText(this, "Número de elementos no compatible", Toast.LENGTH_SHORT).show();
                return;
        }

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                int index = row * numColumns + col;

                if (index < totalImages) {
                    Uri imageUri = duplicatedImageUris.get(index);

                    // Crea un ImageView para mostrar la imagen
                    ImageView imageView = new ImageView(this);

                    // Asigna la imagen a la ImageView
                    Picasso.get().load(imageUri.toString()).into(imageView);

                    // Configura un clic en el ImageView para manejar la lógica del juego
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Agrega aquí la lógica del juego de memoria al hacer clic en una imagen
                        }
                    });

                    // Agrega el ImageView al GridLayout
                    gridLayout.addView(imageView);
                }
            }
        }
    }












}