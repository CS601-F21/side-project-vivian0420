package cs601.sideProject;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 *
 */
public class LoginHandler implements Handler{

    /**
     *
     * @param request
     * @param response
     */
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        if (request.getRequestMethod().equals("GET")) {
            String content = new LoginPageHTML().getLoginPageHTML("Login",HomeHandler.getContent());
            response.response(content);
        }
        if(request.getRequestMethod().equals("POST")) {
            String user = request.getContent().split("&")[0];
            String passW = request.getContent().split("&")[1];
            String userName = user.split("=")[1];
            String password = passW.split("=")[1];
            try(Connection conn = HomeHandler.getConnection()){

                final PreparedStatement query = conn.prepareStatement("SELECT userID FROM User where userName =? and password =? ");
                query.setString(1,userName);
                query.setString(2, password);
                ResultSet result = query.executeQuery();
                if(result.next()){
                    int userID = result.getInt("userID");
                    final String session = String.valueOf(UUID.randomUUID());
                    final PreparedStatement sessionQuery = conn.prepareStatement("INSERT INTO User_sessions(session,user_id) VALUES(?, ?) ");
                    sessionQuery.setString(1,session);
                    sessionQuery.setInt(2,userID);
                    sessionQuery.execute();
                    response.addHeader("set-cookie", "session=" + session);
                    response.addHeader("location", "/home");
                    response.setCode(302);
                    response.response("<html>302 Found</html>");
                } else {
                    String content = new LoginPageHTML().getLoginPageHTML("Login", HomeHandler.getContent());
                    response.response(content + "\n" + "<p style='text-align: center; color: red;'> Invalid userName and/or password.</p>");
                }
            } catch (SQLException | FileNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
