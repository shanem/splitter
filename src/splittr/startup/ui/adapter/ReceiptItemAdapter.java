package splittr.startup.ui.adapter;

import edu.sfsu.cs.orange.ocr.R;
import splittr.startup.ui.model.ReceiptItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReceiptItemAdapter extends ArrayAdapter<ReceiptItem> {

	public ReceiptItemAdapter(Context context) {
		super(context, R.layout.receipt_item);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.receipt_item, null);
		}
		((TextView) convertView.findViewById(R.id.item_label)).setText(getItem(position).label);
		((TextView) convertView.findViewById(R.id.item_price)).setText(formatPrice(getItem(position).priceInCents));
		
		return convertView;
	}
	
	private String formatPrice(int priceInCents) {
		return "$" + (priceInCents / 100) + "." + (priceInCents % 100);
	}
}
