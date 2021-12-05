package cs601.sideProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class HomeHandler implements Handler {

    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        Map<String, String> headers = request.getHeaders();
        if (!headers.containsKey("cookie")  || !headers.get("cookie").contains("session=")) {
            String content = new LoginPageHTML().getLoginPageHTML("Login",getContent());
            response.response(content);
        } else {
            String cookies = headers.get("cookie");
            String sessionCookie = null;
            for (String cookie:cookies.split(";")) {
                if (cookie.trim().startsWith("session=")) {
                    sessionCookie = cookie.split("=")[1];
                }
            }
            if (request.getRequestMethod().equals("GET")) {
                try (Connection conn = getConnection()) {
                    final PreparedStatement query = conn.prepareStatement("select u.username, s.user_id from User_sessions s, User u where s.user_id = u.userId and s.session=? and s.active=1");
                    query.setString(1, sessionCookie);
                    ResultSet resultSet = query.executeQuery();
                    if(!resultSet.next()) {
                        response.addHeader("location", "/login");
                        response.setCode(302);
                        response.response("<html>302 Found</html>");
                        return;
                    }
                    String userName = resultSet.getString("username");
                    int userId = resultSet.getInt("user_id");
                    final PreparedStatement table = conn.prepareStatement("SELECT i.itemID, i.itemName, c.categoryName," +
                            " i.brand, i.price, i.quantity, i.description FROM Item i, Category c WHERE i.user_id=? AND i.categoryID=c.categoryID ORDER BY i.itemID");
                    table.setInt(1, userId);
                    ResultSet tableResultSet = table.executeQuery();
                    String htmlTable = "";
                    if(!tableResultSet.isBeforeFirst()) {
                        htmlTable = "You haven't record any inventory yet.";
                    }
                    else {
                        htmlTable = "<table border='1' cellspacing='0' cellpadding='5'>";
                        int count = 1;
                        htmlTable += "<tr bgcolor='#DCDCDC'><td>" + "N0." + "</td><td>" + "Name " + "</td><td>" + "Category " + "</td><td>" + "Brand " + "</td><td>" + "Price " + "</td><td>" + "Quantity " + "</td><td>" + "Comment " + "</td></tr>";
                        while (tableResultSet.next()) {
                            htmlTable += "<tr>";
                            htmlTable += "<td>" + count++ + "</td>";
                            htmlTable += "<td>" + "<a href=\"/update?itemID=" + tableResultSet.getInt("itemID") +
                                    "\">" + tableResultSet.getString("itemName") + "</a></td>";
                            htmlTable += "<td>" + tableResultSet.getString("categoryName") + "</td>";
                            htmlTable += "<td>" + tableResultSet.getString("brand") + "</td>";
                            htmlTable += "<td>" + tableResultSet.getDouble("price") + "</td>";
                            htmlTable += "<td>" + tableResultSet.getInt("quantity") + "</td>";
                            htmlTable += "<td>" + tableResultSet.getString("description") + "</td>";
                            htmlTable += "</tr>";
                        }
                        htmlTable += "</table>";
                    }
                    String reminder = getReminder(conn);
                    String content = new HomePageHTML().getHomePageHTML(userName, "Current Inventory", htmlTable,reminder);
                    response.response(content);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            else if(request.getRequestMethod().equals("POST")){
                try (Connection conn = getConnection()) {
                    final PreparedStatement query = conn.prepareStatement("select u.username, s.user_id from User_sessions s, User u where s.user_id = u.userId and s.session=?");
                    query.setString(1, sessionCookie);
                    ResultSet resultSet = query.executeQuery();
                    resultSet.next();
                    String userName = resultSet.getString("username");
                    int userId = resultSet.getInt("user_id");

                    String[] contentParts = request.getContent().split("=");
                    if (contentParts.length == 1) {
                        String reminder = getReminder(conn);
                        String content = new HomePageHTML().getHomePageHTML(userName, "Alert: Please enter for searching.","", reminder);
                        response.response(content);
                    }
                    else {
                        final PreparedStatement result = conn.prepareStatement("SELECT i.itemID, i.itemName, c.categoryName," +
                                " i.brand, i.price, i.quantity, i.description FROM Item i, Category c WHERE i.categoryID = c.categoryID " +
                                "AND i.user_id=? AND (i.itemName LIKE ? OR c.categoryName LIKE ? OR i.brand LIKE ?)" );
                        result.setInt(1,userId);
                        result.setString(2,"%" + contentParts[1] + "%");
                        result.setString(3,"%" + contentParts[1] + "%");
                        result.setString(4,"%" + contentParts[1] + "%");
                        ResultSet SearchSet = result.executeQuery();
                        String htmlTable = "";
                        if(!SearchSet.isBeforeFirst()) {
                            htmlTable = contentParts[1] + " not found.";
                        }
                        else {
                            htmlTable = "<table border='1' cellspacing='0' cellpadding='5'>";
                            int count = 1;
                            htmlTable += "<tr bgcolor='#DCDCDC'><td>" + "N0." + "</td><td>" + "Name " + "</td><td>" + "Category " + "</td><td>" + "Brand " + "</td><td>" + "Price " + "</td><td>" + "Quantity " + "</td><td>" + "Comment " + "</td></tr>";
                            while (SearchSet.next()) {
                                htmlTable += "<tr>";
                                htmlTable += "<td>" + count++ + "</td>";
                                htmlTable += "<td>" + "<a href=\"/update?itemID=" + SearchSet.getInt("itemID") +
                                        "\">" + SearchSet.getString("itemName") + "</a></td>";
                                htmlTable += "<td>" + SearchSet.getString("categoryName") + "</td>";
                                htmlTable += "<td>" + SearchSet.getString("brand") + "</td>";
                                htmlTable += "<td>" + SearchSet.getDouble("price") + "</td>";
                                htmlTable += "<td>" + SearchSet.getInt("quantity") + "</td>";
                                htmlTable += "<td>" + SearchSet.getString("description") + "</td>";
                                htmlTable += "</tr>";
                            }
                            htmlTable += "</table>";
                        }
                        String reminder = getReminder(conn);
                        String content = new HomePageHTML().getHomePageHTML(userName, "Search Results:", htmlTable, reminder);
                        response.response(content);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }
    }

    private String getReminder(Connection conn) throws SQLException {
        String reminder = "";
        int counterNum = 1;
        final PreparedStatement lastUpdate = conn.prepareStatement("SELECT itemName FROM Item WHERE DATE_SUB(current_timestamp(), interval 30 day) > lastupdate");
        ResultSet lastUpdateSet = lastUpdate.executeQuery();
        if(!lastUpdateSet.isBeforeFirst()) {
            return "No reminders at this time.";
        } else {
            while (lastUpdateSet.next()) {
                reminder += "<br>" + counterNum++ +". " + lastUpdateSet.getString("itemName") + " hasn't been updated for more than 30 days." + "</br>";
            }
        }
        return reminder;
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

    public static String getContent() {
        String html = "<form action='/login' method='post' accept-charset='utf-8'>";
        html += "<div class='form-group'><label for='usrname'><span class='glyphicon glyphicon-user'></span><b>Username<b></label>";
        html += "<input id='username' type='text' class='form-control' id='usrname' name='username' placeholder='Enter email'/>";
        html += "</div><div class='form-group'>";
        html += "<label for='psw'><span class='glyphicon glyphicon-eye-open'></span> Password</label>";
        html += "<input id=\"password\" type=\"password\" class=\"form-control\" id=\"psw\" name=\"password\" placeholder=\"Enter password\"/></div>";
        html += "<button id=\"login\" type=\"submit\" class=\"btn btn-success btn-block\"><span class=\"glyphicon glyphicon-off\"></span> Login</button>";
        html += "<a style=\"font-size: 10px; margin-left: 45px;\" >No account yet?</a>";
        html += "<a href=\"/signup\" style=\"color:#000000; size: 10px; margin-left:5px;\">Sign up</a></form>";
        return html;
    }
}
