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
import splittr.startup.ui.adapter.PersonAdapter;
import splittr.startup.ui.adapter.ReceiptItemAdapter;
import splittr.startup.venmo.Venmo;
import splittr.startup.venmo.exceptions.UnderMinimumAmountException;
import splittr.startup.venmo.exceptions.VenmoException;
import abbyy.ocrsdk.android.AsyncProcessTask;
import abbyy.ocrsdk.android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
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
	private List<Person> allVenmoFriends = new ArrayList<Person>();
	private List<Person> selectedVenmoFriends = new ArrayList<Person>();
	private List<ReceiptItem> receiptItems = new ArrayList<ReceiptItem>();
	
	//Stuff used by the background tasks
	int tipAmount = 15;

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
				calculateTipAmount();
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
	
	private void calculateTipAmount() {
		String selectedTip = (String) tipOptions.getSelectedItem();
		if (selectedTip == null) {
			tipAmount = 15;
		}
		else {
			tipAmount = Integer.parseInt(selectedTip.replace("%", ""));
		}
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
		int size = 80 * 3;
		for (final Person person : selectedVenmoFriends) {
			PersonView personView = new PersonView(this, person,
					person == selectedPerson, size);
			personView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setSelectedPerson(person);
				}
			});
			peopleView.addView(personView);
		}
		ImageView addUserButton = new ImageView(this);
		addUserButton.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_plus));
		addUserButton.setPadding(20,  20,  20,  20);
		addUserButton.setLayoutParams(new LayoutParams(80 * 4, 80 * 4));
		
		addUserButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPersonSelector();
			}
		});
		peopleView.addView(addUserButton);
	}

	public void setSelectedPerson(Person person) {
		selectedPerson = person;
		itemAdapter.setSelectedPerson(person);
		updateView();
	}
	
	protected void showPersonSelector() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Who are you with?");

		final ListView personList = new ListView(this);
		personList.setFocusable(false);
		final PersonAdapter adapter = new PersonAdapter(this, allVenmoFriends);
		personList.setAdapter(adapter);

		personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		    	Person person = (Person) adapter.getItem(position);
		    	person.selected = !person.selected;
		    	adapter.notifyDataSetChanged();
		    }
		});

		builder.setView(personList);
		final Dialog dialog = builder.create();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				selectedVenmoFriends.clear();
				for (Person person : allVenmoFriends) {
					if (person.selected) {
						selectedVenmoFriends.add(person);
					}
				}
				updateView();
			}
		});
		
		dialog.show();
	}
	
    private class VenmoFriendsTask extends AsyncTask<String, Void, List<Person>> {
        @Override
        protected List<Person> doInBackground(String... userId) {
        	return Venmo.getFriends(userId[0]);
        }

        @Override
        protected void onPostExecute(List<Person> result) {
        	allVenmoFriends = result;
        	updateView();
        }
    }
    
    private class SubmitBillTask extends AsyncTask<String, Void, String> {
        boolean underMinimum = false;
        boolean error = false;
    	
    	@Override
        protected String doInBackground(String... userId) {
        	try {
        		Venmo.submitSplitBill(userId[0], receiptItems, tipAmount);
        	}
        	catch (UnderMinimumAmountException e) {
        		underMinimum = true;
        	}
        	catch (VenmoException e) {
        		error = true;
        	}
        	return null;
        }

        @Override
        protected void onPostExecute(String result) {
        	String message;
        	if (underMinimum) {
        		message = "Sorry, someone is under the minimum transaction amount of $1.00";
        	}
        	else if (error) {
        		message = "Sorry, there was an error submitting your request.";
        	}
        	else {
        		message = "Billed your friends!";
        	}
        	message = "Tipping: " + tipAmount;
        	Toast.makeText(BillSplitActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
}
