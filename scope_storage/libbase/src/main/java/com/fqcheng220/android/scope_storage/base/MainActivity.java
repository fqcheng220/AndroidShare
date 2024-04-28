package com.fqcheng220.android.scope_storage.base;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description: (用一句话描述该文件做什么)
 * @date 2024/4/28 16:01
 *
 * targetsdkversion为33的话
 *
 * 1.写图片到外部共享空间
 * 1.1 运行系统版本低于10（目前只测试了Android7.0，待验证8.0 9.0！！！）
 * 通过路径写，需要静态配置权限和运行时动态申请权限"android.permission.WRITE_EXTERNAL_STORAGE"
 * 通过MediaStore写，需要静态配置权限和运行时动态申请权限"android.permission.WRITE_EXTERNAL_STORAGE"
 *
 * 1.2 运行系统版本等于10\11
 *  * 通过路径写，不管申请不申请权限，都会报错找不到问题java.lang.RuntimeException: java.io.FileNotFoundException open
 *  failed:，除非禁用分区存储功能？？？（待验证！！！）
 *  EACCES (Permission denied)
 *  * 通过MediaStore写，不需要申请任何权限，可以成功写入
 *
 *  1.3 运行系统版本等于12\13
 *  *  * 通过路径写，不需要申请任何权限(Android12可以申请，也可以不申请，不影响流程；Android13不申请权限直接写报错java.lang.RuntimeException:
 *  java
 *  .io.FileNotFoundException,Android13设备用AS断点不生效，待验证！！！)
 *  *  * 通过MediaStore写，不需要申请任何权限(Android12可以申请，也可以不申请，不影响流程；Android13不申请直接写正常,Android13设备用AS断点不生效，待验证！！！)
 */
public class MainActivity extends Activity {
    private static final String TAG  = "MainActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_write_external_pub_images_dir1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testWriteExternalPubImage1();
            }
        });
        findViewById(R.id.btn_write_external_pub_images_dir2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testWriteExternalPubImage2();
            }
        });
    }

    private void testWriteExternalPubImage1() {
        print("testWriteExternalPubImage1");
        if (Build.VERSION.SDK_INT < 31 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            //            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            testWriteExternalPubImage1Actually();
        }
    }

    private void testWriteExternalPubImage1Actually() {
        print("testWriteExternalPubImage1Actually");
        File directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(directory, "displayName1.png");
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imageFile);
            outputStream.write(0x1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 一开始在引用该library的app模块中没有在manifest中配置权限“android.permission
     * .WRITE_EXTERNAL_STORAGE”，只是包含动态申请权限代码，
     * 结果就是可以打开授权弹窗，但是立刻就消失
     *
     * 原因是引用该library的app模块必须主动再次在manifest中配置权限“android.permission
     * .WRITE_EXTERNAL_STORAGE”，这是Android grale编译插件merge manifest决定的！！！
     * 否则该app模块打包生成的apk文件中AndroidManifest.xml是不包含library的AndroidManifest
     * .xml配置权限“android.permission.WRITE_EXTERNAL_STORAGE”
     */
    private void testWriteExternalPubImage2() {
        print("testWriteExternalPubImage2");
        if (Build.VERSION.SDK_INT < 31 && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            testWriteExternalPubImage2Actually();
        }
    }

    private void testWriteExternalPubImage2Actually() {
        print("testWriteExternalPubImage2Actually");
        Uri imageCollections;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollections =
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageCollections = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues imageDetails = new ContentValues();
        imageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, "displayName2");
        imageDetails.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        }
        ContentResolver resolver = this.getApplication().getContentResolver();
        Uri imageContentUri = resolver.insert(imageCollections, imageDetails);
        OutputStream outputStream = null;
        try {
            outputStream = resolver.openOutputStream(imageContentUri, "w");
            outputStream.write(0x1);
            //                 os -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageDetails.clear();
                imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
                resolver.update(imageContentUri, imageDetails, null, null);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        switch (requestCode){
            case 100:
                testWriteExternalPubImage2Actually();
                break;
            case 101:
                testWriteExternalPubImage1Actually();
                break;
        }
    }

    private void print(String msg){
        android.util.Log.d(TAG,msg);
    }
}
