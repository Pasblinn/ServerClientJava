package aa;

import com.google.gson.Gson;

public class Cadastro {
	private int id;
    private String op; // Sempre 1 para operação de cadastro
    private String nome; // Nome do usuário (até 40 caracteres)
    private String user; // ID do usuário (7 dígitos)
    private String password; // Senha (4 números)
    private String isAdmin;
    
    public Cadastro(String nome, String user, String password, String isAdmin) {
       // this.op = op; // Define operação como "Cadastro"
        this.nome = nome;
        this.user = user;
        this.password = password;
        this.isAdmin = isAdmin;
    }
    
    public Cadastro(String nome, String user, String password, String isAdmin, int id) {
        // this.op = op; // Define operação como "Cadastro"
         this.nome = nome;
         this.user = user;
         this.password = password;
         this.isAdmin = isAdmin;
         this.id = id;
     }

    
    

    public boolean isAdmin() {
        return "1".equals(isAdmin); // Isso evita erro se isAdmin for null
    }


	public void setAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getOp() {
        return op;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome.length() > 40) {
            throw new IllegalArgumentException("O nome deve ter no máximo 40 caracteres.");
        }
        this.nome = nome;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        if (!user.matches("\\d{7}")) {
            throw new IllegalArgumentException("O usuário deve conter exatamente 7 dígitos.");
        }
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (!password.matches("\\d{4}")) {
            throw new IllegalArgumentException("A senha deve conter exatamente 4 números.");
        }
        this.password = password;
    }
    
    public int getId() {
		return id;
	}

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
