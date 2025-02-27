package aa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class Request {
    public String op; // Operation (1 = Register, 5 = Login, 6 = Logout)
    public String name; // Name for the Register operation
    public String user; // User ID for Login/Register
    public String password; // Password for Login/Register
    public String token; // Token for Logout
    public String catName;
    public String description;
    public String catId;
    public String isAdmin;
    public List<Categories> categories;
    public String title;
    public String text;
    public String categoryId;
    public List<String> categoryIds;
    public String id; // Para operações de update/delete

    // Construtor completo que inicializa todos os campos
    public Request(String name, String op, String user, String password, String token, String isAdmin) {
        this.name = name;
        this.op = op;
        this.user = user;
        this.password = password;
        this.token = token;
        this.isAdmin = isAdmin;
    }

    public Request(String name, String op, String user, String password, String token) {
        this.name = name;
        this.op = op;
        this.user = user;
        this.password = password;
        this.token = token;
    }
    
    public Request(String op, String token, List<Categories> categories) {
        this.op = op;
        this.token = token;
        this.categories = categories;
    }
    
    public Request(String op, String token, String catName, String description) {
    	this.op = op;
    	this.token = token;
    	this.catName = catName;
    	this.description = description;
    }
    
    public Request(String op, String token, String title, String categoryId, List<String> categoryIds, String id, String teste){
    	this.op = op;
    	this.token = token;
    	this.title = title;
    	this.categoryId = categoryId;
    	this.categoryIds = categoryIds;
    	this.id = id;
    	this.text = teste;
    }
    public Request() {
    	
    }
    
    
   
    public Request(String string, String token2, String id2) {
		// TODO Auto-generated constructor stub
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String isAdmin() {
		return isAdmin;
	}

	public void setAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	// Método para serializar o objeto para JSON
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Método para desserializar JSON para um objeto Request
    public Request deserialize(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Request.class);
    }
}
