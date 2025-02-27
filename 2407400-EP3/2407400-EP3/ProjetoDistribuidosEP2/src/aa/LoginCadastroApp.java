package aa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.google.gson.Gson;

public class LoginCadastroApp {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cadastro e Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        JTabbedPane tabbedPane = new JTabbedPane();

        // Aba de Cadastro
        JPanel cadastroPanel = new JPanel();
        cadastroPanel.setLayout(new GridLayout(4, 2));
        
        JLabel nomeLabel = new JLabel("Nome:");
        JTextField nomeField = new JTextField();
        
        JLabel usuarioLabel = new JLabel("Usuário:");
        JTextField usuarioField = new JTextField();
        
        JLabel senhaLabel = new JLabel("Senha:");
        JPasswordField senhaField = new JPasswordField();
        
        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Constrói o JSON de cadastro
                Cadastro cadastro = new Cadastro(nomeField.getText(), usuarioField.getText(), new String(senhaField.getPassword()), "0");
                String jsonCadastro = cadastro.serialize();
                
                JOptionPane.showMessageDialog(frame, "Dados enviados (JSON):\n" + jsonCadastro);
            }
        });
        
        cadastroPanel.add(nomeLabel);
        cadastroPanel.add(nomeField);
        cadastroPanel.add(usuarioLabel);
        cadastroPanel.add(usuarioField);
        cadastroPanel.add(senhaLabel);
        cadastroPanel.add(senhaField);
        cadastroPanel.add(new JLabel()); // Espaço vazio
        cadastroPanel.add(cadastrarButton);

        // Aba de Login
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 2));
        
        JLabel usuarioLoginLabel = new JLabel("Usuário:");
        JTextField usuarioLoginField = new JTextField();
        
        JLabel senhaLoginLabel = new JLabel("Senha:");
        JPasswordField senhaLoginField = new JPasswordField();
        
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Constrói o JSON de login
                Login login = new Login(usuarioLoginField.getText(), new String(senhaLoginField.getPassword()));
                String jsonLogin = login.serialize();
                
                JOptionPane.showMessageDialog(frame, "Dados enviados (JSON):\n" + jsonLogin);
            }
        });
        
        loginPanel.add(usuarioLoginLabel);
        loginPanel.add(usuarioLoginField);
        loginPanel.add(senhaLoginLabel);
        loginPanel.add(senhaLoginField);
        loginPanel.add(new JLabel()); // Espaço vazio
        loginPanel.add(loginButton);

        tabbedPane.addTab("Cadastro", cadastroPanel);
        tabbedPane.addTab("Login", loginPanel);
        
        frame.add(tabbedPane);
        frame.setVisible(true);
    }
}
