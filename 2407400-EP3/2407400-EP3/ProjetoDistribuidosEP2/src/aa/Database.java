package aa;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Database {
	private Connection connection;

	public Database() {
		try {
			this.connection = DriverManager.getConnection("jdbc:sqlite:database.db");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addUser(Cadastro user) throws SQLException {
		String sql = "INSERT INTO user (name, token, password, isAdmin) VALUES (?, ?, ?, ?)";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
			preparedStatement.setString(1, user.getNome());
			preparedStatement.setString(2, user.getUser());
			preparedStatement.setString(3, user.getPassword());
			preparedStatement.setInt(4, user.isAdmin() ? 1 : 0);
			preparedStatement.executeUpdate();
		}
	}

	public void updateUser(Cadastro user) throws SQLException {
		String sql = "UPDATE user SET name = ?, token = ?, password = ?, isAdmin = ? WHERE id = ?";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
			preparedStatement.setString(1, user.getNome());
			preparedStatement.setString(2, user.getUser());
			preparedStatement.setString(3, user.getPassword());
			preparedStatement.setInt(4, user.isAdmin() ? 1 : 0);
			preparedStatement.setInt(5, user.getId());
			preparedStatement.executeUpdate();
		}
	}

	public void deleteUser(int id) throws SQLException {
		String sql = "DELETE FROM user WHERE id = ?";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		}
	}

	public Cadastro getUserById(int id) throws SQLException {
		String sql = "SELECT * FROM user WHERE id = ?";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return getUserFromResultSet(resultSet);
			}
		}
		return null;
	}

	public Cadastro getUserByToken(String token) throws SQLException {
		String sql = "SELECT * FROM user WHERE token = ?";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
			preparedStatement.setString(1, token);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return getUserFromResultSet(resultSet);
			}
		}
		return null;
	}

	private Cadastro getUserFromResultSet(ResultSet resultSet) throws SQLException {
		if (resultSet == null) {
			return null;
		}
		String name = resultSet.getString("name");
		String token = resultSet.getString("token");
		String password = resultSet.getString("password");
		String isAdmin = resultSet.getInt("isAdmin") == 1 ? "1" : "0";
		int id = resultSet.getInt("id");
		return new Cadastro(name, token, password, isAdmin, id);
	}

	public void addCategorie(Categories categorie) throws SQLException {
		String sql = "INSERT INTO categorie (name, description, subscribed) VALUES (?, ?)";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
			preparedStatement.setString(1, categorie.getCatName());
			preparedStatement.setString(2, categorie.getDescription());
			preparedStatement.executeUpdate();
		}
	}

	public void updateCategorie(Categories categorie) throws SQLException {
		String sql = "UPDATE categorie SET name = ?, description = ? WHERE id = ?";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
			preparedStatement.setString(1, categorie.getCatName());
			preparedStatement.setString(2, categorie.getDescription());
			preparedStatement.setString(3, categorie.getId());
			preparedStatement.executeUpdate();
		}
	}

	public void deleteCategorie(int id) throws SQLException {
		String sql = "DELETE FROM categorie WHERE id = ?";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		}
	}

	public Categories getCategorieById(String catId) throws SQLException {
		String sql = "SELECT * FROM categorie WHERE id = ?";
		try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
			preparedStatement.setString(1, catId);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return getCategorieFromResultSet(resultSet);
			}
		}
		return null;
	}

	private Categories getCategorieFromResultSet(ResultSet resultSet) throws SQLException {
		if (resultSet == null) {
			return null;
		}
		String name = resultSet.getString("name");
		String description = resultSet.getString("description");
		String id = Integer.toString(resultSet.getInt("id"));
		return new Categories(name, description, id);
	}

	public List<Categories> getCategoriesList(String userToken) throws SQLException {
	    List<Categories> categorieList = new ArrayList<>();
	    
	    // Obter o ID do usuário a partir do token
	    Cadastro user = getUserByToken(userToken);
	    if (user == null) return categorieList; // Token inválido

	    String sql = "SELECT c.id, c.name, c.description, " +
	                 "CASE WHEN uc.userId IS NOT NULL THEN 'true' ELSE 'false' END AS subscribed " +
	                 "FROM categorie c " +
	                 "LEFT JOIN user_category uc ON c.id = uc.categoryId AND uc.userId = ?";
	    
	    try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
	        stmt.setInt(1, user.getId());
	        ResultSet resultSet = stmt.executeQuery();
	        
	        while (resultSet.next()) {
	            Categories categorie = new Categories(
	                resultSet.getString("name"),
	                resultSet.getString("description"),
	                resultSet.getString("id")
	            );
	            categorie.setSubscribed(resultSet.getString("subscribed")); // Define o status
	            categorieList.add(categorie);
	        }
	    }
	    return categorieList;
	}

	public boolean isCategoryInUse(String categoryId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM announcement WHERE categoryId = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, Integer.parseInt(categoryId));
			ResultSet rs = stmt.executeQuery();
			return rs.getInt(1) > 0;
		}
	}

	// Adicionar anúncio
	public void addAnnouncement(Announcement ann) throws SQLException {
		String sql = "INSERT INTO announcement (title, text, date, categoryId, userId) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, ann.title);
			stmt.setString(2, ann.text);
			stmt.setString(3, new SimpleDateFormat("dd/MM/yyyy").format(new Date())); // Corrigido
			stmt.setInt(4, Integer.parseInt(ann.categoryId));
			stmt.setInt(5, Integer.parseInt(ann.userId));
			stmt.executeUpdate();
		}
	}

	// Ler anúncios (exemplo básico)
	public List<Announcement> getAnnouncements(String userId, boolean isAdmin) throws SQLException {
		List<Announcement> announcements = new ArrayList<>();
		String sql = isAdmin ? "SELECT * FROM announcement" : "SELECT * FROM announcement WHERE userId = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			if (!isAdmin)
				stmt.setInt(1, Integer.parseInt(userId));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Announcement ann = new Announcement();
				ann.id = rs.getString("id");
				ann.title = rs.getString("title");
				ann.text = rs.getString("text");
				ann.date = rs.getString("date");
				ann.categoryId = rs.getString("categoryId");
				ann.userId = rs.getString("userId");
				announcements.add(ann);
			}
		}
		return announcements;
	}

	// Atualizar anúncio
	public void updateAnnouncement(Announcement ann) throws SQLException {
		String sql = "UPDATE announcement SET title = ?, text = ?, categoryId = ? WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, ann.title);
			stmt.setString(2, ann.text);
			stmt.setInt(3, Integer.parseInt(ann.categoryId));
			stmt.setInt(4, Integer.parseInt(ann.id));
			stmt.executeUpdate();
		}
	}

	// Deletar anúncio
	public void deleteAnnouncement(String id) throws SQLException {
		String sql = "DELETE FROM announcement WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, Integer.parseInt(id));
			stmt.executeUpdate();
		}
	}

	public void subscribeUserToCategory(int userId, int categoryId) throws SQLException {
		String sql = "INSERT INTO user_category (userId, categoryId) VALUES (?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, categoryId);
			stmt.executeUpdate();
		}
	}

	public void unsubscribeUserFromCategory(int userId, int categoryId) throws SQLException {
		String sql = "DELETE FROM user_category WHERE userId = ? AND categoryId = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, categoryId);
			stmt.executeUpdate();
		}
	}

	public boolean isUserSubscribed(int userId, int categoryId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM user_category WHERE userId = ? AND categoryId = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, categoryId);
			ResultSet rs = stmt.executeQuery();
			return rs.getInt(1) > 0;
		}
	}
}
