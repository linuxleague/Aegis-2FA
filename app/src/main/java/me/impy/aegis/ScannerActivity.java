package me.impy.aegis;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.impy.aegis.crypto.KeyInfo;
import me.impy.aegis.crypto.TOTP;

public class ScannerActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        handleDummyResult();

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        mScannerView.setFormats(getSupportedFormats());

        ActivityCompat.requestPermissions(ScannerActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    public void handleDummyResult() {
        // Do something with the result here
        //Toast.makeText(this, rawResult.getText(), Toast.LENGTH_SHORT).show();

        try {
            KeyInfo info = KeyInfo.FromURL("otpauth://totp/ACME%20Co:john@example.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ");
            KeyProfile keyProfile = new KeyProfile();
            keyProfile.KeyInfo = info;
            keyProfile.Name = info.getLabel();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("KeyProfile", keyProfile);

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_SHORT).show();

        try {
            KeyInfo info = KeyInfo.FromURL(rawResult.getText());
            KeyProfile keyProfile = new KeyProfile();
            keyProfile.KeyInfo = info;
            keyProfile.Name = info.getLabel();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("KeyProfile", keyProfile);

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
                    mScannerView.startCamera();          // Start camera on resume
                } else {
                    Toast.makeText(ScannerActivity.this, "Permission denied to get access to the camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private List<BarcodeFormat> getSupportedFormats() {
        ArrayList<BarcodeFormat> supportedFormats = new ArrayList<>();
        supportedFormats.add(BarcodeFormat.QR_CODE);

        return supportedFormats;
    }
}