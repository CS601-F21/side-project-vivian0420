package cs601.sideProject;

public class HomeHandler implements Handler {

    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        if (request.getRequestMethod().equals("GET")) {
            String content = new LoginPageHTML().getLoginPageHTML();
            response.response(content);
        }
    }
}
