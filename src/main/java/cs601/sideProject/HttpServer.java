package cs601.sideProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTPServer class
 */
public class HttpServer {
       private final boolean running = true;
       private final Map<String, Handler> mapping;
       private int port;
       private ServerSocket serverSocket;
       private final ExecutorService executorService;

    /**
     * Constructor. Specify the the number of port and create serverSocket and a HashMap of mapping.
     * @param port the number of port that we will listen to
     */
    public HttpServer( int port) {
        this.mapping = new HashMap<String, Handler>();
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * Add request path and handler to mapping
     *
     * @param path    request path
     * @param handler instance of Handler
     */
    public void setMapping(String path, Handler handler) {
        this.mapping.put(path, handler);
    }

    /**
     *
     */
    public void startup() {
        new Thread(() -> {
            while(running) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }


                Map<String, String> headers = new HashMap<>();
                this.executorService.execute(() -> {
                    try (BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                         PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);) {

                        String requestLine = inStream.readLine();
                        String line = inStream.readLine();
                        while(line != null && !line.trim().isEmpty()) {
                            if(line.contains(":")) {
                                headers.put(line.split(":")[0].toLowerCase(), line.split(":")[1]);
                            }
                            line = inStream.readLine();
                        }
                        ServerRequest request = new ServerRequest(requestLine,headers,getContent(headers, inStream));
                        ServerResponse response = new ServerResponse(outputStream);
                        handleRequest(request, response);
                        inStream.close();
                        outputStream.close();
                        socket.close();

                    }

                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }).start();
    }

    /**
     * Get HTTP post content
     *
     * @param headers HTTP header
     * @param in      BufferedReader
     * @return post content
     */
    public String getContent(Map<String, String> headers, BufferedReader in) {
        if (!headers.containsKey("content-length")) {
            return null;
        }
        int length = Integer.parseInt(headers.get("content-length").trim());
        char[] content = new char[length];
        try {
            in.read(content, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new String(content);
    }

    /**
     * Handle HTTP requests
     *
     * @param request  The HTTP request that the server receives
     * @param response The HTTP response that the server sends
     */
    public void handleRequest(ServerRequest request, ServerResponse response) {
        if (request.is400()) {
            response.setCode(400);
            response.response("400 Bad Request");
        } else if (request.is405()) {
            response.setCode(405);
            response.response("405 Method Not Allowed");
        } else if (!this.mapping.containsKey(request.getPath())) {
            response.setCode(404);
            response.response("404 Not Found");
        } else {
            this.mapping.get(request.getPath()).handle(request, response);
        }

    }

}
