package cs601.sideProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogoutHandler implements Handler{
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        String session = request.getHeaders().get("cookie");
        String cookieString = session.split(";")[1];
        String cookie = cookieString.split("=")[1];
        if(request.getRequestMethod().equals("POST")) {
            try(Connection conn = getConnection()){
                PreparedStatement query = conn.prepareStatement("UPDATE User_sessions SET active=0 WHERE session=?");
                query.setString(1,cookie);
                query.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            response.addHeader("location", "/login");
            response.setCode(302);
            response.response("<html>302 Found</html>");
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
