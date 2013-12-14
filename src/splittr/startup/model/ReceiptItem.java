package splittr.startup.model;

public class ReceiptItem {
	public String label;
	public int priceInCents;
	
	public ReceiptItem(String label, int priceInCents) {
		this.label = label;
		this.priceInCents = priceInCents;
	}
}
