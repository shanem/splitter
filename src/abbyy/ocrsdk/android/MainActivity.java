package abbyy.ocrsdk.android;

import java.io.File;

import splittr.startup.ui.activity.BillSplitActivity;
import splittr.startup.venmo.Venmo;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.slidinglayer.SlidingLayer;

public class MainActivity extends Activity {

	private final int TAKE_PICTURE = 0;
	private final int SELECT_FILE = 1;

	private String resultUrl = "result.txt";

	private RelativeLayout mainLayout;
	private View overlay;
	private SlidingLayer slidingLayer;
	private Button debugButton;
	private Button loginButton;
	private Button fromCameraButton;
	private Button venmoLoginButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
		slidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer);
		debugButton = (Button) findViewById(R.id.debugButton);
		loginButton = (Button) findViewById(R.id.loginButton);
		fromCameraButton = (Button) findViewById(R.id.fromCameraButton);
		venmoLoginButton = (Button) findViewById(R.id.slidingLoginButton);
		overlay = (View) findViewById(R.id.overlay);

		slidingLayer.setVisibility(View.VISIBLE);
		
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri oAuthUri = Uri.parse("https://api.venmo.com/oauth/authorize?client_id=" + Venmo.CLIENT_ID
						+ "&scope=make_payments,access_friends,access_profile&response_type=token&"
						+ "redirect_uri=http%3A%2F%2Fec2-50-16-145-146.compute-1.amazonaws.com%2Ftesterex.php");
				Intent browserIntent = new Intent("android.intent.action.VIEW", oAuthUri);
				startActivity(browserIntent);
			}
		});

		debugButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						BillSplitActivity.class);
				intent.putExtra(BillSplitActivity.OCR_TEXT, "");
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void captureImageFromSdCard(View view) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");

		startActivityForResult(intent, SELECT_FILE);
	}

	public static final int MEDIA_TYPE_IMAGE = 1;

	private static Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"ABBYY Cloud OCR SDK Demo App");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// Create a media file name
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "image.jpg");

		return mediaFile;
	}

	public void captureImageFromCamera(View view) {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		Uri fileUri = getOutputMediaFileUri(); // create a file to save the
												// image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
															// name

		startActivityForResult(intent, TAKE_PICTURE);
	}
	
	private void updateView() {
		loginButton.setVisibility(Venmo.getAccessToken() == null ? View.VISIBLE : View.INVISIBLE);
		debugButton.setVisibility(Venmo.getAccessToken() == null ? View.INVISIBLE : View.VISIBLE);
		fromCameraButton.setVisibility(Venmo.getAccessToken() == null ? View.INVISIBLE : View.VISIBLE);
	}

	public void openBillView(View view) {

	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if (intent.getData() != null && intent.getData().getQuery() != null) {
			String query = intent.getData().getQuery();
			if (query.contains("access_token=")) {
				Venmo.setAccessToken(query.replace("access_token=", ""));
			}
		}
		
		updateView();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;

		String imageFilePath = null;

		switch (requestCode) {
		case TAKE_PICTURE:
			imageFilePath = getOutputMediaFileUri().getPath();
			break;
		case SELECT_FILE: {
			Uri imageUri = data.getData();

			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cur = managedQuery(imageUri, projection, null, null, null);
			cur.moveToFirst();
			imageFilePath = cur.getString(cur
					.getColumnIndex(MediaStore.Images.Media.DATA));
		}
			break;
		}

		// Remove output file
		deleteFile(resultUrl);

		Intent results = new Intent(this, BillSplitActivity.class);
		results.putExtra("IMAGE_PATH", imageFilePath);
		results.putExtra("RESULT_PATH", resultUrl);
		startActivity(results);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (slidingLayer.isOpened()) {
				slidingLayer.closeLayer(true);
				overlay.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
