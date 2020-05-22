package com.hmfl.careasy.bsdiff;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.hmfl.careasy.mybspatch.BsPatchUtil;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_main);
        findViewById(R.id.aaa);
        Button button = findViewById(R.id.button);
        mProgressBar = findViewById(R.id.progress);
        //申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 200);
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smartupdate();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != mDisposable) {
            mDisposable.dispose();
        }
    }

    private void smartupdate() {
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
//                File oldApk = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "old.apk");
//                //定义生成的新包
//                File newApk = new File(Environment.getExternalStorageDirectory(), "new1.apk");
//                //假设patch.patch文件已经下载到sdcard上，切已经校验通过
//                File patch = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "patch.patch");
                File oldApk = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/BSDiff/", "old.apk");
                //定义生成的新包
                File newApk = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/BSDiff/", "new1.apk");
                //假设patch.patch文件已经下载到sdcard上，切已经校验通过
                File patch = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/BSDiff/", "patch.patch");
                if(!patch.exists()) {
                    emitter.onError(new IOException("patch file not exist!"));
                    return;
                }
                //合并差分包
                BsPatchUtil.patch(oldApk.getAbsolutePath(), patch.getAbsolutePath(), newApk.getAbsolutePath());
                if (newApk.exists()) {
                    emitter.onNext(newApk);
                    emitter.onComplete();
                    patch.delete();
                } else {
                    emitter.onError(new IOException("bspatch failed,file not exist!"));
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(File file) {
                        Log.i("MainActivity", "****onNext****");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("MainActivity", "****onError****");
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        Log.i("MainActivity", "****onComplete****");
                        mProgressBar.setVisibility(View.GONE);
                    }
                });

    }
}
