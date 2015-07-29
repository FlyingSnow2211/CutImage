package com.hxht.testimageupload;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends Activity {

    private static final int REQUEST_TO_CAMERA = 1;
    private static final int REQUEST_TO_PHOTOALBUM = 2;
    private static final int REQUEST_TO_PHOTOCUTED = 3;
    private static final String fileName = "temp.jpg";
    private ImageView viewById;
    private File tempFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        viewById = (ImageView) findViewById(R.id.iv);
    }

    /**
     * 拍照
     *
     * @param view
     */
    public void toCamera(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (isExistSd()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(),fileName)));
        }
        startActivityForResult(intent,REQUEST_TO_CAMERA);
    }

    /**
     * 从相册中选择图片
     *
     * @param view
     */
    public void toPhotoAlbum(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,REQUEST_TO_PHOTOALBUM);
    }

    /**
     * Intent请求回调函数
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TO_CAMERA) {
            if (isExistSd()){
                tempFile = new File(Environment.getExternalStorageDirectory(),fileName);
                cutImage(Uri.fromFile(tempFile));
            }else {
                Toast.makeText(this, "SD卡不存在，图片保存失败", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_TO_PHOTOALBUM) {
            if (data != null){
                Uri uri = data.getData();
                cutImage(uri);
            }
        } else if (requestCode == REQUEST_TO_PHOTOCUTED) {
            try {
                Bitmap bitmap = data.getParcelableExtra("data");
                viewById.setImageBitmap(bitmap);
                boolean delete = tempFile.delete();
                System.out.println("delete------------------------------" + delete);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 定义方法判断SD卡的存在性
     */
    private boolean isExistSd() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * 定义方法裁剪图片
     */
    public void cutImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_TO_PHOTOCUTED);
    }
}
