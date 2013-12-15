package splittr.startup.ui;

import splittr.startup.model.Person;
import abbyy.ocrsdk.android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonView extends RelativeLayout {
	private ImageView avatar;
	private View selectionView;
	
	private final Person person;
	private final Context context;
	
	public PersonView(final Context context, final Person person, boolean selected, int size) {
        super(context);
        this.context = context;
        this.person = person;
    	setSelected(selected);
    	setLayoutParams(new LayoutParams(size, size));
        
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.person, this, true);
		
		avatar = (ImageView) findViewById(R.id.avatar);
		ImageLoader.getInstance().displayImage(person.imageUrl, avatar);
		selectionView = findViewById(R.id.selection);
		
		selectionView.setVisibility(isSelected() ? View.VISIBLE : View.GONE);
    }
}
