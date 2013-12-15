package splittr.startup.ui;

import com.nostra13.universalimageloader.core.ImageLoader;

import splittr.startup.model.Person;
import splittr.startup.ui.activity.BillSplitActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import edu.sfsu.cs.orange.ocr.R;

public class PersonView extends RelativeLayout {
	private ImageView avatar;
	private View selectionView;
	
	private final Person person;
	private final Context context;
	
	public PersonView(final Context context, final Person person, boolean selected) {
        super(context);
        this.context = context;
        this.person = person;
    	setSelected(selected);
        
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.person, this, true);
		
		avatar = (ImageView) findViewById(R.id.avatar);
		ImageLoader.getInstance().displayImage(person.imageUrl, avatar);
		selectionView = findViewById(R.id.selection);
		
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((BillSplitActivity) context).setSelectedPerson(person);
			}
		});
		selectionView.setVisibility(isSelected() ? View.VISIBLE : View.GONE);
    }
}
