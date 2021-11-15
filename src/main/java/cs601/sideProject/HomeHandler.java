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
                    final PreparedStatement query = conn.prepareStatement("select u.username from User_sessions s, User u where s.user_id = u.userId and s.session=? ");
                    query.setString(1, sessionString);
                    ResultSet resultSet = query.executeQuery();
                    resultSet.next();
                    String userName = resultSet.getString("username");

                    final PreparedStatement table = conn.prepareStatement("SELECT i.itemID, i.itemName, c.categoryName," +
                            " i.brand, i.price, i.quantity, i.description FROM Item i, Category c WHERE i.categoryID=c.categoryID ORDER BY i.itemID");
                    ResultSet tableResultSet = table.executeQuery();
                    String htmlTable = "<table border='1' cellspacing='0' cellpadding='0'>";
                    int count = 1;
                    htmlTable += "<tr bgcolor='#DCDCDC'><td>" + "N0." + "</td><td>" + "Name " + "</td><td>" + "Category " + "</td><td>" + "Brand " + "</td><td>" + "Price " + "</td><td>" + "Quantity " + "</td><td>" + "Comment " + "</td></tr>";
                    while(tableResultSet.next()) {
                        htmlTable += "<tr>";
                        htmlTable +="<td>" + count++ + "</td>";
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
                    String content = new HomePageHTML().getHomePageHTML(userName, "Current Inventory", htmlTable);
                    response.response(content);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            else if(request.getRequestMethod().equals("POST")){
                try (Connection conn = getConnection()) {
                    final PreparedStatement query = conn.prepareStatement("select u.username from User_sessions s, User u where s.user_id = u.userId and s.session=?");
                    query.setString(1, sessionString);
                    ResultSet resultSet = query.executeQuery();
                    resultSet.next();
                    String userName = resultSet.getString("username");

                    String[] contentParts = request.getContent().split("=");
                    if (contentParts.length == 1) {
                        String content = new HomePageHTML().getHomePageHTML(userName, "Alert: Please enter for searching.","");
                        response.response(content);
                    }
                    else {
                        final PreparedStatement result = conn.prepareStatement("SELECT i.itemID, i.itemName, c.categoryName," +
                                " i.brand, i.price, i.quantity, i.description FROM Item i, Category c WHERE i.categoryID = c.categoryID " +
                                "AND (i.itemName LIKE ? OR c.categoryName LIKE ? OR i.brand LIKE ?)" );
                        result.setString(1,"%" + contentParts[1] + "%");
                        result.setString(2,"%" + contentParts[1] + "%");
                        result.setString(3,"%" + contentParts[1] + "%");
                        ResultSet SearchSet = result.executeQuery();
                        String htmlTable = "<table border='1' cellspacing='0' cellpadding='0'>";
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
                        String content = new HomePageHTML().getHomePageHTML(userName, "Search Results:", htmlTable);
                        response.response(content);
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
