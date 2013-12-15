package splittr.startup.ui.adapter;

import java.text.DecimalFormat;
import java.util.List;

import splittr.startup.model.Person;
import splittr.startup.model.ReceiptItem;
import splittr.startup.ui.PersonView;
import splittr.startup.venmo.Venmo;
import abbyy.ocrsdk.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReceiptItemAdapter extends ArrayAdapter<ReceiptItem> {

	private Context context;
	private Person selectedPerson;

	public ReceiptItemAdapter(Context context, List<ReceiptItem> objects) {
		super(context, R.layout.bill_row, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ReceiptItem item = getItem(position);
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.bill_row, null);
		((TextView) convertView.findViewById(R.id.bill_row_price))
				.setText(Venmo.formatCents(item.priceInCents));
		((TextView) convertView.findViewById(R.id.bill_row_name))
				.setText(item.label);
		
		/*ViewGroup peopleLayout = (ViewGroup) convertView.findViewById(R.id.people_layout);
		peopleLayout.removeAllViews();
		for (final Person person : item.people) {
			PersonView personView = new PersonView(context, person, false, 42 * 3);
			peopleLayout.addView(personView);
		}*/

		return convertView;
	}

	public void setSelectedPerson(Person selectedPerson) {
		this.selectedPerson = selectedPerson;
	}

	public Person getSelectedPerson() {
		return selectedPerson;
	}
	
}
