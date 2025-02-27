package aa;

import java.util.List;

import com.google.gson.Gson;

public class Response {
	public String response;
	public String message;
	public String token;
	public String name;
	public String password;
	public String user;
	List<Categories> categories;
	private List<Announcement> announcements;
	//private Cadastro user;

	public Response(String i, String message, String token) {
		this.response = i;
		this.message = message;
		this.token = token;
	}

//	public Response(String i, Cadastro user, String token, String message) {
//		this.response = i;
//		this.message = message;
//		this.token = token;
//		this.user = user;
//	}
	

	public Response(String i, String message, List<Categories> cats, String token) {
		this.response = i;
		this.message = message;
		this.categories = cats;
		this.token = token;
	}
	public Response(String i, String user, String password, String name, String message) {
		this.response = i;
		this.user = user;
		this.password = password;
		this.name = name;
		this.message = message;
		
	}
	
	
	public Response(String response, String message, List<Announcement> announcements, Object object) {
		this.response = response;
		this.message = message;
		this.announcements = announcements;
		// TODO Auto-generated constructor stub
	}

	public String serialize() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static Response deserialize(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Response.class);
    }
}
