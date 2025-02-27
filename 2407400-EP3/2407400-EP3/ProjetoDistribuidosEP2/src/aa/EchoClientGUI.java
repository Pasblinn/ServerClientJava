package aa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import com.google.gson.*;

public class EchoClientGUI extends JFrame {
	private JTextField serverIPField, portField;
	private JTextArea responseArea;
	private JTabbedPane tabbedPane;

	private Socket echoSocket;
	private PrintWriter out;
	private BufferedReader in;
	private String currentToken;
	private Gson gson = new Gson();

	public EchoClientGUI() {
		initComponents();
		setLocationRelativeTo(null);
	}

	private void initComponents() {
		setTitle("Client");
		setSize(1000, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Painel de conexão
		JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		serverIPField = new JTextField("localhost", 15);
		portField = new JTextField("20000", 5);
		JButton connectButton = new JButton("Connect");

		connectButton.addActionListener(e -> connectToServer());

		connectionPanel.add(new JLabel("Server IP:"));
		connectionPanel.add(serverIPField);
		connectionPanel.add(new JLabel("Port:"));
		connectionPanel.add(portField);
		connectionPanel.add(connectButton);

		// Área de resposta
		responseArea = new JTextArea();
		responseArea.setEditable(false);

		// Abas de operações
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("User", createUserPanel());
		tabbedPane.addTab("Category", createCategoryPanel());
		tabbedPane.addTab("Announcement", createAnnouncementPanel());
		tabbedPane.addTab("Subscription", createSubscriptionPanel());

		getContentPane().add(connectionPanel, BorderLayout.NORTH);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, new JScrollPane(responseArea));
		splitPane.setResizeWeight(0.7); // 70% para abas, 30% para logs
		getContentPane().add(splitPane, BorderLayout.CENTER);
	}

	// Seção de Usuário
	private JPanel createUserPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		String[] buttons = { "Register", "Login", "Read", "Update", "Delete", "Logout" };
		for (String label : buttons) {
			JButton btn = new JButton(label);
			btn.addActionListener(new UserButtonListener(label));
			panel.add(btn);
		}
		return panel;
	}

	private void handleLogout() {
		if (currentToken == null) {
			appendResponse("No active session to logout");
			return;
		}

		Request req = new Request();
		req.op = "6";
		req.token = currentToken;
		sendRequest(req);
		currentToken = null;
	}

	private void showDeleteUserDialog() {
		JDialog dialog = new JDialog(this, "Delete User", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField userField = new JTextField();
		JTextField tokenField = new JTextField(currentToken);

		dialog.getContentPane().add(new JLabel("User ID:"));
		dialog.getContentPane().add(userField);
		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);

		JButton submit = new JButton("Delete");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "4";
			req.user = userField.getText();
			req.token = tokenField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showUpdateUserDialog() {
		JDialog dialog = new JDialog(this, "Update User", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField userField = new JTextField();
		JPasswordField passField = new JPasswordField();
		JTextField nameField = new JTextField();
		JTextField tokenField = new JTextField(currentToken);

		dialog.getContentPane().add(new JLabel("User ID:"));
		dialog.getContentPane().add(userField);
		dialog.getContentPane().add(new JLabel("New Password:"));
		dialog.getContentPane().add(passField);
		dialog.getContentPane().add(new JLabel("New Name:"));
		dialog.getContentPane().add(nameField);
		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);

		JButton submit = new JButton("Update");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "3";
			req.user = userField.getText();
			req.password = new String(passField.getPassword());
			req.name = nameField.getText();
			req.token = tokenField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void sendRequest(Request request) {
		new SwingWorker<Void, String>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					String json = gson.toJson(request);
					appendResponse("[SENT] " + json); // Log do JSON enviado
					out.println(json);
					String response = in.readLine();
					publish(response);
				} catch (IOException ex) {
					publish("Error: " + ex.getMessage());
				}
				return null;
			}

			@Override
			protected void process(List<String> responses) {
				responses.forEach(response -> {
					appendResponse("[RECEIVED] " + response); // Log da resposta
					handleServerResponse(response);
				});
			}
		}.execute();
	}

	private void showReadUserDialog() {
		JDialog dialog = new JDialog(this, "Read User", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField userField = new JTextField();
		JTextField tokenField = new JTextField(currentToken);

		dialog.getContentPane().add(new JLabel("User ID:"));
		dialog.getContentPane().add(userField);
		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);

		JButton submit = new JButton("Read");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "2";
			req.user = userField.getText();
			req.token = tokenField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private class UserButtonListener implements ActionListener {
		private String operation;

		public UserButtonListener(String operation) {
			this.operation = operation;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (operation) {
			case "Register":
				showRegisterDialog();
				break;
			case "Login":
				showLoginDialog();
				break;
			case "Read":
				showReadUserDialog();
				break;
			case "Update":
				showUpdateUserDialog();
				break;
			case "Delete":
				showDeleteUserDialog();
				break;
			case "Logout":
				handleLogout();
				break;
			}
		}
	}

	private void showRegisterDialog() {
		JDialog dialog = new JDialog(this, "Register", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField nameField = new JTextField();
		JTextField userField = new JTextField();
		JPasswordField passField = new JPasswordField();

		dialog.getContentPane().add(new JLabel("Name:"));
		dialog.getContentPane().add(nameField);
		dialog.getContentPane().add(new JLabel("User ID (7 digits):"));
		dialog.getContentPane().add(userField);
		dialog.getContentPane().add(new JLabel("Password (4 digits):"));
		dialog.getContentPane().add(passField);

		JButton submit = new JButton("Submit");
		submit.addActionListener(e -> {
			Request req = new Request(nameField.getText(), "1", userField.getText(),
					new String(passField.getPassword()), null);
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showLoginDialog() {
		JDialog dialog = new JDialog(this, "Login", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField userField = new JTextField();
		JPasswordField passField = new JPasswordField();

		dialog.getContentPane().add(new JLabel("User ID:"));
		dialog.getContentPane().add(userField);
		dialog.getContentPane().add(new JLabel("Password:"));
		dialog.getContentPane().add(passField);

		JButton submit = new JButton("Login");
		submit.addActionListener(e -> {
			Request req = new Request(null, "5", userField.getText(), new String(passField.getPassword()), null);
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	// Implementações similares para Read/Update/Delete User...

	// Seção de Categorias
	private JPanel createCategoryPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		String[] buttons = { "Create Category", "Read Categories", "Update Category", "Delete Category" };
		for (String label : buttons) {
			JButton btn = new JButton(label);
			btn.addActionListener(new CategoryButtonListener(label));
			panel.add(btn);
		}
		return panel;
	}

	private class CategoryButtonListener implements ActionListener {
		private String operation;

		public CategoryButtonListener(String operation) {
			this.operation = operation;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (operation) {
			case "Create Category":
				showCreateCategoryDialog();
				break;
			case "Read Categories":
				handleReadCategories();
				break;
			case "Update Category":
				showUpdateCategoryDialog();
				break;
			case "Delete Category":
				showDeleteCategoryDialog();
				break;
			}
		}
	}

	private void showCreateCategoryDialog() {
		JDialog dialog = new JDialog(this, "Create Category", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);
		JTextField nameField = new JTextField();
		JTextField descField = new JTextField();

		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);
		dialog.getContentPane().add(new JLabel("Name:"));
		dialog.getContentPane().add(nameField);
		dialog.getContentPane().add(new JLabel("Description:"));
		dialog.getContentPane().add(descField);

		JButton submit = new JButton("Create");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "7";
			req.token = tokenField.getText();
			req.catName = nameField.getText();
			req.description = descField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showDeleteCategoryDialog() {
		JDialog dialog = new JDialog(this, "Delete Category", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);
		JTextField idsField = new JTextField();

		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);
		dialog.getContentPane().add(new JLabel("Category IDs (comma-separated):"));
		dialog.getContentPane().add(idsField);

		JButton submit = new JButton("Delete");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "10";
			req.token = tokenField.getText();
			req.categoryIds = List.of(idsField.getText().split(","));
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void handleReadCategories() {
		JDialog dialog = new JDialog(this, "Read User", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);

		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);

		JButton submit = new JButton("Read");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "8";
			req.token = tokenField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showUpdateCategoryDialog() {
		JDialog dialog = new JDialog(this, "Update Category", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);
		JTextField catIdField = new JTextField();
		JTextField nameField = new JTextField();
		JTextField descField = new JTextField();

		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);
		dialog.getContentPane().add(new JLabel("Category ID:"));
		dialog.getContentPane().add(catIdField);
		dialog.getContentPane().add(new JLabel("New Name:"));
		dialog.getContentPane().add(nameField);
		dialog.getContentPane().add(new JLabel("New Description:"));
		dialog.getContentPane().add(descField);

		JButton submit = new JButton("Update");
		submit.addActionListener(e -> {
			// Primeiro ler categorias
			Request readReq = new Request();
			readReq.op = "8";
			readReq.token = currentToken;

			new SwingWorker<Response, Void>() {
				@Override
				protected Response doInBackground() throws Exception {
					out.println(gson.toJson(readReq));
					return gson.fromJson(in.readLine(), Response.class);
				}

				@Override
				protected void done() {
					try {
						Response res = get();
						Categories selected = findCategory(res.categories, catIdField.getText());

						if (selected != null) {
							selected.name = nameField.getText();
							selected.description = descField.getText();

							Request updateReq = new Request();
							updateReq.op = "9";
							updateReq.token = currentToken;
							updateReq.categories = List.of(selected);
							sendRequest(updateReq);
							dialog.dispose();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}.execute();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private Categories findCategory(List<Categories> categories, String id) {
		return categories.stream().filter(c -> c.id.equals(id)).findFirst().orElse(null);
	}

	// Seção de Anúncios
	private JPanel createAnnouncementPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		String[] buttons = { "Create Announcement", "Read Announcements", "Update Announcement",
				"Delete Announcement" };
		for (String label : buttons) {
			JButton btn = new JButton(label);
			btn.addActionListener(new AnnouncementButtonListener(label));
			panel.add(btn);
		}
		return panel;
	}

	private class AnnouncementButtonListener implements ActionListener {
		private String operation;

		public AnnouncementButtonListener(String operation) {
			this.operation = operation;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (operation) {
			case "Create Announcement":
				showCreateAnnouncementDialog();
				break;
			case "Read Announcements":
				handleReadAnnouncements();
				break;
			case "Update Announcement":
				showUpdateAnnouncementDialog();
				break;
			case "Delete Announcement":
				showDeleteAnnouncementDialog();
				break;
			}
		}
	}

	// Seção de Subscrições
	private JPanel createSubscriptionPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JButton subscribeBtn = new JButton("Subscribe");
		subscribeBtn.addActionListener(e -> showSubscribeDialog());

		JButton unsubscribeBtn = new JButton("Unsubscribe");
		unsubscribeBtn.addActionListener(e -> showUnsubscribeDialog());

		panel.add(subscribeBtn);
		panel.add(unsubscribeBtn);
		return panel;
	}

	private void showCreateAnnouncementDialog() {
		JDialog dialog = new JDialog(this, "Create Announcement", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);
		JTextField titleField = new JTextField();
		JTextArea textArea = new JTextArea();
		JScrollPane textScroll = new JScrollPane(textArea);
		JTextField categoryIdField = new JTextField();

		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);
		dialog.getContentPane().add(new JLabel("Title:"));
		dialog.getContentPane().add(titleField);
		dialog.getContentPane().add(new JLabel("Text:"));
		dialog.getContentPane().add(textScroll);
		dialog.getContentPane().add(new JLabel("Category ID:"));
		dialog.getContentPane().add(categoryIdField);

		JButton submit = new JButton("Create");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "11";
			req.token = tokenField.getText();
			req.title = titleField.getText();
			req.text = textArea.getText();
			req.categoryId = categoryIdField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void handleReadAnnouncements() {
		JDialog dialog = new JDialog(this, "Read User", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);

		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);

		JButton submit = new JButton("Read");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "12";
			req.token = tokenField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showUpdateAnnouncementDialog() {
		JDialog dialog = new JDialog(this, "Update Announcement", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);
		JTextField idField = new JTextField();
		JTextField titleField = new JTextField();
		JTextArea textArea = new JTextArea();
		JScrollPane textScroll = new JScrollPane(textArea);
		JTextField categoryIdField = new JTextField();

		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);
		dialog.getContentPane().add(new JLabel("Announcement ID:"));
		dialog.getContentPane().add(idField);
		dialog.getContentPane().add(new JLabel("New Title:"));
		dialog.getContentPane().add(titleField);
		dialog.getContentPane().add(new JLabel("New Text:"));
		dialog.getContentPane().add(textScroll);
		dialog.getContentPane().add(new JLabel("New Category ID:"));
		dialog.getContentPane().add(categoryIdField);

		JButton submit = new JButton("Update");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "13";
			req.token = tokenField.getText();
			req.id = idField.getText();
			req.title = titleField.getText();
			req.text = textArea.getText();
			req.categoryId = categoryIdField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.setSize(400, 300);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showDeleteAnnouncementDialog() {
		JDialog dialog = new JDialog(this, "Delete Announcement", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);
		JTextField idField = new JTextField();

		dialog.getContentPane().add(new JLabel("Token:"));
		dialog.getContentPane().add(tokenField);
		dialog.getContentPane().add(new JLabel("Announcement ID:"));
		dialog.getContentPane().add(idField);

		JButton submit = new JButton("Delete");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "14";
			req.token = tokenField.getText();
			req.id = idField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void showSubscribeDialog() {
		JDialog dialog = new JDialog(this, "Subscribe", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);
		JTextField catIdField = new JTextField();

		JLabel label = new JLabel("Token:");
		dialog.getContentPane().add(label);
		dialog.getContentPane().add(tokenField);
		dialog.getContentPane().add(new JLabel("Category ID:"));
		dialog.getContentPane().add(catIdField);

		JButton submit = new JButton("Subscribe");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "15";
			req.token = tokenField.getText();
			req.categoryId = catIdField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	// Métodos de conexão e comunicação
	private void connectToServer() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					echoSocket = new Socket(serverIPField.getText(), Integer.parseInt(portField.getText()));
					out = new PrintWriter(echoSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
					appendResponse("Connected to server!");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(EchoClientGUI.this, "Connection error: " + ex.getMessage());
				}
				return null;
			}
		}.execute();
	}

	private void showUnsubscribeDialog() {
		JDialog dialog = new JDialog(this, "Unsubscribe", true);
		dialog.getContentPane().setLayout(new GridLayout(0, 2, 5, 5));

		JTextField tokenField = new JTextField(currentToken);
		JTextField catIdField = new JTextField();

		JLabel label = new JLabel("Token:");
		dialog.getContentPane().add(label);
		dialog.getContentPane().add(tokenField);
		dialog.getContentPane().add(new JLabel("Category ID:"));
		dialog.getContentPane().add(catIdField);

		JButton submit = new JButton("Unsubscribe");
		submit.addActionListener(e -> {
			Request req = new Request();
			req.op = "16";
			req.token = tokenField.getText();
			req.categoryId = catIdField.getText();
			sendRequest(req);
			dialog.dispose();
		});

		dialog.getContentPane().add(submit);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	private void handleServerResponse(String response) {
		Response res = gson.fromJson(response, Response.class);
		if (res != null && res.token != null) {
			currentToken = res.token;
		}
	}

	private void appendResponse(String text) {
		SwingUtilities.invokeLater(() -> responseArea.append(text + "\n"));
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new EchoClientGUI().setVisible(true));
	}
}