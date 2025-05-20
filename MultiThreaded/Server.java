
import java.io.*;
import java.net.*;

public class Server {

    private static final int PORT = 8010;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                
                String requestLine = in.readLine();
                if (requestLine == null || requestLine.isEmpty()) {
                    socket.close();
                    return;
                }
                System.out.println("Request: " + requestLine);
                String[] parts = requestLine.split(" ");
                String method = parts[0];
                String path = parts[1];

                
                String line;
                int contentLength = 0;
                while (!(line = in.readLine()).isEmpty()) {
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }

               
                if ("GET".equals(method)) {
                    File file = new File("index.html");
                    if (file.exists()) {
                        out.println("HTTP/1.1 200 OK");
                        out.println("Content-Type: text/html");
                        out.println();
                        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                            String htmlLine;
                            while ((htmlLine = fileReader.readLine()) != null) {
                                out.println(htmlLine);
                            }
                        }
                    } else {
                        out.println("HTTP/1.1 404 Not Found");
                        out.println("Content-Type: text/plain");
                        out.println();
                        out.println("File not found");
                    }

                
                } else if ("POST".equals(method)) {
                    char[] body = new char[contentLength];
                    in.read(body);
                    String postData = new String(body);
                    System.out.println("POST data: " + postData);

                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/plain");
                    out.println();
                    out.println("Data received: " + postData);

                
                } else {
                    out.println("HTTP/1.1 405 Method Not Allowed");
                    out.println("Content-Type: text/plain");
                    out.println();
                    out.println("Only GET and POST supported.");
                }

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
