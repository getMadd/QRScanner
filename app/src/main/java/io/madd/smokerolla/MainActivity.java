package io.madd.smokerolla;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(getApplicationContext());

        setContentView(R.layout.activity_main);
        button = findViewById(R.id.lauch_Btn);


        // Start the Camera for Barcode
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }
        });


    }

    //Retrieve result from Camera with presumed Barcode
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {


            Bundle extras = data.getExtras();
            Bitmap qrScan = (Bitmap) extras.get("data");

            // Use Firebase ML Vision to check if barcode exist in Img
            try {

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(qrScan);

                if (image != null) {
                    findBarcodeInImg(image);
                }
            }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    public void findBarcodeInImg(FirebaseVisionImage barcode){

        // Firebase Setup

        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_QR_CODE).build();


        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector(options);

        // Checking in background

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(barcode)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        // Task completed successfully
                        // ...
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Toast.makeText(MainActivity.this, "Plummeted", Toast.LENGTH_SHORT).show();
                    }
                });


        // If the reults array is not empty, and result is URL
        if(result.getResult().size() > 0){

            for(FirebaseVisionBarcode barcode1:result.getResult()){

                if(barcode1.getValueType() == FirebaseVisionBarcode.TYPE_URL){

                    // Show URL on Screen
                    System.out.print(barcode1.getRawValue());


                    // TODO: Take user to that address and do stuff
                }
            }
        }
        


    }

}



