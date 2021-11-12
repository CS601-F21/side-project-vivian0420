package cs601.sideProject;

public class LoginHandler implements Handler{
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        System.out.println(request.getContent());
        String user = request.getContent().split("&")[0];
        String passW = request.getContent().split("&")[1];
        String userName = user.split("=")[1];
        String password = passW.split("=")[1];



    }
}
