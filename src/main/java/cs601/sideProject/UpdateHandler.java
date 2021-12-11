package cs601.sideProject;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * UpdateHandler. Allow users to update the items' information
 */
public class UpdateHandler implements Handler {

    /**
     * Allow users to check the information of items and update/delete items.
     * @param request  The HTTP request that the server receives
     * @param response The HTTP response that the server sends
     */
    @Override
    public void handle(ServerRequest request, ServerResponse response) {

        try (Connection conn = HomeHandler.getConnection()) {
            String userName;
            int userID;
            Map<String, String> headers = request.getHeaders();
            if (!headers.containsKey("cookie")) {
                String content = new LoginPageHTML().getLoginPageHTML("Login", HomeHandler.getContent());
                response.response(content);
                return;
            } else {
                String sessionS = headers.get("cookie");
                String sessionString = sessionS.split(";")[1];
                String session = sessionString.split("=")[1];
                final PreparedStatement query = conn.prepareStatement("select s.user_id,u.username from User_sessions s, User u where s.user_id = u.userId and s.session=?");
                query.setString(1, session);
                ResultSet resultSet = query.executeQuery();
                if (!resultSet.next()) {
                    response.response(new LoginPageHTML().getLoginPageHTML("Login", HomeHandler.getContent()));
                    return;
                }
                userName = resultSet.getString("username");
                userID = resultSet.getInt("user_id");
            }

            //display items' information
            if (request.getRequestMethod().equals("GET")) {
                int id = Integer.parseInt(request.getQueryParam().get("itemID"));
                String content = getContent(userName, id, userID, conn);
                response.response(content);
            }

            //allow users to update the information of items
            else if (request.getRequestMethod().equals("POST")) {
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
                    int transaction = Integer.parseInt(strings[5].split("=")[1]);
                    int itemID = Integer.parseInt(strings[7].split("=")[1]);

                    PreparedStatement statement = conn.prepareStatement("UPDATE Item SET itemName=?,categoryID=?,brand=?,price=?,quantity=?+?,description=?, lastupdate = current_timestamp() WHERE itemID=?");
                    statement.setString(1, itemName);
                    statement.setInt(2, categoryID);
                    statement.setString(3, brand);
                    statement.setDouble(4, price);
                    statement.setInt(5, quantity);
                    statement.setInt(6,transaction);
                    if(strings[6].split("=").length == 2) {
                        statement.setString(7, strings[6].split("=")[1]);
                    } else {
                        statement.setString(7, "");
                    }

                    statement.setInt(8, itemID);
                    statement.executeUpdate();
                    response.setCode(302);
                    response.addHeader("location", "/home");
                    response.response("<html>302 removed</html>");
                }

                //Allow users to delete items
                else if (request.getFormData().get("formAction").equals("DELETE")) {
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
        } catch (SQLException | FileNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * display items' information
     * @param userName the current user's userName
     * @param itemId the current item's id
     * @param userID the user id of current user
     * @param conn connection
     * @return the content that display the information of t current item and reminder
     * @throws SQLException exception when it has problems to connect to MySQL
     */
    private String getContent(String userName, int itemId, int userID, Connection conn) throws SQLException {
        final PreparedStatement queryItem = conn.prepareStatement("SELECT i.itemName, i.itemID, c.categoryName, c.categoryID, i.brand, i.price, i.quantity, i.description FROM Item i, Category c WHERE c.categoryID = i.categoryID AND i.itemID=?");
        queryItem.setInt(1, itemId);
        ResultSet itemResultSet = queryItem.executeQuery();
        String htmlItem = "<table>";
        itemResultSet.next();
        htmlItem += "<form action='/update' method='post' accept-charset='utf-8'><tr>" + "<td>" + "Name: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getString("itemName") + "' name='itemName'/></td>" + "</tr>";
        htmlItem += "<tr>" + "<td>" + "Category: " + "</td><td>" + select(itemResultSet.getInt("categoryID"), conn) + "</td></tr>";
        htmlItem += "<tr>" + "<td>" + "Brand: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getString("brand") + "' name='brand'/></td>" + "</tr>";
        htmlItem += "<tr>" + "<td>" + "Price: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getDouble("price") + "' name='price'/></td>" + "</tr>";
        htmlItem += "<tr>" + "<td>" + "Quantity: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getInt("quantity") + "' name='quantity'/></td>" + "</tr>";
        htmlItem += "<tr>" + "<td>" + "Transaction: " + "</td>" + "<td><input type='text' value='0'name='transaction'/></td>" + "</tr>";
        htmlItem += "<tr>" + "<td>" + "Comment: " + "</td>" + "<td><input type='text' value='" + itemResultSet.getString("description") + "' name='description'/></td>" + "</tr>";
        htmlItem += "<input type='hidden' name='itemID' value='" + itemResultSet.getInt("itemID") + "' />";
        htmlItem += "<input type='hidden' name='formAction' id='formAction' value='UPDATE'/>";
        htmlItem += "<tr><td><button id='update' type='submit' onclick='form_update()'>Update</button></td>";
        htmlItem += "<td><button id='delete' type='submit' onclick='form_delete()'>Delete</button></td></tr></form>";
        htmlItem += "</table>";

        String reminder = "";
        int counterNum = 1;
         final PreparedStatement lastUpdate = conn.prepareStatement("SELECT itemID,itemName FROM Item WHERE user_id=? AND DATE_SUB(current_timestamp(), interval 30 day) > lastupdate ");
         lastUpdate.setInt(1,userID);
         ResultSet lastUpdateSet = lastUpdate.executeQuery();
         final PreparedStatement quantity = conn.prepareStatement("SELECT itemID,itemName,quantity FROM Item WHERE user_id=? AND quantity <= 0 ");
         quantity.setInt(1,userID);
         ResultSet quantitySet = quantity.executeQuery();
         if(!lastUpdateSet.isBeforeFirst() && !quantitySet.isBeforeFirst()) {
             reminder = "No reminders at this time.";
         } else {
             while(quantitySet.next()) {
                 reminder += "<br>" + counterNum++ +". " + "<a href='update?itemID=" + quantitySet.getInt("itemID") + "'>" + quantitySet.getString("itemName") + "</a>" + " 's quantity is " + quantitySet.getString("quantity") + "." +"</br>";
             }
             while (lastUpdateSet.next()) {
                 reminder += "<br>" + counterNum++ +". " + "<a href='update?itemID=" + lastUpdateSet.getInt("itemID") + "'>" + lastUpdateSet.getString("itemName") + "</a>" + " hasn't been updated for more than 30 days." + "</br>";
             }
         }

        String content = new HomePageHTML().getHomePageHTML(userName, htmlItem, reminder);
        return content;
    }

    /**
     * Sorting form html
     * @param categoryId items' category id
     * @param con connection
     * @return the html of sorting form
     * @throws SQLException exception when it has problems to connect to MySQL
     */
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
