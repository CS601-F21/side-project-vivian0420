package cs601.sideProject;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * LogoutHandler. Handle users' log out behavior
 */
public class LogoutHandler implements Handler{

    /**
     * When users click log out button, set the current user's active as 0 and redirect the user to the login page.
     * @param request  The HTTP request that the server receives
     * @param response The HTTP response that the server sends
     */
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        String session = request.getHeaders().get("cookie");
        String cookieString = session.split(";")[1];
        String cookie = cookieString.split("=")[1];
        if(request.getRequestMethod().equals("POST")) {
            try(Connection conn = HomeHandler.getConnection()){
                PreparedStatement query = conn.prepareStatement("UPDATE User_sessions SET active=0 WHERE session=?");
                query.setString(1,cookie);
                query.executeUpdate();
            } catch (SQLException | FileNotFoundException throwables) {
                throwables.printStackTrace();
            }
            response.addHeader("location", "/login");
            response.setCode(302);
            response.response("<html>302 Found</html>");
        }
    }
}
