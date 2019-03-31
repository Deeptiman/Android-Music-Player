package com.example.musicplayer.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class PermissionModel implements ActivityCompat.OnRequestPermissionsResultCallback {

    String TAG = "PERMISSION_TAG";

    private static final int RECORD_AUDIO_REQUEST_PERMISSIONS = 100;

    private boolean REQUEST_PERMISSION_MODE = false;

    private Activity activity;
    private Context context;
    private PermissionContract mPermissionContract;

    public void checkRecordAudioPermission(Activity activity, PermissionContract permissionContract) {
        this.activity = activity;
        this.context = activity;
        this.mPermissionContract = permissionContract;
        if ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {

            Log.d(TAG, "Check Permission");

            if ((ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.RECORD_AUDIO))) {

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != -1) {

                    Log.d(TAG, "onReceivePermission :");

                    REQUEST_PERMISSION_MODE = false;
                    mPermissionContract.onReceivePermission();
                } else {
                    Log.d(TAG, "addPermission :");
                    addPermission();
                }

            } else {

                Log.d(TAG, "requestPermissions :");

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST_PERMISSIONS);
            }
        } else {

            Log.d(TAG, "Permissions : Exists");

            mPermissionContract.onReceivePermission();
        }
    }

    public void hasRecordAudioPermission() {
        REQUEST_PERMISSION_MODE = true;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != -1) {

            if (alertDialog != null)
                alertDialog.dismiss();

            if (REQUEST_PERMISSION_MODE) {
                REQUEST_PERMISSION_MODE = false;
                mPermissionContract.onReceivePermission();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult : requestCode = "+requestCode);

        switch (requestCode) {

            case RECORD_AUDIO_REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(activity, "Access Granted", Toast.LENGTH_SHORT).show();
                        REQUEST_PERMISSION_MODE = false;

                        Log.d(TAG, "onRequestPermissionsResult : onReceivePermission");

                        mPermissionContract.onReceivePermission();
                    } else {
                        addPermission();

                        Log.d(TAG, "onRequestPermissionsResult : No Access");
                    }
                }
            }
            break;
        }
    }

    AlertDialog alertDialog;

    private void addPermission() {
        REQUEST_PERMISSION_MODE = true;
        Log.d(TAG, "addPermission");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage("Allow MusicApp to record audio on your device?");
        alertDialogBuilder.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivity(intent);
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public interface PermissionContract {
        public void onReceivePermission();
    }

}

