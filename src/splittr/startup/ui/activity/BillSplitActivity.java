package splittr.startup.ui.activity;

import splittr.startup.ui.adapter.ReceiptItemAdapter;
import splittr.startup.ui.model.ReceiptItem;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import edu.sfsu.cs.orange.ocr.R;

public class BillSplitActivity extends Activity {
	public static final String OCR_TEXT = "ocrText"; 
	
	private TextView ocrTextView;
	private ListView itemsListView;
	
	private ReceiptItemAdapter itemAdapter;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.bill_split_layout);
		
		ocrTextView = (TextView) findViewById(R.id.ocr_text_view);
		ocrTextView.setText(getIntent().getExtras().getString(OCR_TEXT));
		
		itemAdapter = new ReceiptItemAdapter(this);
		
		itemsListView = (ListView) findViewById(R.id.items_list);
		itemsListView.setAdapter(itemAdapter);
		
		itemAdapter.add(new ReceiptItem("Apple", 100));
		itemAdapter.add(new ReceiptItem("Banana", 50));
		itemAdapter.add(new ReceiptItem("Pear", 80));
	}
}
