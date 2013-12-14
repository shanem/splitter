package splittr.startup.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import edu.sfsu.cs.orange.ocr.R;

public class BillSplitActivity extends Activity {
	public static final String OCR_TEXT = "ocrText"; 
	
	private TextView ocrTextView;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.bill_split_layout);
		
		ocrTextView = (TextView) findViewById(R.id.ocr_text_view);
		ocrTextView.setText(getIntent().getExtras().getString(OCR_TEXT));
	}
}
