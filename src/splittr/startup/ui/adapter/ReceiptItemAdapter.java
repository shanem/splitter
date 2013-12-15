
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
        super(context, R.layout.bill_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReceiptItem item = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.bill_item, null);

        TextView price = ((TextView) convertView.findViewById(R.id.bill_item_price));
        price.setText(Venmo.formatCents(item.priceInCents));
        price.setOnDragListener(new MyDragListener());

        TextView name = ((TextView) convertView.findViewById(R.id.bill_item_name));
        name.setText(item.label);
        name.setOnDragListener(new MyDragListener());

        if (convertView.getTag() != null && convertView.getTag().equals("selected")) {
            price.setTextColor(0xff0083C8);
            name.setTextColor(0xff0083C8);
        }

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
            ViewGroup parent;
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    parent = ((ViewGroup) v.getParent());
                    if (parent.getTag() == null || !parent.getTag().equals("selected")) {
                        ((TextView) parent.findViewById(R.id.bill_item_price)).setTextColor(0xff0083C8);
                        ((TextView) parent.findViewById(R.id.bill_item_name)).setTextColor(0xff0083C8);
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    parent = ((ViewGroup) v.getParent());
                    if (parent.getTag() == null || !parent.getTag().equals("selected")) {
                        ((TextView) parent.findViewById(R.id.bill_item_price)).setTextColor(Color.GRAY);
                        ((TextView) parent.findViewById(R.id.bill_item_name)).setTextColor(Color.BLACK);
                    }
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    ListView owner = (ListView) view.getParent().getParent();
                    ((DraggableFriendsAdapter) ((HeaderViewListAdapter) owner.getAdapter()).getWrappedAdapter())
                            .notifyDataSetChanged();
                    parent = ((ViewGroup) v.getParent());
                    parent.setTag("selected");
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
