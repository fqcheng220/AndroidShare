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
 * 参考文章
 * https://juejin.cn/post/7306815404562137128
 * https://medium.com/@wanxiao1994/android%E5%AD%98%E5%82%A8%E6%9D%83%E9%99%90%E9%80%82%E9%85%8D%E4%B8%8E%E8%AF%BB%E5%86%99%E5%AA%92%E4%BD%93%E6%96%87%E4%BB%B6-5c2004a62dfa
 *
 * ===============================================================
 * targetsdkversion为33的话
 * ===============================================================
 *
 * 1.写图片到外部共享空间
 * 1.1 运行系统版本低于10（目前只测试了Android7.0和9.0，待验证8.0！！！）
 * 通过路径写，需要静态配置权限和运行时动态申请权限"android.permission.WRITE_EXTERNAL_STORAGE"，一个静态，一个动态，都需要
 * 如果没动态申请，则报错java.lang.RuntimeException: java.io.FileNotFoundException (Permission denied)
 * ，如果只有动态没静态，则授权弹窗弹出后立刻消失（闪一下）
 * 通过MediaStore写，需要静态配置权限和运行时动态申请权限"android.permission.WRITE_EXTERNAL_STORAGE"，一个静态，一个动态，都需要
 *  * 如果没动态申请，则报错java.lang.SecurityException: Permission Denial: writing com.android.providers.media.MediaProvider uri content://media/external/images/media from pid=3002, uid=10071 requires android.permission.WRITE_EXTERNAL_STORAGE, or grantUriPermission()
 *  * ，如果只有动态没静态，则授权弹窗弹出后立刻消失（闪一下）
 *
 * 1.2 运行系统版本等于10\11
 *  * 通过路径写，不管申请不申请权限，都会报错找不到文件java.lang.RuntimeException: java.io.FileNotFoundException open
 *  failed:，Android10除非禁用分区存储功能？？？Android11禁用也无效？？？（待验证！！！）
 *  EACCES (Permission denied)
 *  * 通过MediaStore写，不需要申请任何权限，可以成功写入，申请了也可以成功写入
 *
 *  1.3 运行系统版本等于12\13
 *  *  * 通过路径写，不需要申请任何权限(Android12可以申请，也可以不申请，不影响正常写入流程;
 *  注意：Android13不申请权限直接写报错java.lang.RuntimeException:java.io.FileNotFoundException,但是如果动态申请的话，requestPermisssion根本没任何反应，不会有授权弹窗，没法走下去，所以Android13必须禁止动态申请权限)
 *  *  * 通过MediaStore写，不需要申请任何权限(Android12可以申请，也可以不申请，不影响正常写入流程;
 *  注意：Android13不申请直接写正常,但是如果动态申请的话，requestPermisssion根本没任何反应，不会有授权弹窗，没法走下去，所以Android13必须禁止动态申请权限)
 *
 *
 *
 * ===============================================================
 *  targetsdkversion为29的话(
 *  总结：
 *  同一套代码，光改变targetsdkversion的话，targetsdkversion为33编译生成的apk
 *  与targetsdkversion为29编译生成的apk安装运行在同一系统上的表现差异，
 *  只会出现在版本大于29到33的系统上，版本小于等于29和大于33的系统上运行两个apk表现无差异)
 *
 *  测试验证结果：
 *  只有在Android13系统上运行表现有差异，
 *  targetsdkversion为29的apk安装运行在Android13上可以动态申请权限WRITE_EXTERNAL_STORAGE并弹窗，如果是通过文件路径形式写图片的话，会写失败
 *  targetsdkversion为33的apk安装运行在Android13上禁止动态申请权限WRITE_EXTERNAL_STORAGE，否则调用申请逻辑不会弹任何授权弹窗，流程终止
 *  ===============================================================
 *  *
 *  * 1.写图片到外部共享空间
 *  * 1.2 运行系统版本11
 *  *  * 通过路径写，不管申请不申请权限，都会报错找不到文件java.lang.RuntimeException: java.io.FileNotFoundException open
 *  *  failed:，Android10除非禁用分区存储功能？？？Android11禁用也无效？？？（待验证！！！）
 *  *  EACCES (Permission denied)
 *  *  * 通过MediaStore写，不需要申请任何权限，可以成功写入，申请了也可以成功写入
 *  *
 *  *  1.3 运行系统版本等于12\13
 *  *  *  * 通过路径写，不需要申请任何权限(Android12可以申请，也可以不申请，不影响正常写入流程;
 *  注意：Android13可以申请，也可以不申请，只是不管申请成功后还是不申请直接写都不影响最后报错java.lang.RuntimeException:java.io.FileNotFoundException)
 *  *  *  * 通过MediaStore写，不需要申请任何权限(Android12、13可以申请，也可以不申请，不影响正常写入流程;)
 *
 *
 *
 *  * ===============================================================
 *  *  targetsdkversion为28的话同理(
 *  *  总结：
 *  *  同一套代码，光改变targetsdkversion的话，targetsdkversion为29编译生成的apk
 *  *  与targetsdkversion为28编译生成的apk安装运行在同一系统上的表现差异，
 *  *  只会出现在版本等于29的系统上，版本小于等于28和大于29的系统上运行两个apk表现无差异)
 *
 *  测试验证结果：
 *  *  只有在Android10系统上运行表现有差异，
 *  *  targetsdkversion为28的apk安装运行在Android10上如果是通过文件路径形式写图片的话，必须动态申请权限WRITE_EXTERNAL_STORAGE
 *  ，申请成功后才能正常写
 *  *  targetsdkversion为29的apk安装运行在Android10上如果是通过文件路径形式写图片的话，不管申请不申请权限WRITE_EXTERNAL_STORAGE，写图片都失败
 *  *  ===============================================================
 *  *  *
 *  *  * 1.写图片到外部共享空间
 *  *  * 1.2 运行系统版本等于10
 *  *  * 通过路径写，必须申请权限成功后才能正常写入，否则报错找不到文件java.lang.RuntimeException: java.io
 *  .FileNotFoundException open failed:EACCES (Permission denied)
 *  *  * 通过MediaStore写，不需要申请任何权限，可以成功写入，申请了也可以成功写入
 *
 *
 *
 *  ===============================================================================================
 *  结论：
 *  1.针对写图片到外部共享空间这个场景：
 *  推荐直接使用MediaStore写，只要运行系统环境低于29（跟targetsdkversion无关），才动态申请权限 WRITE_EXTERNAL_STORAGE，否则不申请
 *  不推荐用文件路径写的原因是，只要targetsdkversion大于等于29，运行在Android10或者以上系统都有可能crash或者失效
 *  ===============================================================================================
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
//        boolean compatible =
//                (29 == getTargetVersion() && Build.VERSION.SDK_INT < 29) || (33 == getTargetVersion() && Build.VERSION.SDK_INT < 29);
        boolean compatible = Build.VERSION.SDK_INT < 29;
        if (compatible && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            //            requestPermissions(new String[]{Manifest.permission
            //            .WRITE_EXTERNAL_STORAGE}, 100);
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
//        boolean compatible =
//                (29 == getTargetVersion() && Build.VERSION.SDK_INT < 29) || (33 == getTargetVersion() && Build.VERSION.SDK_INT < 29);
        boolean compatible = Build.VERSION.SDK_INT < 29;
        if (compatible && ContextCompat.checkSelfPermission(this,
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

    private int getTargetVersion(){
        return getApplicationInfo().targetSdkVersion;
    }
}
