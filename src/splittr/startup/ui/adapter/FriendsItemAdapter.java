package splittr.startup.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import splittr.startup.model.Person;
import splittr.startup.ui.CircularImageView;
import abbyy.ocrsdk.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FriendsItemAdapter extends ArrayAdapter<Person> {

	private Context context;
	private ArrayList<Person> people;

	public FriendsItemAdapter(Context context, List<Person> people) {
		super(context, R.layout.friend_row, people);
		this.context = context;
		this.people = (ArrayList<Person>) people;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Person person = people.get(position);
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.friend_row, null);

		CircularImageView thumbnail = (CircularImageView) convertView
				.findViewById(R.id.friend_thumb);
		thumbnail.setBorderWidth(0);
		thumbnail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// set selected

			}
		});

		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		imageLoader.displayImage(person.imageUrl, thumbnail);

		return convertView;
	}

}
