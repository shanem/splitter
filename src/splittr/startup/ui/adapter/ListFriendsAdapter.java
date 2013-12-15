
package splittr.startup.ui.adapter;

import java.util.List;

import splittr.startup.model.Person;
import splittr.startup.ui.CircularImageView;
import abbyy.ocrsdk.android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ListFriendsAdapter extends ArrayAdapter<Person> {
    Context context;

    public ListFriendsAdapter(Context context, List<Person> items) {
        super(context, R.layout.list_friend_item, items);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Person person = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_friend_item, null);
        ((TextView) convertView.findViewById(R.id.name)).setText(person.name);
        final CircularImageView thumbnail = ((CircularImageView) convertView.findViewById(R.id.person_image));
        thumbnail.setBorderColor(0x00A3D8);
        if (!person.selected)
            thumbnail.setAlpha(.2f);
        else
            thumbnail.setAlpha(1f);
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thumbnail.getAlpha() < 1f)
                    thumbnail.setAlpha(1f);
                else
                    thumbnail.setAlpha(.2f);
                person.selected = !person.selected;
            }
        });

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.displayImage(person.imageUrl, thumbnail);

        return convertView;
    }
}
