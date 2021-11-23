package com.example.imageview_camera_photos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This Activity shows an ImageView that can be filled with a picture from the camera or the gallery.
 * The user can choose which option they prefer by clicking one of the two different Buttons.
 * It also possible to repeat this process again.
 *
 * Necessary permissions in the AndroidManifest.xml file:
 * uses-permission android:name="android.permission.CAMERA"
 * uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
 *
 * Guide for getting pictures in the gallery of an emulator:
 * 1. Open Settings on the device
 * 2. Go to Storage and select Photos & Videos
 * 3. Select Images
 * 4. Drag and Drop pictures from your computer
 * 5. Cold Boot the emulator (AVD Manager)
 *
 * Layout File: activity_main.xml
 *
 * @author Lukas Plenk
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // These constants are used for checking if the user allowed certain permissions
    private static final int GALLERY_PERMISSION_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 2;

    // Controlling the program flow after entering the Activity from camera or gallery again
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private boolean isUri;

    // UI components
    private ImageView imageView;
    private Button buttonCamera, buttonGallery;

    /**
     * This method references the ImageView and the Buttons.
     * An ActivityResultLauncher gets defined.
     * It is needed because the camera or the gallery gives a result to this Activity.
     * @param savedInstanceState is a standard parameter.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        buttonCamera = findViewById(R.id.button_camera);
        buttonGallery = findViewById(R.id.button_gallery);

        buttonCamera.setOnClickListener(this);
        buttonGallery.setOnClickListener(this);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

            /**
             * Method for handling the previous Intent and its result code.
             * A switch case checks the result code from the camera or gallery.
             * There is also an if else statement for checking if the image is in Uri format or not.
             * @param result is the result that the MainActivity gets from previous Intents.
             */
            @Override
            public void onActivityResult(ActivityResult result) {

                Intent data = result.getData();


                switch (result.getResultCode()) {

                    case RESULT_CANCELED:
                        break;

                    case RESULT_OK:

                        if (isUri) {

                            imageView.setImageURI(data.getData());
                        }
                        else {

                            imageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
                        }
                        break;
                }
            }
        });
    }

    /**
     * The onClick method checks the interaction with the Buttons.
     * It executes the dialog for permission rights and the methods for getting the photo.
     * This depends on the current state of permission rights inside the app.
     * @param view is the UI component that was clicked on.
     */
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_camera) {

            if (getApplication().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                takePictureFromCamera();
            }
            else {

                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            }

        }
        else if (view.getId() == R.id.button_gallery) {

            if (getApplication().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                pickImageFromGallery();
            }
            else {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_CODE);
            }
        }
    }

    /**
     * Method for taking a picture from the camera.
     * Since the camera returns a Bitmap format, the isUri variable gets set to false.
     * An Intent leads to the camera itself and waits for a result in form of a photo.
     */
    private void takePictureFromCamera() {

        isUri = false;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intent);
    }

    /**
     * Method for selecting a picture from the gallery.
     * Since the gallery returns an Uri to the photo, the isUri variable gets set to true.
     * An Intent leads to the gallery itself and waits for a result in form of a photo.
     */
    private void pickImageFromGallery() {

        isUri = true;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    /**
     * Method for checking the request on camera and gallery.
     * If the request was allowed, the program shows a Toast to the user and takes action to get a photo.
     * If not, the program shows another Toast.
     * @param requestCode is the request code from the dialog.
     * @param permissions is the permission that was needed.
     * @param grantResults is used for distinguishing between multiple requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // This means the request was successful
        if (requestCode == CAMERA_PERMISSION_CODE) {

            // The first and only permission of the request was granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(MainActivity.this, "Camera Access ALLOWED", Toast.LENGTH_LONG).show();
                takePictureFromCamera();
            }
            else {

                Toast.makeText(MainActivity.this, "Camera Access DENIED", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == GALLERY_PERMISSION_CODE) {

            // The first and only permission of the request was granted
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(MainActivity.this, "Reading Gallery ALLOWED", Toast.LENGTH_LONG).show();
                pickImageFromGallery();
            }
            else {

                Toast.makeText(MainActivity.this, "Reading Gallery DENIED", Toast.LENGTH_LONG).show();
            }
        }
    }
}