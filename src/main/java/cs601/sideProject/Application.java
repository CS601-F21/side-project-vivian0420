package cs601.sideProject;

public class Application {
    public static void main(String[] args) {
        int port = 1024;
        HttpServer server = new HttpServer(port);
        server.setMapping("/home", new HomeHandler());
        server.setMapping("/login", new LoginHandler());
        server.setMapping("/logout", new LogoutHandler());
        server.setMapping("/CreateItemHandler", new CreateItemHandler());
        server.setMapping("/update", new UpdateHandler());
        server.setMapping("/signup", new SignUpHandler());
        server.startup();


    }
}