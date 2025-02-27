package aa;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.*;

public class ServerTest extends Thread {
	private Socket clientSocket;
	public boolean server = true;

	public static void main(String[] args) throws IOException, SQLException {
		
		Model.getInstance().getDatabase().addUser(new Cadastro("Paulo", "2406400", "1234", "1"));
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Digite a porta para o servidor: ");
		int port = Integer.parseInt(stdIn.readLine());

		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Servidor iniciado na porta " + port);

			while (true) {
				new ServerTest(serverSocket.accept());
			}
		} catch (IOException e) {
			System.err.println("Erro ao iniciar o servidor na porta: " + port);
			e.printStackTrace();
		} finally {
			if (serverSocket != null)
				serverSocket.close();
		}
	}

	private ServerTest(Socket clientSoc) {
		this.clientSocket = clientSoc;
		start();
	}

	public void run() {
		try {
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String inputLine;
			Gson gson = new Gson();

			while (server) {
				inputLine = in.readLine();
				System.out.println("Mensagem recebida: " + inputLine);	

				String response;
				try {
					Request request = gson.fromJson(inputLine, Request.class);
					response = processRequest(request);

				} catch (SQLException e) {
					System.out.println(e.getMessage());
					response = new Response("999", "Invalid JSON format", null).serialize();
				}
				System.out.println("Enviando resposta: " + response);
				out.println(response);

			}
			System.out.println("Desconectando com: " + clientSocket.getInetAddress().getHostAddress() + ":"
					+ clientSocket.getPort());
			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Erro na comunicação com o cliente.");
			e.printStackTrace();
		}
	}


	private String processRequest(Request request) throws SQLException {
		Gson gson = new Gson();
		Cadastro user = null;
		switch (request.op) {
		case "1": // Cadastro
			// Verifique se o usuário já existe
			if (Model.getInstance().getDatabase().getUserByToken(request.user) != null) {
				return gson.toJson(new Response("103", "Already exists an account with the username", null));
			}
			// Valide os campos
			if (request.user == null || request.password == null || !request.password.matches("\\d{4}")
					|| request.name == null || request.name.length() > 40) {
				return gson.toJson(new Response("101", "Fields missing", null));
			}
			if (!request.name.matches("[a-zA-Z0-9 ]*") || !request.password.matches("\\d{4}")
					|| !request.user.matches("\\d{7}")) {
				return gson.toJson(new Response("102", "Invalid information: user or password", null));
			}
			// Adicione o novo usuário
			Model.getInstance().getDatabase().addUser((new Cadastro(request.name, request.user, request.password, request.isAdmin)));
			return gson.toJson(new Response("100", "Successfull account creation", null));
		case "5": // Login
			// Verifique as credenciais do usuário
			if (request.user == null || request.password == null) {
				return gson.toJson(new Response("002", "Fields missing", null));
			}
			user = Model.getInstance().getDatabase().getUserByToken(request.user);
			if (user != null) {
				if (user.getPassword().equals(request.password)) {
					return gson.toJson(new Response("000", "Successful login", request.user));
				}
			}
			return gson.toJson(new Response("003", "Login failed", null));
		case "6": // Logout
			if (request.token == null || request.token.isEmpty()) {
				return gson.toJson(new Response("011", "Fields missing", null));
			}
			return gson.toJson(new Response("010", "Successful logout", null));

		case "2": // Read
			// Verifique se o token é válido
			user = Model.getInstance().getDatabase().getUserByToken(request.token);
			if (user == null) {
				return gson.toJson(new Response("112", "Invalid or empty token", null));
			}
			// Permita que administradores leiam qualquer usuário ou que usuários comuns
			// leiam seus próprios dados
			if (request.user.equals("") || request.user == null) {
				Cadastro targetUser = Model.getInstance().getDatabase().getUserByToken(request.token);
				if (targetUser != null) {
					String responseCode = targetUser.isAdmin() ? "111" : "110";
					return gson.toJson(new Response(responseCode, targetUser.getUser(), targetUser.getPassword(), targetUser.getNome(),"Returns all information of the account"));
				} else {
					return gson.toJson(new Response("114", "User not found ( Admin Only )", null));
				}
			}
			else if (user.isAdmin() || user.getUser().equals(request.user)) {
				Cadastro targetUser = Model.getInstance().getDatabase().getUserByToken(request.user);
				if (targetUser != null) {
					String responseCode = targetUser.isAdmin() ? "111" : "110";
					return gson.toJson(new Response(responseCode, targetUser.getUser(), targetUser.getPassword(), targetUser.getNome(),"Returns all information of the account"));
				} else {
					return gson.toJson(new Response("114", "User not found ( Admin Only )", null));
				}
			} else {
				return gson.toJson(new Response("113",
						"Invalid Permission, user does not have permission to visualize other users data", null));
			}
		case "3": // Update
			// Verifique se o token é válido
			user = Model.getInstance().getDatabase().getUserByToken(request.token);
			if (user == null) {
				return gson.toJson(new Response("121", "invalid or empty token", null));
			}
			// Permita que administradores atualizem qualquer usuário ou que usuários comuns
			// atualizem seus próprios dados
			if (user.isAdmin() || user.getUser().equals(request.user)) {
				Cadastro targetUser = Model.getInstance().getDatabase().getUserByToken(request.user);
				if (targetUser != null) {
					targetUser.setNome(request.name);
					targetUser.setPassword(request.password);
					return gson.toJson(new Response("120", "Account sucessfully updated", null));
				} else {
					return gson.toJson(new Response("123", "No User or token found (Admin Only)", null));
				}
			} else {
				return gson.toJson(new Response("122",
						"Invalid Permission, user does not have permission to update other users data", null));
			}
		case "4": // Delete
			// Verifique se o token é válido
			user = Model.getInstance().getDatabase().getUserByToken(request.token);
			if (user == null) {
				return gson.toJson(new Response("121", "invalid or empty token", null));
			}
			// Permita que administradores excluam qualquer usuário ou que usuários comuns
			// excluam seus próprios dados
			if (user.isAdmin() || user.getUser().equals(request.user)) {
				Cadastro targetUser = Model.getInstance().getDatabase().getUserByToken(request.user);
				if (targetUser != null) {
					Model.getInstance().getDatabase().deleteUser(targetUser.getId());
					return gson.toJson(new Response("130", "Account Sucessfully Deleted ", null));
				} else {
					return gson.toJson(new Response("134", "User Not Found (Admin Only)", null));
				}
			} else {
				return gson.toJson(new Response("133",
						"Invalid Permission, User does not have permission to delete account", null));
			}
		case "7": //Create Category
	        if (request.token == null || request.token.isEmpty()) {
	            return gson.toJson(new Response("202", "Invalid token", null));
	        }

	        user = Model.getInstance().getDatabase().getUserByToken(request.token);
	        if (user == null || !user.isAdmin()) {
	            return gson.toJson(new Response("202", "Invalid token", null));
	        }

//	        if (request.catName == null || request.catName.isEmpty() ||
//	            request.description == null || request.description.isEmpty()) {
//	            return gson.toJson(new Response("201", "Missing fields", null));
//	        }

	        Model.getInstance().getDatabase().addCategorie(new Categories(request.catName, request.description));
	        return gson.toJson(new Response("200", "Successful category creation", null));
		case "8": // READ CATEGORY
		    if (request.token == null || request.token.isEmpty()) {
		        return gson.toJson(new Response("212", "Invalid token", null));
		    }
		    
		    // Verificar se o usuário existe
		     user = Model.getInstance().getDatabase().getUserByToken(request.token);
		    if (user == null) {
		        return gson.toJson(new Response("212", "Invalid token", null));
		    }
		    
		    try {
		        List<Categories> categories = Model.getInstance().getDatabase().getCategoriesList(request.token);
		        return gson.toJson(new Response("210", "Successful category read", categories, null));
		    } catch (SQLException e) {
		        return gson.toJson(new Response("213", "Unknown Error", null));
		    }
		case "9": // Update Category
	        	if (request.token == null || request.token.isEmpty()) {
	        	    return gson.toJson(new Response("222", "Invalid token", null));
	        	}

	        	user = Model.getInstance().getDatabase().getUserByToken(request.token);
	        	if (user == null || !user.isAdmin()) {
	        	    return gson.toJson(new Response("222", "Invalid token", null));
	        	}

	        	if (request.categories == null) {
	        	    return gson.toJson(new Response("221", "Missing or invalid fields", null));
	        	}

	        	List<Categories> categories = Model.getInstance().getDatabase().getCategoriesList(request.token);
	        	List<String> catIds = categories.stream()
                        				.map(Categories::getId)
                        				.collect(Collectors.toList());
	        	
	        	for (Categories categorie : request.categories) {
	        		if (!catIds.contains(categorie.id)) {
	        			return gson.toJson(new Response("223", "Invalid information inserted", null));
	        		}
	        	}
	        	
	        	try {
	        		for (Categories categorie : request.categories) {
	        			 Model.getInstance().getDatabase().updateCategorie(categorie);
		        	}
	        	} catch (SQLException e) {
	        	    e.printStackTrace();
	        	    return gson.toJson(new Response("224", "Error updating category", null));
	        	}

	        	return gson.toJson(new Response("220", "Successful category update", null));
		case "10": // Delete Category
		    if (request.token == null || request.token.isEmpty()) {
		        return gson.toJson(new Response("232", "Invalid token", null));
		    }
		    
		    user = Model.getInstance().getDatabase().getUserByToken(request.token);
		    if (user == null || !user.isAdmin()) {
		        return gson.toJson(new Response("232", "Invalid token", null));
		    }
		    
		    if (request.categoryIds == null || request.categoryIds.isEmpty()) {
		        return gson.toJson(new Response("231", "Missing fields", null));
		    }
		    
		    try {
		        // Verificar se categorias existem e não estão em uso
		        for (String catId : request.categoryIds) {
		            Categories cat = Model.getInstance().getDatabase().getCategorieById(catId);
		            if (cat == null) {
		                return gson.toJson(new Response("233", "Invalid information inserted", null));
		            }
		            if (Model.getInstance().getDatabase().isCategoryInUse(catId)) {
		                return gson.toJson(new Response("234", "Category in use", null));
		            }
		        }
		        
		        // Deletar categorias
		        for (String catId : request.categoryIds) {
		            Model.getInstance().getDatabase().deleteCategorie(Integer.parseInt(catId));
		        }
		        return gson.toJson(new Response("230", "Successful category deletion", null));
		        
		    } catch (SQLException | NumberFormatException e) {
		        e.printStackTrace();
		        return gson.toJson(new Response("235", "Unknown Error", null));
		    }
		    
		case "11": // Create Announcement
		    if (request.token == null) {
		    	return gson.toJson(new Response("302", "Invalid Token", null));
		    }
		    	
		    
		    user = Model.getInstance().getDatabase().getUserByToken(request.token);
		    
		    if (request.title == null || request.text == null || request.categoryId == null) {
		        return gson.toJson(new Response("301", "Missing fields", null));
		    }
		    
		    Announcement ann = new Announcement();
		    ann.title = request.title;
		    ann.text = request.text;
		    ann.categoryId = request.categoryId;
		    ann.userId = String.valueOf(user.getId());
		    
		    Model.getInstance().getDatabase().addAnnouncement(ann);
		    return gson.toJson(new Response("300", "Successful announcement creation", null));

		case "12": // READ ANNOUNCEMENT
		    if (request.token == null) {
		        return gson.toJson(new Response("312", "Invalid token", null));
		    }
		    user = Model.getInstance().getDatabase().getUserByToken(request.token);
		    if (user == null) {
		        return gson.toJson(new Response("312", "Invalid token", null));
		    }
		    try {
		        boolean isAdmin = user.isAdmin();
		        List<Announcement> announcements = Model.getInstance().getDatabase().getAnnouncements(user.getId() + "", isAdmin);
		        return gson.toJson(new Response("310", "Successful announcement read", announcements, null));
		    } catch (SQLException e) {
		        return gson.toJson(new Response("315", "Unknown Error", null));
		    }

		case "13": // UPDATE ANNOUNCEMENT
		    if (request.token == null || request.id == null) {
		        return gson.toJson(new Response("321", "Missing fields", null));
		    }
		    user = Model.getInstance().getDatabase().getUserByToken(request.token);
		    if (user == null) {
		        return gson.toJson(new Response("322", "Invalid token", null));
		    }
		    try {
		        ann = new Announcement();
		        ann.id = request.id;
		        ann.title = request.title != null ? request.title : "";
		        ann.text = request.text != null ? request.text : "";
		        ann.categoryId = request.categoryId != null ? request.categoryId : "";
		        Model.getInstance().getDatabase().updateAnnouncement(ann);
		        return gson.toJson(new Response("320", "Successful announcement update", null));
		    } catch (SQLException e) {
		        return gson.toJson(new Response("324", "Unknown Error", null));
		    }

		case "14": // DELETE ANNOUNCEMENT
		    if (request.token == null || request.id == null) {
		        return gson.toJson(new Response("331", "Missing fields", null));
		    }
		    user = Model.getInstance().getDatabase().getUserByToken(request.token);
		    if (user == null || !user.isAdmin()) { // Apenas admin pode deletar
		        return gson.toJson(new Response("332", "Invalid token", null));
		    }
		    try {
		        Model.getInstance().getDatabase().deleteAnnouncement(request.id);
		        return gson.toJson(new Response("330", "Successful announcement deletion", null));
		    } catch (SQLException e) {
		        return gson.toJson(new Response("334", "Unknown Error", null));
		    }
		case "15": // SUBSCRIBE TO CATEGORY
            if (request.token == null || request.categoryId == null) {
                return gson.toJson(new Response("341", "Missing fields", null));
            }
            user = Model.getInstance().getDatabase().getUserByToken(request.token);
            if (user == null) {
                return gson.toJson(new Response("342", "Invalid token", null));
            }
            try {
                int categoryId = Integer.parseInt(request.categoryId);
                Categories category = Model.getInstance().getDatabase().getCategorieById(request.categoryId);
                if (category == null) {
                    return gson.toJson(new Response("343", "Invalid information inserted", null));
                }
                if (Model.getInstance().getDatabase().isUserSubscribed(user.getId(), categoryId)) {
                    return gson.toJson(new Response("343", "Already subscribed", null));
                }
                Model.getInstance().getDatabase().subscribeUserToCategory(user.getId(), categoryId);
                return gson.toJson(new Response("340", "Successful subscription", null));
            } catch (NumberFormatException | SQLException e) {
                return gson.toJson(new Response("344", "Unknown Error", null));
            }
        case "16": // UNSUBSCRIBE TO CATEGORY
            if (request.token == null || request.categoryId == null) {
                return gson.toJson(new Response("351", "Missing fields", null));
            }
            user = Model.getInstance().getDatabase().getUserByToken(request.token);
            if (user == null) {
                return gson.toJson(new Response("352", "Invalid token", null));
            }
            try {
                int categoryId = Integer.parseInt(request.categoryId);
                if (!Model.getInstance().getDatabase().isUserSubscribed(user.getId(), categoryId)) {
                    return gson.toJson(new Response("353", "Not subscribed", null));
                }
                Model.getInstance().getDatabase().unsubscribeUserFromCategory(user.getId(), categoryId);
                return gson.toJson(new Response("350", "Successfully unsubscribed", null));
            } catch (NumberFormatException | SQLException e) {
                return gson.toJson(new Response("354", "Unknown Error", null));
            }
		}
		
		return null;
	}
}
