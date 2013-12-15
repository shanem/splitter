package abbyy.ocrsdk.android;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import splittr.startup.model.Person;
import splittr.startup.model.ReceiptItem;
import splittr.startup.ui.PersonView;
import splittr.startup.ui.adapter.ReceiptItemAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class ResultsActivity extends Activity {

	String outputPath;

	public static final String OCR_TEXT = "ocrText";

	private TextView ocrTextView;
	private ListView itemsListView;
	private ViewGroup peopleView;

	private ReceiptItemAdapter itemAdapter;

	private Person selectedPerson;
	private List<Person> people = new ArrayList<Person>();
	private List<ReceiptItem> receiptItems = new ArrayList<ReceiptItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bill_split_layout);

		String imageUrl = "unknown";

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			imageUrl = extras.getString("IMAGE_PATH");
			outputPath = extras.getString("RESULT_PATH");
		}

		// Starting recognition process
		new AsyncProcessTask(this).execute(imageUrl, outputPath);
		
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

	public void updateResults() {
		try {
			StringBuffer contents = new StringBuffer();

			FileInputStream fis = openFileInput(outputPath);
			Reader reader = new InputStreamReader(fis, "UTF-8");
			BufferedReader bufReader = new BufferedReader(reader);
			String text = null;
			while ((text = bufReader.readLine()) != null) {
				contents.append(text).append(
						System.getProperty("line.separator"));
			}

			displayMessage(contents.toString());
		} catch (Exception e) {
			displayMessage("Error: " + e.getMessage());
		}
	}

	private void displayMessage(String text) {
		ocrTextView.post(new MessagePoster(text));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_results, menu);
		return true;
	}

	class MessagePoster implements Runnable {
		public MessagePoster(String message) {
			_message = message;
		}

		public void run() {
			ocrTextView.append(_message + "\n");
			//setContentView(ocrTextView);
		}

		private final String _message;
	}
	
	protected void generatePlaceholderData() {
		receiptItems.clear();
		receiptItems.add(new ReceiptItem("Apple", 100));
		receiptItems.add(new ReceiptItem("Banana", 50));
		receiptItems.add(new ReceiptItem("Pear", 80));
		
		people.clear();
		people.add(new Person("Shane", ""));
		people.get(0).imageResource = R.drawable.placeholder_avatar;
		people.add(new Person("Mark", ""));
		people.get(1).imageResource = R.drawable.placeholder_avatar_1;
		people.add(new Person("Bob", ""));
		people.get(2).imageResource = R.drawable.placeholder_avatar_2;
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