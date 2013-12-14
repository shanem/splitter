package splittr.startup.ui.activity;

import splittr.startup.model.ReceiptItem;
import splittr.startup.ui.PersonView;
import splittr.startup.ui.adapter.ReceiptItemAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.sfsu.cs.orange.ocr.R;

public class BillSplitActivity extends Activity {
	public static final String OCR_TEXT = "ocrText"; 
	
	private TextView ocrTextView;
	private ListView itemsListView;
	private ViewGroup peopleView;
	
	private ReceiptItemAdapter itemAdapter;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.bill_split_layout);
		
		ocrTextView = (TextView) findViewById(R.id.ocr_text_view);
		ocrTextView.setText(getIntent().getExtras().getString(OCR_TEXT));

		peopleView = (ViewGroup) findViewById(R.id.people_view);
		
		itemAdapter = new ReceiptItemAdapter(this);
		itemsListView = (ListView) findViewById(R.id.items_list);
		itemsListView.setAdapter(itemAdapter);
		
		itemAdapter.add(new ReceiptItem("Apple", 100));
		itemAdapter.add(new ReceiptItem("Banana", 50));
		itemAdapter.add(new ReceiptItem("Pear", 80));
		
		updateView();
	}
	
	protected void updateView() {
		peopleView.removeAllViews();
		for (int i = 0; i < 3; i++) {
			PersonView personView = new PersonView(this);
			peopleView.addView(personView);
		}
	}
}
