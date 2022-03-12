import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

        int port = 8989;
        System.out.println("Запуск сервера " + port + "...");

        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(port);
                 Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                final String word = in.readLine();

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                out.println(gson.toJson(engine.search(word)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}