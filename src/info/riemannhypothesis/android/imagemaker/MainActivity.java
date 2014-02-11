package info.riemannhypothesis.android.imagemaker;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private ImageView mImageView;
	private View mMainView;
	private Paint mPaint;

	private int width, height;
	private ImageButton mButtonNew;
	private Random mRandom;
	private ImageButton mButtonShare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRandom = new Random();
		init();
	}

	private void init() {
		setContentView(R.layout.activity_main);
		mMainView = findViewById(R.id.relativeLayout);
		mButtonNew = (ImageButton) findViewById(R.id.buttonNew);
		mButtonNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Animation anim = AnimationUtils.makeOutAnimation(
						MainActivity.this, true);
				mMainView.startAnimation(anim);
				mMainView.setVisibility(View.GONE);
				init();
			}
		});
		mButtonShare = (ImageButton) findViewById(R.id.buttonShare);
		mButtonShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAndShare();
			}
		});
		mImageView = (ImageView) findViewById(R.id.imageView);
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = width;

		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawARGB(255, mRandom.nextInt(256), mRandom.nextInt(256),
				mRandom.nextInt(256));

		mPaint = new Paint();
		mPaint.setStrokeWidth(16);

		mImageView.setImageBitmap(mBitmap);
		mImageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				mPaint.setARGB(mRandom.nextInt(256), mRandom.nextInt(256),
						mRandom.nextInt(256), mRandom.nextInt(256));
				mCanvas.drawCircle(x, y, 10, mPaint);
				mImageView.setImageBitmap(mBitmap);
				return true;
			}
		});

		Animation anim = AnimationUtils
				.makeInAnimation(MainActivity.this, true);
		mMainView.startAnimation(anim);
		mMainView.setVisibility(View.VISIBLE);
	}

	public void saveAndShare() {
		if (mBitmap == null) {
			return;
		}
		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		path.mkdirs();

		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());

		String filename = "ImageMaker_" + timestamp + ".jpg";

		File file = new File(path, filename);
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(file);
			mBitmap.compress(CompressFormat.JPEG, 75, stream);
			stream.close();
		} catch (Exception e) {
			return;
		}

		Uri uri = Uri.fromFile(file);

		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent.setData(uri);
		sendBroadcast(intent);

		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/jpeg");
		share.putExtra(Intent.EXTRA_STREAM, uri);
		startActivity(Intent.createChooser(share, "Share using..."));
	}
}
