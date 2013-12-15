package splittr.startup.ui.activity;

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
import splittr.startup.venmo.Venmo;
import abbyy.ocrsdk.android.AsyncProcessTask;
import abbyy.ocrsdk.android.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BillSplitActivity extends Activity {

	String outputPath;

	public static final String OCR_TEXT = "ocrText";

	private TextView ocrTextView;
	private ListView itemsListView;
	private ViewGroup peopleView;
	private Spinner tipOptions;
	private Button submitButton;

	private ReceiptItemAdapter itemAdapter;

	private Person selectedPerson;
	private List<Person> people = new ArrayList<Person>();
	private List<ReceiptItem> receiptItems = new ArrayList<ReceiptItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bill_split_layout);
		generatePlaceholderData();
		
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
		
		tipOptions = (Spinner) findViewById(R.id.tip_selector);
		tipOptions.setSelection(5);
		
		submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] params = new String[1];
				params[0] = "790795";
				SubmitBillTask billTask = new SubmitBillTask();
				billTask.execute(params);
			}
		});

		itemAdapter = new ReceiptItemAdapter(this, receiptItems);
		itemAdapter.notifyDataSetChanged();
		itemsListView = (ListView) findViewById(R.id.items_list);
		itemsListView.setAdapter(itemAdapter);
		itemsListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View item,
							int position, long id) {
						ReceiptItem receiptItem = itemAdapter.getItem(position);
						if (selectedPerson != null) {
							if (receiptItem.people.contains(selectedPerson)) {
								receiptItem.people.remove(selectedPerson);
							} else {
								receiptItem.people.add(selectedPerson);
							}
						}
						itemAdapter.notifyDataSetChanged();
					}
				});
		
		String[] params = new String[1];
		params[0] = "790795";
		VenmoFriendsTask friendsTask = new VenmoFriendsTask();
		friendsTask.execute(params);
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
			// setContentView(ocrTextView);
		}

		private final String _message;
	}

	protected void generatePlaceholderData() {
		receiptItems.clear();
		receiptItems.add(new ReceiptItem("Apple", 100));
		receiptItems.add(new ReceiptItem("Banana", 50));
		receiptItems.add(new ReceiptItem("Pear", 80));
	}

	protected void updateView() {
		peopleView.removeAllViews();
		for (final Person person : people) {
			PersonView personView = new PersonView(this, person,
					person == selectedPerson, 80 * 3);
			personView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setSelectedPerson(person);
				}
			});
			peopleView.addView(personView);
		}
	}

	public void setSelectedPerson(Person person) {
		selectedPerson = person;
		itemAdapter.setSelectedPerson(person);
		updateView();
	}
	
    private class VenmoFriendsTask extends AsyncTask<String, Void, List<Person>> {
        @Override
        protected List<Person> doInBackground(String... userId) {
        	return Venmo.getFriends(userId[0]);
        }

        @Override
        protected void onPostExecute(List<Person> result) {
        	people = result;
        	updateView();
        }
    }
    
    private class SubmitBillTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... userId) {
        	try {
        		return Venmo.submitSplitBill(userId[0], receiptItems, 15);
        	}
        	catch (Exception e) {
        		Log.d("Exception", e.toString());
        	}
        	return null;
        }

        @Override
        protected void onPostExecute(String result) {
        	if (result != null) {
        		Toast.makeText(BillSplitActivity.this, "Billed your friends!", Toast.LENGTH_LONG).show();
        	}
        	else {
        		Toast.makeText(BillSplitActivity.this, "Sorry, there was an error submitting your request.", Toast.LENGTH_LONG).show();
        	}
        }
    }
}
