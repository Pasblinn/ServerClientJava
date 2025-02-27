package aa;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class EchoClient2 {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);

		System.out.print("Enter the server IP: ");
		String serverHostname = scanner.nextLine();

		System.out.print("Enter the server port: ");
		int port = Integer.parseInt(scanner.nextLine());

		Socket echoSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			echoSocket = new Socket(serverHostname, port);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("Error connecting to host: " + serverHostname);
			System.exit(1);
		}

		System.out.println("Connected to " + echoSocket.getInetAddress().getHostAddress() + ":" + echoSocket.getPort());

		boolean running = true;
		while (running) {
			System.out.println("\nSelect an option:");
			System.out.println("1 - Register");
			System.out.println("2 - Read");
			System.out.println("3 - Update");
			System.out.println("4 - Delete");
			System.out.println("5 - Login");
			System.out.println("6 - Logout");
			System.out.println("7 - Create Category");
			System.out.println("8 - Read Category");
			System.out.println("9 - Update Category");			
			System.out.println("10 - Delete Category");
			System.out.println("11 - Create Announcements");
			System.out.println("12 - Read Announcements");
			System.out.println("13 - Update Announcement");
			System.out.println("14 - Delete Announcement");
            System.out.println("15 - Subscribe to Category");
            System.out.println("16 - Unsubscribe from Category");

			System.out.print("Option: ");
			String option = scanner.nextLine();
			switch (option) {
			case "1":
				handleRegister(scanner, out, in);
				break;
			case "2":
				handleRead(scanner, out, in);
				break;
			case "3":
				handleUpdate(scanner, out, in);
				break;
			case "4":
				handleDelete(scanner, out, in);
				break;
			case "5":
				handleLogin(scanner, out, in);
				break;
			case "6":
				handleLogout(scanner, out, in);
				System.out.println("Shutting down client...");
				running = false;
				break;
			case "7":
				handleCreateCategory(scanner, out, in);
				break;
			case "8":
				handleReadCategory(scanner, out, in);
				break;
			case "9":
				handleUpdateCategory(scanner, out, in);
				break;
			case "10":
				handleDeleteCategory(scanner, out, in);
				break;
			case "11":
			    handleCreateAnnouncement(scanner, out, in);
			    break;
			case "12":
			    handleReadAnnouncements(scanner, out, in);
			    break;
			case "13":
			    handleUpdateAnnouncement(scanner, out, in);
			    break;
			case "14":
			    handleDeleteAnnouncement(scanner, out, in);
			    break;
            case "15":
                handleSubscribe(scanner, out, in);
                break;
            case "16":
                handleUnsubscribe(scanner, out, in);
                break;
			default:
				System.out.println("Invalid option. Please try again.");
			}
		}

		out.close();
		in.close();
		echoSocket.close();
		scanner.close();
	}

	private static void handleRegister(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
		System.out.print("Enter the name (max 40 characters, no accents or special characters): ");
		String name = scanner.nextLine();

		System.out.print("Enter the user ID (7 digits): ");
		String user = scanner.nextLine();

		System.out.print("Enter the password (4 digits): ");
		String password = scanner.nextLine();

		Request register = new Request(name, "1", user, password, null);
		String json = register.serialize();

		System.out.println("Sending registration data: " + register.serialize());
		out.println(json);

		String serverResponse = in.readLine();
		System.out.println("\nServer response: " + serverResponse);
	}

	private static void handleLogin(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
		System.out.print("Enter the user ID (7 digits): ");
		String user = scanner.nextLine();

		System.out.print("Enter the password (4 digits): ");
		String password = scanner.nextLine();

		Request login = new Request(null, "5", user, password, null);
		String json = login.serialize();

		System.out.println("Sending login data: " + login.serialize());
		out.println(json);

		String serverResponse = in.readLine();
		System.out.println("\nServer response: " + serverResponse);
	}

	private static void handleLogout(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
		System.out.print("Enter the token: ");
		String token = scanner.nextLine();

		Request logout = new Request(null, "6", null, null, token);
		String json = logout.serialize();

		System.out.println("Sending logout data: " + logout.serialize());
		out.println(json);

		String serverResponse = in.readLine();
		System.out.println("Server response: " + serverResponse);
	}

	private static void handleRead(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
		System.out.print("Enter the user ID (7 digits): ");
		String user = scanner.nextLine();

		System.out.print("Enter the token: ");
		String token = scanner.nextLine();

		Request readRequest = new Request(null, "2", user, null, token);
		String json = readRequest.serialize();

		System.out.println("Sending read request: " + readRequest.serialize());
		out.println(json);

		String serverResponse = in.readLine();
		System.out.println("Server response: " + serverResponse);
	}

	private static void handleUpdate(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
		System.out.print("Enter the user ID (7 digits): ");
		String user = scanner.nextLine();

		System.out.print("Enter the new password (4 digits): ");
		String password = scanner.nextLine();

		System.out.print("Enter the new name: ");
		String name = scanner.nextLine();

		System.out.print("Enter the token: ");
		String token = scanner.nextLine();

		Request updateRequest = new Request(name, "3", user, password, token);
		String json = updateRequest.serialize();

		System.out.println("Sending update request: " + updateRequest.serialize());
		out.println(json);

		String serverResponse = in.readLine();
		System.out.println("Server response: " + serverResponse);
	}

	private static void handleDelete(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
		System.out.print("Enter the user ID (7 digits): ");
		String user = scanner.nextLine();

		System.out.print("Enter the token: ");
		String token = scanner.nextLine();

		Request deleteRequest = new Request(null, "4", user, null, token);
		String json = deleteRequest.serialize();

		System.out.println("Sending delete request: " + json);
		out.println(json);

		String serverResponse = in.readLine();
		System.out.println("Server response: " + serverResponse);
	}
	private static void handleCreateCategory(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
	    System.out.print("Enter the token: ");
	    String token = scanner.nextLine();

	    System.out.print("Enter the category name: ");
	    String categoryName = scanner.nextLine();

	    System.out.print("Enter the category description: ");
	    String categoryDescription = scanner.nextLine();
	    
	    Request createCategory = new Request("7", token, categoryName, categoryDescription);
	    String json = createCategory.serialize();
	    
	    System.out.println("Sending create request: " + createCategory.serialize());
	    out.println(json);
	    
	    String serverResponse = in.readLine();
	    System.out.println("Server response: " + serverResponse);
	}
	private static void handleReadCategory(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
		System.out.println("Enter the Token: ");
		String token = scanner.nextLine();
		
		Request readCategory = new Request("8", token, null, null);
		String json = readCategory.serialize();
		
		System.out.println("Sending read request: " + readCategory.serialize());
		out.println(json);
		
		String serverResponse = in.readLine();
		System.out.println("Server response: " + serverResponse);
		
	}
	private static void handleUpdateCategory(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
		System.out.println("Enter the Token: ");
		String token = scanner.nextLine();
		
		Request readCategory = new Request("8", token, null, null);
		String json = readCategory.serialize();
		
		//System.out.println("Sending read request: " + readCategory.serialize());
		out.println(json);
		
		String serverResponse = in.readLine();
		//System.out.println("Server response: " + serverResponse);
		
		Response response = Response.deserialize(serverResponse);
		
		System.out.println("Enter the Category ID: ");
		int catId = Integer.parseInt(scanner.nextLine());
		
		System.out.println("Enter the new Name: ");
		String newName = scanner.nextLine();
				
		System.out.println("Enter the new Description: ");
		String newDescription = scanner.nextLine();
		
		int changed = 0;
		for (Categories categorie : response.categories) {
			if (Integer.parseInt(categorie.id) == catId) {
				categorie.name = newName;
				categorie.description = newDescription;
			}
		}
		
		Request updateCategory = new Request("9", token, response.categories);
		json = updateCategory.serialize();
		
		System.out.println("Sending update request: " + updateCategory.serialize());
		out.println(json);
		
		serverResponse = in.readLine();
		System.out.println("Server Response: " + serverResponse);
	}
	private static void handleDeleteCategory(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
	    System.out.print("Enter the Token: ");
	    String token = scanner.nextLine();

	    System.out.print("Enter Category IDs to delete (comma separated): ");
	    String idsInput = scanner.nextLine();
	    List<String> categoryIds = List.of(idsInput.split(","));

	    // Construir a requisição usando o padrão existente
	    Request deleteRequest = new Request();
	    deleteRequest.op = "10";
	    deleteRequest.token = token;
	    deleteRequest.categoryIds = categoryIds;

	    System.out.println("Sending delete request: " + deleteRequest.serialize());
	    out.println(deleteRequest.serialize());

	    String serverResponse = in.readLine();
	    System.out.println("Server response: " + serverResponse);
	}
	private static void handleCreateAnnouncement(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
	    System.out.print("Enter token: ");
	    String token = scanner.nextLine();
	    
	    System.out.print("Title: ");
	    String title = scanner.nextLine();
	    
	    System.out.print("Text: ");
	    String text = scanner.nextLine();
	    
	    System.out.print("Category ID: ");
	    String catId = scanner.nextLine();
	    
	    Request req = new Request();
	    req.op = "11";
	    req.token = token;
	    req.title = title;
	    req.text = text;
	    req.categoryId = catId;
	    
	    out.println(req.serialize());
	    System.out.println("Response: " + in.readLine());
	}

	// Métodos handlers:
	private static void handleReadAnnouncements(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
	    System.out.print("Enter Token: ");
	    String token = scanner.nextLine();
	    
	    Request req = new Request();
	    req.op = "12";
	    req.token = token;
	    
	    System.out.println("Sending Read Request: " + req.serialize());
	    out.println(req.serialize());
	    
	    
	    System.out.println("Response: " + in.readLine());
	}

	private static void handleUpdateAnnouncement(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
	    System.out.print("Enter token: ");
	    String token = scanner.nextLine();
	    
	    System.out.print("Announcement ID to update: ");
	    String id = scanner.nextLine();
	    
	    System.out.print("New Title: ");
	    String title = scanner.nextLine();
	    
	    System.out.print("New Text: ");
	    String text = scanner.nextLine();
	    
	    System.out.print("New Category ID: ");
	    String catId = scanner.nextLine();
	    
	    Request req = new Request();
	    req.op = "13";
	    req.token = token;
	    req.id = id;         // ID do anúncio a ser atualizado
	    req.title = title;
	    req.text = text;
	    req.categoryId = catId;
	    
	    System.out.println("Sending Update Request" + req.serialize());
	    
	    out.println(req.serialize());
	    System.out.println("Response: " + in.readLine());
	}

	private static void handleDeleteAnnouncement(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
	    System.out.print("Enter token: ");
	    String token = scanner.nextLine();
	    
	    System.out.print("Announcement ID to delete: ");
	    String id = scanner.nextLine();
	    
	    Request req = new Request("14", token, id);
	    req.op = "14";
	    req.token = token;
	    req.id = id;
	    
	    System.out.println("Sending Delete Request: " + req.serialize());;
	    
	    out.println(req.serialize());
	    System.out.println("Response: " + in.readLine());
	}
    private static void handleSubscribe(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
        System.out.print("Enter token: ");
        String token = scanner.nextLine();
        
        System.out.print("Enter Category ID: ");
        String categoryId = scanner.nextLine();
        
        Request req = new Request();
        req.op = "15";
        req.token = token;
        req.categoryId = categoryId; // Campo alinhado com o protocolo
        
        out.println(req.serialize());
        System.out.println("Server response: " + in.readLine());
    }

    private static void handleUnsubscribe(Scanner scanner, PrintWriter out, BufferedReader in) throws IOException {
        System.out.print("Enter token: ");
        String token = scanner.nextLine();
        
        System.out.print("Enter Category ID: ");
        String categoryId = scanner.nextLine();
        
        Request req = new Request();
        req.op = "16";
        req.token = token;
        req.categoryId = categoryId; // Campo alinhado com o protocolo
        
        out.println(req.serialize());
        System.out.println("Server response: " + in.readLine());
    }
}

