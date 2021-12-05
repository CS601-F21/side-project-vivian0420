package cs601.sideProject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpHandler implements Handler{
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        if(request.getRequestMethod().equals("GET")) {
            response.response(new LoginPageHTML().getLoginPageHTML("Sign Up", getContent(request)));
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
                try (Connection conn = getConnection()) {
                    final PreparedStatement insertUser = conn.prepareStatement("Insert INTO User(userName,password) VALUES(?,?)");
                    insertUser.setString(1,userName);
                    insertUser.setString(2,userPassword);
                    insertUser.executeUpdate();
                    response.setCode(302);
                    response.addHeader("location", "/login");
                    response.response("<html>302 Found</html>");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

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

    public static Connection getConnection(){
        try{
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/cs601sideProject";
            String username = "root";
            String password = "2281997163";
            Class.forName(driver);

            Connection con = DriverManager.getConnection(url,username,password);
            return con;

        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
