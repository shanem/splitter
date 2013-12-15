package splittr.startup.ui.activity;

import java.util.ArrayList;
import java.util.List;

import splittr.startup.model.Person;
import splittr.startup.model.ReceiptItem;
import splittr.startup.ui.PersonView;
import splittr.startup.ui.adapter.ReceiptItemAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import edu.sfsu.cs.orange.ocr.R;

public class BillSplitActivity extends Activity {
	public static final String OCR_TEXT = "ocrText"; 
	
	private TextView ocrTextView;
	private ListView itemsListView;
	private ViewGroup peopleView;
	
	private ReceiptItemAdapter itemAdapter;
	
	private Person selectedPerson;
	private List<Person> people = new ArrayList<Person>();
	private List<ReceiptItem> receiptItems = new ArrayList<ReceiptItem>();
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.bill_split_layout);
		
		ocrTextView = (TextView) findViewById(R.id.ocr_text_view);
		ocrTextView.setText(getIntent().getExtras().getString(OCR_TEXT));

		peopleView = (ViewGroup) findViewById(R.id.people_view);
			
		generatePlaceholderData();
		
		itemAdapter = new ReceiptItemAdapter(this, receiptItems);
		itemAdapter.notifyDataSetChanged();
		itemsListView = (ListView) findViewById(R.id.items_list);
		itemsListView.setAdapter(itemAdapter);
		itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
				ReceiptItem receiptItem = itemAdapter.getItem(position);
				if (selectedPerson != null && !receiptItem.people.contains(selectedPerson)) {
					receiptItem.people.add(selectedPerson);
				}
				itemAdapter.notifyDataSetChanged();
			}
		});
		
		updateView();
	}
	
	protected void generatePlaceholderData() {
		receiptItems.clear();
		receiptItems.add(new ReceiptItem("Apple", 100));
		receiptItems.add(new ReceiptItem("Banana", 50));
		receiptItems.add(new ReceiptItem("Pear", 80));
		
		people.clear();
		people.add(new Person("Shane", "http://i.stack.imgur.com/UywPC.jpg?s=64&g=1"));
		people.add(new Person("Mark", "https://www.gravatar.com/avatar/bbd257637393bcbc390db713d3ad31bb?s=128&d=identicon&r=PG"));
		people.add(new Person("Bob", "https://www.gravatar.com/avatar/2bb8f84e5afc6318fd03de5bf28d1edc?s=128&d=identicon&r=PG"));
	}
	
	protected void updateView() {
		peopleView.removeAllViews();
		for (Person person : people) {
			PersonView personView = new PersonView(this, person, person == selectedPerson);
			peopleView.addView(personView);
		}
	}
	
	public void setSelectedPerson(Person person) {
		selectedPerson = person;
		itemAdapter.setSelectedPerson(person);
		updateView();
	}
}
