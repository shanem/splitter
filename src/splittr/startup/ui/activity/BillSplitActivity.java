package splittr.startup.ui.activity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import splittr.startup.model.Person;
import splittr.startup.model.ReceiptItem;
import splittr.startup.ui.CircularImageView;
import splittr.startup.ui.adapter.FriendsItemAdapter;
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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
	private Spinner tipOptions;
	private Button submitButton;
	private ListView peopleView;

	private ReceiptItemAdapter itemAdapter;
	private FriendsItemAdapter peopleAdapter;

	private Person selectedPerson;
	private List<Person> allVenmoFriends = new ArrayList<Person>();
	private List<Person> selectedVenmoFriends = new ArrayList<Person>();
	private List<ReceiptItem> receiptItems = new ArrayList<ReceiptItem>();

	// Stuff used by the background tasks
	int tipAmount = 15;

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

		peopleView = (ListView) findViewById(R.id.friends_list);

		tipOptions = (Spinner) findViewById(R.id.tip_selector);
		tipOptions.setSelection(0);

		submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				calculateTipAmount();
				String[] params = new String[1];
				params[0] = null;
				SubmitBillTask billTask = new SubmitBillTask();
				billTask.execute(params);
			}
		});
		peopleView = (ListView) findViewById(R.id.friends_list);

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
		params[0] = null;
		VenmoFriendsTask friendsTask = new VenmoFriendsTask();
		friendsTask.execute(params);

		peopleAdapter = new FriendsItemAdapter(getApplicationContext(),
				selectedVenmoFriends);
		peopleView.setAdapter(peopleAdapter);

		View addUserButton = ((LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.add_person, null);
		addUserButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPersonSelector();
			}
		});
		CircularImageView icon = (CircularImageView) addUserButton
				.findViewById(R.id.add_icon);
		icon.setBorderColor(Color.TRANSPARENT);
		peopleView.addFooterView(addUserButton);
	}

	private void calculateTipAmount() {
		String selectedTip = (String) tipOptions.getSelectedItem();
		if (selectedTip == null) {
			tipAmount = 15;
		} else {
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
			Log.e("BillSplitActivity", "");
			Log.e("BillSplitActivity", "ERROR: " + e.getMessage());
			Log.e("BillSplitActivity", "");
			// displayMessage("Error: " + e.getMessage());
		}
	}

	private void displayMessage(String text) {
		// TODO: Populate listview with OCR results
		// ocrTextView.post(new MessagePoster(text));
		generatePlaceholderData();
		itemAdapter.notifyDataSetChanged();
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
		receiptItems.add(new ReceiptItem("BB Crab&Shrimp", 745));
		receiptItems.add(new ReceiptItem("Soda", 231));
	}

	public void setSelectedPerson(Person person) {
		selectedPerson = person;
		itemAdapter.setSelectedPerson(person);
		peopleAdapter.notifyDataSetChanged();
	}

	private class VenmoFriendsTask extends
			AsyncTask<String, Void, List<Person>> {
		@Override
		protected List<Person> doInBackground(String... userId) {
			String myId = Venmo.getMyId();
			return Venmo.getFriends(myId);
		}

		@Override
		protected void onPostExecute(List<Person> result) {
			allVenmoFriends = result;
			peopleAdapter.notifyDataSetChanged();
		}
	}

	protected void showPersonSelector() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Who are you with?");
		
		final ViewGroup personDialogView = (ViewGroup) ((LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.friends_dialog, null);

		final ListView personList = (ListView) personDialogView
				.findViewById(R.id.friends_dialog_listview);
		personList.setFocusable(false);
		final PersonAdapter adapter = new PersonAdapter(this, allVenmoFriends);
		personList.setAdapter(adapter);
		final Button addFriends = (Button) personDialogView
				.findViewById(R.id.add_friends_button);
		addFriends.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedVenmoFriends.clear();
				for (Person person : allVenmoFriends) {
					if (person.selected) {
						selectedVenmoFriends.add(person);
					}
				}
				peopleAdapter.notifyDataSetChanged();
			}
		});
		builder.setView(personDialogView);
		Dialog dialog = builder.create();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				selectedVenmoFriends.clear();
				for (Person person : allVenmoFriends) {
					if (person.selected) {
						selectedVenmoFriends.add(person);
					}
				}
				peopleAdapter.notifyDataSetChanged();
			}
		});

		dialog.show();
	}

	private class SubmitBillTask extends AsyncTask<String, Void, String> {
		boolean underMinimum = false;
		boolean error = false;

		@Override
		protected String doInBackground(String... userId) {
			try {
				Venmo.submitSplitBill(userId[0], receiptItems, tipAmount);
			} catch (UnderMinimumAmountException e) {
				underMinimum = true;
			} catch (VenmoException e) {
				error = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			String message;
			if (underMinimum) {
				message = "Sorry, someone is under the minimum transaction amount of $1.00";
			} else if (error) {
				message = "Sorry, there was an error submitting your request.";
			} else {
				message = "Billed your friends!";
			}
			Toast.makeText(BillSplitActivity.this, message, Toast.LENGTH_LONG)
					.show();
		}
	}
}
