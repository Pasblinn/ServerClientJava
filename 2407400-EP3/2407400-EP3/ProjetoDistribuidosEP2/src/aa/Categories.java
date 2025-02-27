package aa;

import com.google.gson.Gson;

public class Categories {
public String name;
public String description;
public String id;
public String subscribed;


public Categories(String catName, String description, String id) {
	super();
	this.name = catName;
	this.description = description;
	this.id = id;
	this.subscribed = "false";
}


public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getSubscribed() {
	return subscribed;
}

public void setSubscribed(String subscribed) {
	this.subscribed = subscribed;
}

public Categories(String catName, String description) {
	this.name = catName;
	this.description = description;
}


public String getId() {
	return id;
}


public void setId(String id) {
	this.id = id;
}


public String serialize() {
    Gson gson = new Gson();
    return gson.toJson(this);
}


public String getCatName() {
	return name;
}


public void setCatName(String catName) {
	this.name = catName;
}


public String getDescription() {
	return description;
}


public void setDescription(String description) {
	this.description = description;
}

}
