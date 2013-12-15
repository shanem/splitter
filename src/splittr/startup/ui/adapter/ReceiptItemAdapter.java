package splittr.startup.ui.adapter;

import java.util.List;

import splittr.startup.model.Person;
import splittr.startup.model.ReceiptItem;
import splittr.startup.venmo.Venmo;
import abbyy.ocrsdk.android.R;
import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
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
		TextView convert = ((TextView) convertView
				.findViewById(R.id.bill_row_price));
		convert.setText(Venmo.formatCents(item.priceInCents));
		convert.setOnDragListener(new MyDragListener());
		TextView row = ((TextView) convertView.findViewById(R.id.bill_row_name));
		row.setText(item.label);
		row.setOnDragListener(new MyDragListener());

		/*
		 * ViewGroup peopleLayout = (ViewGroup)
		 * convertView.findViewById(R.id.people_layout);
		 * peopleLayout.removeAllViews(); for (final Person person :
		 * item.people) { PersonView personView = new PersonView(context,
		 * person, false, 42 * 3); peopleLayout.addView(personView); }
		 */

		// convertView.setOnDragListener(new MyDragListener());

		return convertView;
	}

	public void setSelectedPerson(Person selectedPerson) {
		this.selectedPerson = selectedPerson;
	}

	public Person getSelectedPerson() {
		return selectedPerson;
	}

	class MyDragListener implements OnDragListener {

		@Override
		public boolean onDrag(View v, DragEvent event) {
			int action = event.getAction();
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				// do nothing
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				((TextView)((ViewGroup) v.getParent()).findViewById(R.id.bill_row_price))
						.setTextColor(Color.GREEN);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				((TextView)((ViewGroup) v.getParent()).findViewById(R.id.bill_row_price))
				.setTextColor(Color.GRAY);
				break;
			case DragEvent.ACTION_DROP:
				// Dropped, reassign View to ViewGroup
				View view = (View) event.getLocalState();
				ListView owner = (ListView) view.getParent().getParent();
				((FriendsItemAdapter) ((HeaderViewListAdapter) owner
						.getAdapter()).getWrappedAdapter())
						.notifyDataSetChanged();
				/*
				 * LinearLayout container = (LinearLayout) v;
				 * container.addView(view); view.setVisibility(View.VISIBLE);
				 */
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				// v.setBackgroundColor(Color.GREEN);
			default:
				break;
			}
			return true;
		}
	}

}
