package cs601.sideProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class HomeHandler implements Handler {

    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        Map<String, String> headers = request.getHeaders();
        if (!headers.containsKey("cookie")) {
            String content = new LoginPageHTML().getLoginPageHTML();
            response.response(content);
        } else {
            String sessionS = headers.get("cookie");
            String sessionString = sessionS.split("=")[1];
            if (request.getRequestMethod().equals("GET")) {
                try (Connection conn = getConnection()) {
                    final PreparedStatement query = conn.prepareStatement("select u.username from User_sessions s, User u where s.user_id = u.userId and s.session=?");
                    query.setString(1, sessionString);
                    ResultSet resultSet = query.executeQuery();
                    resultSet.next();
                    String userName = resultSet.getString("username");
                    String content = new HomePageHTML().getHomePageHTML(userName);
                    response.response(content);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
            if (request.getRequestMethod().equals("POST")) {

                String user = request.getContent().split("&")[0];
                String passW = request.getContent().split("&")[1];
                String userName = user.split("=")[1];
                String password = passW.split("=")[1];
                try (Connection conn = getConnection()) {

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
                        String content = new HomePageHTML().getHomePageHTML(userName);
                        response.response(content);
                    } else {
                        String content = new LoginPageHTML().getLoginPageHTML();
                        response.response(content + "\n" + "<p> Invalid userName and/or password.</p>");
                    }


                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

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
