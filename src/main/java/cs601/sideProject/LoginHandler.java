package cs601.sideProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LoginHandler implements Handler{
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        if (request.getRequestMethod().equals("GET")) {
            String content = new LoginPageHTML().getLoginPageHTML();
            response.response(content);
        }
        if(request.getRequestMethod().equals("POST")) {

            String user = request.getContent().split("&")[0];
            String passW = request.getContent().split("&")[1];
            String userName = user.split("=")[1];
            String password = passW.split("=")[1];
            try(Connection conn = getConnection()){

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
                    String content = new LoginPageHTML().getLoginPageHTML();
                    response.response(content + "\n" + "<p> Invalid userName and/or password.</p>");
                }


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
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
