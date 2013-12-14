package splittr.startup.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import edu.sfsu.cs.orange.ocr.R;

public class PersonView extends RelativeLayout {
	private ImageView avatar;
	
	public PersonView(Context context) {
        super(context);
        
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.person, this, true);
		
		avatar = (ImageView) findViewById(R.id.avatar);
		avatar.setImageResource(R.drawable.placeholder_avatar);
    }
}
