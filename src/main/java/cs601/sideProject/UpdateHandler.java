package cs601.sideProject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Callable;

public class UpdateHandler implements Handler {
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        try (Connection conn = getConnection()) {
            String userName;
            Map<String, String> headers = request.getHeaders();
            if (!headers.containsKey("cookie")) {
                String content = new LoginPageHTML().getLoginPageHTML();
                response.response(content);
                return;
            } else {
                String sessionS = headers.get("cookie");
                String sessionString = sessionS.split("=")[1];
                final PreparedStatement query = conn.prepareStatement("select u.username from User_sessions s, User u where s.user_id = u.userId and s.session=?");
                query.setString(1, sessionString);
                ResultSet resultSet = query.executeQuery();
                if (!resultSet.next()) {
                    response.response(new LoginPageHTML().getLoginPageHTML());
                    return;
                }
                userName = resultSet.getString("username");
            }
            if (request.getRequestMethod().equals("GET")) {
                int id = Integer.parseInt(request.getQueryParam().get("itemID"));
                String content = getContent(userName, id, conn);
                response.response(content);
            } else if (request.getRequestMethod().equals("POST")) {
                if (request.getFormData().get("formAction").equals("UPDATE")) {
                    String[] strings = new String[0];
                    try {
                        strings = URLDecoder.decode(request.getContent(), StandardCharsets.UTF_8.name()).split("&");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String itemName = strings[0].split("=")[1];
                    int categoryID = Integer.parseInt(strings[1].split("=")[1]);
                    String brand = strings[2].split("=")[1];
                    double price = Double.parseDouble(strings[3].split("=")[1]);
                    int quantity = Integer.parseInt(strings[4].split("=")[1]);
                    int itemID = Integer.parseInt(strings[6].split("=")[1]);
                    PreparedStatement statement = conn.prepareStatement("UPDATE Item SET itemName=?,categoryID=?,brand=?,price=?,quantity=quantity+?,description=? WHERE itemID=?");
                    statement.setString(1, itemName);
                    statement.setInt(2, categoryID);
                    statement.setString(3, brand);
                    statement.setDouble(4, price);
                    statement.setInt(5, quantity);
                    if(strings[5].split("=").length == 2) {
                        statement.setString(6, strings[5].split("=")[1]);
                    } else {
                        statement.setString(6, "");
                    }

                    statement.setInt(7, itemID);
                    statement.executeUpdate();
                    String content = getContent(userName, itemID, conn);
                    response.response(content);
                } else if (request.getFormData().get("formAction").equals("DELETE")) {
                    int itemID = Integer.parseInt(request.getFormData().get("itemID"));
                    PreparedStatement delete = conn.prepareStatement("delete from Item where itemID = ?");
                    delete.setInt(1, itemID);
                    delete.execute();
                    response.setCode(302);
                    response.addHeader("location", "/home");
                    response.response("<html>302 removed</html>");
                } else {
                    response.setCode(400);
                    response.response("<html>invalid formAction" + request.getFormData().get("formAction") + "<html>");
                }

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private String getContent(String userName, int id, Connection conn) throws SQLException {
        final PreparedStatement queryItem = conn.prepareStatement("SELECT i.itemName, i.itemID, c.categoryName, c.categoryID, i.brand, i.price, i.quantity, i.description FROM Item i, Category c WHERE c.categoryID = i.categoryID AND i.itemID=?");
        queryItem.setInt(1, id);
        ResultSet itemResultSet = queryItem.executeQuery();
        String htmlItem = "<table>";
        itemResultSet.next();
        htmlItem += "<form action='/update' method='post' accept-charset='utf-8'><tr>" + "<td>" + "Name: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getString("itemName") + "' name='itemName'/></td>" + "</tr>";
        htmlItem += "<tr>" + "<td>" + "Category: " + "</td><td>" + select(itemResultSet.getInt("categoryID"), conn) + "</td></tr>";
        htmlItem += "<tr>" + "<td>" + "Brand: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getString("brand") + "' name='brand'/></td>" + "</tr>";
        htmlItem += "<tr>" + "<td>" + "Price: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getDouble("price") + "' name='price'/></td>" + "</tr>";
        htmlItem += "<tr>" + "<td>" + "Quantity: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getInt("quantity") + "' name='quantity'/></td>" + "</tr>";
        htmlItem += "<tr>" + "<td>" + "Comment: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getString("description") + "' name='description'/></td>" + "</tr>";
        htmlItem += "<input type='hidden' name='itemID' value='" + itemResultSet.getInt("itemID") + "' />";
        htmlItem += "<input type='hidden' name='formAction' id='formAction' value='UPDATE'/>";
        htmlItem += "<tr><td><button id='update' type='submit' onclick='form_update()'>Update</button></td>";
        htmlItem += "<td><button id='delete' type='submit' onclick='form_delete()'>Delete</button></td></tr></form>";
        htmlItem += "</table>";
        String content = new HomePageHTML().getHomePageHTML(userName, itemResultSet.getString("itemName") + ":", htmlItem);
        return content;
    }

    public static Connection getConnection() {
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/cs601sideProject";
            String username = "root";
            String password = "2281997163";
            Class.forName(driver);

            Connection con = DriverManager.getConnection(url, username, password);
            return con;

        } catch (ClassNotFoundException e) {
            System.out.println(e);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String select(int categoryId, Connection con) throws SQLException {
        final PreparedStatement query = con.prepareStatement("SELECT categoryName, categoryID FROM Category");
        ResultSet categorySet = query.executeQuery();
        String html = "<select name='category'>";
        while (categorySet.next()) {
            if (categorySet.getInt("categoryID") == categoryId) {
                html += "<option selected value='" + categorySet.getString("categoryID") + "'> " + categorySet.getString("categoryName") + "</option>";
            } else {
                html += "<option value='" + categorySet.getString("categoryID") + "'> " + categorySet.getString("categoryName") + "</option>";
            }

        }
        html += "</select>";
        return html;
    }
}
