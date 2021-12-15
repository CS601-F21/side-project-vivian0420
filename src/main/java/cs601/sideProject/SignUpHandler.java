package cs601.sideProject;

import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * SignUpHandler. Hnadle users' sign up behavior
 */
public class SignUpHandler implements Handler{

    /**
     * Display sign up page to users. When users sign up, check is the passwords are matching. If is, redirect the suer to
     * the login page. Otherwise, display error alert and allow users to re-enter.
     * @param request  The HTTP request that the server receives
     * @param response The HTTP response that the server sends
     */
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        if(request.getRequestMethod().equals("GET")) {
            Map<String, String> headers = request.getHeaders();
            if (!headers.containsKey("cookie") || !headers.get("cookie").contains("session=")) {
                response.response(new LoginPageHTML().getLoginPageHTML("Sign Up", getContent(request)));
            }
            else{
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
                    }else {
                        response.response(new LoginPageHTML().getLoginPageHTML("Sign Up", getContent(request)));
                    }
                } catch (SQLException | FileNotFoundException throwables) {
                    throwables.printStackTrace();
                }
                }
        }
        else if(request.getRequestMethod().equals("POST")) {
            String user = request.getContent().split("&")[0];
            String password = request.getContent().split("&")[1];
            String reenterPassword = request.getContent().split("&")[2];
            String userName = user.split("=")[1];
            String userPassword = password.split("=")[1];
            String userRePassword = reenterPassword.split("=")[1];
            if (!userPassword.equals(userRePassword)) {
                response.setCode(302);
                response.addHeader("location", "/signup?error=" + URLEncoder.encode("Password not matched", StandardCharsets.UTF_8));
                response.response("<html>302 Found</html>");
            } else {
                try (Connection conn = HomeHandler.getConnection()) {
                    final PreparedStatement insertUser = conn.prepareStatement("Insert INTO User(userName,password) VALUES(?,?)");
                    insertUser.setString(1,userName);
                    insertUser.setString(2,userPassword);
                    insertUser.executeUpdate();
                    response.setCode(302);
                    response.addHeader("location", "/login");
                    response.response("<html>302 Found</html>");
                } catch (SQLException | FileNotFoundException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param request
     * @return
     */
    public String getContent(ServerRequest request) {

        String html = "<form action='/signup' method='post' accept-charset='utf-8'>";
        html += "<div class='form-group'>";
        if(request.getQueryParam().containsKey("error")) {
            html += "<p style='background-color:tomato;'>Password not matched</p>";
        }
        html += "<label for='usrname'><span class='glyphicon glyphicon-user'></span><b>Username<b></label>";
        html += "<input id='username' type='text' class='form-control' id='usrname' name='username' placeholder='Enter Username'/>";
        html += "</div><div class='form-group'>";
        html += "<label for='psw'><span class='glyphicon glyphicon-eye-open'></span> Password</label>";
        html += "<input id='password' type='password' class='form-control' id='psw' name='password' placeholder='Enter password'/>";
        html += "</div><div class='form-group'>";
        html += "<label for='psw'><span class='glyphicon glyphicon-eye-open'></span> Confirm Password</label>";
        html += "<input id='password' type='password' class='form-control' id='re_psw' name='re_password' placeholder='Enter password again'/></div>";
        html += "<button id='confirm' type='submit' class='btn btn-success btn-block'><span class='glyphicon glyphicon-off'></span> Confirm</button></form>";
        return html;
    }
}
