package cs601.sideProject;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

/**
 * LoginHandler. Handle users' login behavior
 */
public class LoginHandler implements Handler {

    /**
     * Display login page to users. When user login, create user-session for the current user and redirect the user to the home page.
     *
     * @param request  The HTTP request that the server receives
     * @param response The HTTP response that the server sends
     */
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        if (request.getRequestMethod().equals("GET")) {
            Map<String, String> headers = request.getHeaders();
            if (!headers.containsKey("cookie") || !headers.get("cookie").contains("session=")) {
                String content = new LoginPageHTML().getLoginPageHTML("Login", HomeHandler.getContent());
                response.response(content);
            } else {
                String cookies = headers.get("cookie");
                String sessionCookie = null;
                for (String cookie : cookies.split(";")) {
                    if (cookie.trim().startsWith("session=")) {
                        sessionCookie = cookie.split("=")[1];
                    }
                }
                try (Connection conn = HomeHandler.getConnection()) {
                    final PreparedStatement query = conn.prepareStatement("SELECT user_id FROM User_sessions WHERE session=? AND active=1");
                    query.setString(1, sessionCookie);
                    ResultSet result = query.executeQuery();
                    if (result.next()) {
                        response.setCode(302);
                        response.addHeader("location", "/home");
                        response.response("<html>302 Found</html>");
                    } else {
                        String content = new LoginPageHTML().getLoginPageHTML("Login", HomeHandler.getContent());
                        response.response(content);
                    }
                } catch (SQLException | FileNotFoundException throwables) {
                    throwables.printStackTrace();
                }
            }

        }
        if (request.getRequestMethod().equals("POST")) {
            String user = request.getContent().split("&")[0];
            String passW = request.getContent().split("&")[1];
            String userName = user.split("=")[1];
            String password = passW.split("=")[1];
            try (Connection conn = HomeHandler.getConnection()) {

                final PreparedStatement query = conn.prepareStatement("SELECT userID FROM User where userName =? and password =? ");
                query.setString(1, userName);
                query.setString(2, password);
                ResultSet result = query.executeQuery();
                if (result.next()) {
                    int userID = result.getInt("userID");
                    final String session = String.valueOf(UUID.randomUUID());
                    final PreparedStatement sessionQuery = conn.prepareStatement("INSERT INTO User_sessions(session,user_id) VALUES(?, ?) ");
                    sessionQuery.setString(1, session);
                    sessionQuery.setInt(2, userID);
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
