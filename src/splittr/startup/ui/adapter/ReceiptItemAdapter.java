package splittr.startup.ui.adapter;

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
	Context context;
	Person selectedPerson;
	
	public ReceiptItemAdapter(Context context, List<ReceiptItem> items) {
		super(context, R.layout.receipt_item, items);
		this.context = context;
	}
	
	public void setSelectedPerson(Person selectedPerson) {
		this.selectedPerson = selectedPerson;
	}
	
	public Person getSelectedPerson() {
		return selectedPerson;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		final ReceiptItem item = getItem(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.receipt_item, null);
		}
		((TextView) convertView.findViewById(R.id.item_label)).setText(item.label);
		((TextView) convertView.findViewById(R.id.item_price)).setText(formatPrice(item.priceInCents));
		
		ViewGroup peopleLayout = (ViewGroup) convertView.findViewById(R.id.people_layout);
		peopleLayout.removeAllViews();
		for (final Person person : item.people) {
			PersonView personView = new PersonView(context, person, false, 42 * 3);
			peopleLayout.addView(personView);
		}
		
		return convertView;
	}
	
	private String formatPrice(int priceInCents) {
		return "$" + Venmo.formatCents(priceInCents);
	}
}
