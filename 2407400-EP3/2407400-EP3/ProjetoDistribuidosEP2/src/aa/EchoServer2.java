package aa;

import java.net.*;
import java.io.*;
import com.google.gson.*;

public class EchoServer2 extends Thread {
    private Socket clientSocket;

    public static void main(String[] args) throws IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Digite a porta para o servidor: ");
        int port = Integer.parseInt(stdIn.readLine());

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Servidor iniciado na porta " + port);
            while (true) {
                new EchoServer2(serverSocket.accept());
            }
        } catch (IOException e) {
            System.err.println("Não foi possível iniciar o servidor na porta: " + port);
            System.exit(1);
        } finally {
            if (serverSocket != null) serverSocket.close();
        }
    }

    private EchoServer2(Socket clientSoc) {
        this.clientSocket = clientSoc;
        start();
    }

    public void run() {
        System.out.println("Nova conexão iniciada");

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            Gson gson = new Gson();

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Mensagem recebida: " + inputLine);

                // Aqui você pode processar as mensagens JSON e responder
                out.println(inputLine); // Temporariamente, retorna a mensagem recebida

                if (inputLine.equalsIgnoreCase("bye")) break;
            }

            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Erro na comunicação com o cliente.");
            e.printStackTrace();
        }
    }
}
