
import java.io.*;
import java.net.*;

public class Server {

    public void run() throws IOException {
        int port = 8010;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is waiting for client...");

        while (true) {
            System.out.println("Server is listening on port " + port);
            Socket socket = serverSocket.accept();
            System.out.println("Connection accepted from " + socket.getRemoteSocketAddress());

            BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter toClient = new PrintWriter(socket.getOutputStream());

            // Read request line. Reads the first line of the HTTP request (e.g., GET / HTTP/1.1).
            String requestLine = fromClient.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                socket.close();
                continue;
            }

            System.out.println("Request: " + requestLine);
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            // Read and skip headers
            String line;
            int contentLength = 0;
            while (!(line = fromClient.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            if (method.equals("GET")) {
                // Serve static HTML
                File file = new File("index.html"); //Looks for a file named index.html.
                if (file.exists()) {
                    BufferedReader fileReader = new BufferedReader(new FileReader(file));
                    toClient.println("HTTP/1.1 200 OK");
                    toClient.println("Content-Type: text/html");
                    toClient.println(); //Sends HTTP response headers followed by an empty line (required by the protocol).
                    String htmlLine;
                    while ((htmlLine = fileReader.readLine()) != null) {
                        toClient.println(htmlLine);
                    }
                    fileReader.close();
                } else {
                    toClient.println("HTTP/1.1 404 Not Found"); //if not found sends an error response. 
                    toClient.println("Content-Type: text/plain");
                    toClient.println();
                    toClient.println("File not found");
                }
            } else if (method.equals("POST")) {
                // Read the POST body
                char[] body = new char[contentLength];
                fromClient.read(body);
                String postData = new String(body);
                System.out.println("POST data: " + postData);

                // Respond
                toClient.println("HTTP/1.1 200 OK");
                toClient.println("Content-Type: text/plain");
                toClient.println();
                toClient.println("Data received: " + postData);
            } else {
                toClient.println("HTTP/1.1 405 Method Not Allowed");
                toClient.println("Content-Type: text/plain");
                toClient.println();
                toClient.println("Only GET and POST supported.");
            }

            toClient.flush();
            socket.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }
}
