
package splittr.startup.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import splittr.startup.model.Person;
import splittr.startup.ui.CircularImageView;
import abbyy.ocrsdk.android.R;
import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class DraggableFriendsAdapter extends ArrayAdapter<Person> {

    private Context context;
    private ArrayList<Person> people;

    public DraggableFriendsAdapter(Context context, List<Person> people) {
        super(context, R.layout.draggable_friend_item, people);
        this.context = context;
        this.people = (ArrayList<Person>) people;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Person person = people.get(position);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.draggable_friend_item, null);
        
        CircularImageView thumbnail_bk = (CircularImageView) convertView
                .findViewById(R.id.friend_thumb_back);
        thumbnail_bk.setBorderWidth(0);

        CircularImageView thumbnail = (CircularImageView) convertView
                .findViewById(R.id.friend_thumb);
        thumbnail.setBorderWidth(0);
        thumbnail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // set selected

            }
        });

        thumbnail.setOnTouchListener(new MyTouchListener());

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.displayImage(person.imageUrl, thumbnail);
        imageLoader.displayImage(person.imageUrl, thumbnail_bk);

        return convertView;
    }

    private final class MyTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }

    }

}
