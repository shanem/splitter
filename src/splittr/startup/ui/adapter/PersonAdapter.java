package splittr.startup.ui.adapter;

import java.util.List;

import splittr.startup.model.Person;
import splittr.startup.model.ReceiptItem;
import splittr.startup.ui.PersonView;
import abbyy.ocrsdk.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class PersonAdapter extends ArrayAdapter<Person> {
	Context context;
	
	public PersonAdapter(Context context, List<Person> items) {
		super(context, R.layout.person_item, items);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		final Person person = getItem(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.person_item, null);
		}
		((TextView) convertView.findViewById(R.id.name)).setText(person.name);
		((CheckBox) convertView.findViewById(R.id.checkmark)).setChecked(person.selected);
		
		return convertView;
	}
}
