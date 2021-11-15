package cs601.sideProject;

public class SearchHandler implements Handler{
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        System.out.println(request.getContent());
    }
}
