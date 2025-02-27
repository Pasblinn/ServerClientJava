package aa;

public class Announcement {
    public String id;
    public String title;
    public String text;
    public String date;
    public String categoryId;
    public String userId;
    
	public Announcement(String id, String title, String text, String date, String categoryId, String userId) {
		super();
		this.id = id;
		this.title = title;
		this.text = text;
		this.date = date;
		this.categoryId = categoryId;
		this.userId = userId;
	}
	
	public Announcement(String title, String text, String categoryId, String userId) {
		super();
		this.title = title;
		this.text = text;
		this.categoryId = categoryId;
		this.userId = userId;
	}

	public Announcement() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
    
    // Construtor e getters/setters
    
    
}