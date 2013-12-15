package splittr.startup.model;

public class Person {
	public String name;
	public String imageUrl;
	public Long venmoId;
	public int owesCents = 0;
	
	public Person(String name, String imageUrl, Long venmoId) {
		this.name = name;
		this.imageUrl = imageUrl;
		this.venmoId = venmoId;
	}
}
