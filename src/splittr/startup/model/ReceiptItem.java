package splittr.startup.model;

import java.util.ArrayList;
import java.util.List;

public class ReceiptItem {
	public String label;
	public int priceInCents;
	public List<Person> people;
	
	public ReceiptItem(String label, int priceInCents) {
		this.label = label;
		this.priceInCents = priceInCents;
		people = new ArrayList<Person>();
	}
}
