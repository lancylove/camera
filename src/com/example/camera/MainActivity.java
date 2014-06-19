package com.example.camera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener {
	private ImageButton img_btn;
	private Button btn;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	// 创建一个以当前时间为名称的文件
	File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	// 初始化控件
	private void init() {
		img_btn = (ImageButton) findViewById(R.id.imageView1);
		btn = (Button) findViewById(R.id.button1);
		// 为ImageButton和Button添加监听事件
		img_btn.setOnClickListener(this);
		btn.setOnClickListener(this);
	}

	// 点击事件
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageView1:
			showDialog();
			break;

		case R.id.button1:
			Log.i("sss", ((EditText)findViewById(R.id.edt)).getText().toString());
			
			showDialog();
			break;
		}

	}

	// 提示对话框方法
	private void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle("头像设置")
				.setPositiveButton("拍照", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
//						Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
//						//指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
//						openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//						
						
						// 调用系统的拍照功能
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// 指定调用相机拍照后照片的储存路径
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(tempFile));
						startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
					}
				})
				.setNegativeButton("相册", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:// 当选择拍照时调用
			Log.i("拍照后", "tempfile--"+tempFile);
			startPhotoZoom(Uri.fromFile(tempFile), 200,130);
			break;

		case PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
			// 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
			if (data != null)
				startPhotoZoom(data.getData(), 200,130);
			break;

		case PHOTO_REQUEST_CUT:// 返回的结果
			if (data != null)
				setPicToView(data);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void startPhotoZoom(Uri uri, int size,int x) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
//		intent.putExtra("aspectX", 0);
//		intent.putExtra("aspectY", 0);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", x);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	// 将进行剪裁后的图片显示到UI界面上
	@SuppressWarnings("deprecation")
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			img_btn.setBackgroundDrawable(drawable);
		}
	}

	// 使用系统当前日期加以调整作为照片的名称
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
}