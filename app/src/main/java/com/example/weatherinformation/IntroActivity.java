package com.example.weatherinformation;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class IntroActivity extends AppCompatActivity {
    private static final String TAG = IntroActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 인트로 화면을 1초동안 보여주고 메인으로 이동
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 초기화
            IntroActivityPermissionsDispatcher.initWithPermissionCheck(IntroActivity.this);
        }, Constants.LoadingDelay.LONG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        IntroActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onBackPressed() {
        // 백키 눌려도 종료 안되게 하기 위함
        //super.onBackPressed();
    }

    /* 초기화 */
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    void init() {
        // 메인으로 이동
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    void showRationale(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.dialog_allow, (dialog, which) -> request.proceed())
                .setNegativeButton(R.string.dialog_deny, (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(getString(R.string.permission_rationale_app_use))
                .show();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    void showDenied() {
        Toast.makeText(this, getString(R.string.permission_rationale_app_use), Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    void showNeverAsk() {
        Toast.makeText(this, getString(R.string.permission_rationale_app_use), Toast.LENGTH_LONG).show();
    }
}
