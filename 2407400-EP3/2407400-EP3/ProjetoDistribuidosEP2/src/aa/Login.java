package aa;

import com.google.gson.Gson;

public class Login {
    private int op; // Operação de login (5)
    private String userLogin; // ID do usuário (7 dígitos)
    private String passwordLogin; // Senha (4 números)

    public Login(String userLogin, String passwordLogin) {
        this.op = 5; // Operação de login
        this.userLogin = userLogin;
        this.passwordLogin = passwordLogin;
    }

    public int getOp() {
        return op;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        if (!userLogin.matches("\\d{7}")) {
            throw new IllegalArgumentException("O usuário deve conter exatamente 7 dígitos.");
        }
        this.userLogin = userLogin;
    }

    public String getPasswordLogin() {
        return passwordLogin;
    }

    public void setPasswordLogin(String passwordLogin) {
        if (!passwordLogin.matches("\\d{4}")) {
            throw new IllegalArgumentException("A senha deve conter exatamente 4 números.");
        }
        this.passwordLogin = passwordLogin;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
