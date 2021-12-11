package cs601.sideProject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * HomeHandler. Handle display current inventory, display reminder, sorting, searching,etc.
 */
public class HomeHandler implements Handler {

    /**
     * Handle display current inventory, display reminder, sorting, searching,etc.
     * @param request the request that the server received
     * @param response the response that server sent out
     */
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        Map<String, String> headers = request.getHeaders();

        //If the current user haven't login, force the user to the login page.
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
                    //If the user has already log out(active=0), force the user to the login page.
                    if(!resultSet.next()) {
                        response.addHeader("location", "/login");
                        response.setCode(302);
                        response.response("<html>302 Found</html>");
                        return;
                    }

                    //Read sorting keyword from the request that the server received, connect to MuSQL database and do
                    // the corresponding query.Then display the query result.
                    String sort = request.getQueryParam().get("sort");
                    if(sort == null) {
                        sort = "i.itemID";
                    }
                    String userName = resultSet.getString("username");
                    int userId = resultSet.getInt("user_id");
                    final PreparedStatement table = conn.prepareStatement("SELECT i.itemID, i.itemName, c.categoryName," +
                            " i.brand, i.price, i.quantity, i.description FROM Item i, Category c WHERE i.user_id=? AND i.categoryID=c.categoryID ORDER BY " + sort + " desc");
                    table.setInt(1, userId);
                    ResultSet tableResultSet = table.executeQuery();
                    String htmlTable = "";
                    if(!tableResultSet.isBeforeFirst()) {
                        htmlTable += "<h2>Current Inventory</h2>";
                        htmlTable += "<p>You haven't any records yet.</p>";
                    }
                    else {
                        htmlTable += "<h2>Current Inventory</h2>";
                        htmlTable += "<form action='/home?sortby=' method='GET'>";
                        htmlTable += "<label for='sort'>Sort by:</label>";
                        htmlTable += "<select name='sort' id='sort' style='margin-left: 10px;'>";
                        htmlTable += "<option value='select'>-Please select-</option>";
                        htmlTable += "<option value='quantity'>Quantity</option>";
                        htmlTable += "<option value='categoryName'>Category</option>";
                        htmlTable += "<option value='lastupdate'>Date Updated</option></select>";
                        htmlTable += "<input style='margin-left: 10px; margin-bottom: 5px;' type='submit' value='Submit'></form>";
                        htmlTable += "<table border='1' cellspacing='0' cellpadding='5'>";
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
                    String reminder = getReminder(conn,userId);
                    String content = new HomePageHTML().getHomePageHTML(userName, htmlTable, reminder);
                    response.response(content);

                } catch (SQLException | FileNotFoundException throwables) {
                    throwables.printStackTrace();
                }
            }

            //display search results
            else if(request.getRequestMethod().equals("POST")){
                try (Connection conn = getConnection()) {
                    final PreparedStatement query = conn.prepareStatement("select u.username, s.user_id from User_sessions s, User u where s.user_id = u.userId and s.session=?");
                    query.setString(1, sessionCookie);
                    ResultSet resultSet = query.executeQuery();
                    resultSet.next();
                    String userName = resultSet.getString("username");
                    int userId = resultSet.getInt("user_id");

                    String contentParts = request.getFormData().get("search");
                    if (contentParts == null) {
                        String reminder = getReminder(conn, userId);
                        String html = "<p>Please enter for searching.</p>";
                        String content = new HomePageHTML().getHomePageHTML(userName,html, reminder);
                        response.response(content);
                    }
                    else {
                        final PreparedStatement result = conn.prepareStatement("SELECT i.itemID, i.itemName, c.categoryName," +
                                " i.brand, i.price, i.quantity, i.description FROM Item i, Category c WHERE i.categoryID = c.categoryID " +
                                "AND i.user_id=? AND (i.itemName LIKE ? OR c.categoryName LIKE ? OR i.brand LIKE ?)" );
                        result.setInt(1,userId);
                        result.setString(2,"%" + contentParts + "%");
                        result.setString(3,"%" + contentParts + "%");
                        result.setString(4,"%" + contentParts + "%");
                        ResultSet SearchSet = result.executeQuery();
                        String htmlTable = "";
                        if(!SearchSet.isBeforeFirst()) {
                            htmlTable = contentParts + " not found.";
                        }
                        else {
                            htmlTable = "<table border='1' cellspacing='0' cellpadding='5'>";
                            int count = 1;
                            htmlTable += "<h3>Search Results:</h3>";
                            htmlTable += "<tr bgcolor='#DCDCDC'><td>" + "N0." + "</td><td>" + "Name " + "</td><td>" + "Category " + "</td><td>" + "Brand " + "</td><td>" + "Price " + "</td><td>" + "Quantity " + "</td><td>" + "Comment " + "</td></tr>";
                            while (SearchSet.next()) {
                                htmlTable += "<tr>";
                                htmlTable += "<td>" + count++ + "</td>";
                                htmlTable += "<td>" + "<a href='update?itemID=" + SearchSet.getInt("itemID") +
                                        "'>" + SearchSet.getString("itemName") + "</a></td>";
                                htmlTable += "<td>" + SearchSet.getString("categoryName") + "</td>";
                                htmlTable += "<td>" + SearchSet.getString("brand") + "</td>";
                                htmlTable += "<td>" + SearchSet.getDouble("price") + "</td>";
                                htmlTable += "<td>" + SearchSet.getInt("quantity") + "</td>";
                                htmlTable += "<td>" + SearchSet.getString("description") + "</td>";
                                htmlTable += "</tr>";
                            }
                            htmlTable += "</table>";
                        }
                        String reminder = getReminder(conn,userId);
                        String content = new HomePageHTML().getHomePageHTML(userName, htmlTable, reminder);
                        response.response(content);
                    }
                } catch (SQLException | FileNotFoundException throwables) {
                    throwables.printStackTrace();
                }

            }
        }
    }

    /**
     * It an item's quantity is less or equal to 0, or it hasn't been modified for more than 30 days, display this
     * item to the user
     * @param conn connection
     * @param userId current user id
     * @return the content of reminder
     * @throws SQLException exception when it has problems to connect to MySQL
     */
    private String getReminder(Connection conn, int userId) throws SQLException {
        String reminder = "";
        int counterNum = 1;
        final PreparedStatement lastUpdate = conn.prepareStatement("SELECT itemID,itemName FROM Item WHERE user_id=? AND DATE_SUB(current_timestamp(), interval 30 day) > lastupdate");
        lastUpdate.setInt(1,userId);
        ResultSet lastUpdateSet = lastUpdate.executeQuery();
        final PreparedStatement quantity = conn.prepareStatement("SELECT itemID,itemName,quantity FROM Item WHERE user_id=? AND quantity <= 0 ");
        quantity.setInt(1,userId);
        ResultSet quantitySet = quantity.executeQuery();
        if(!lastUpdateSet.isBeforeFirst() && !quantitySet.isBeforeFirst()) {
            return "No reminders at this time.";
        } else {
            while(quantitySet.next()) {
                reminder += "<br>" + counterNum++ +". " + "<a href='update?itemID=" + quantitySet.getInt("itemID") + "'>" + quantitySet.getString("itemName") + "</a>" + " 's quantity is " + quantitySet.getString("quantity") + "." +"</br>";
            }
            while (lastUpdateSet.next()) {
                reminder += "<br>" + counterNum++ +". " + "<a href='update?itemID=" + lastUpdateSet.getInt("itemID") + "'>" + lastUpdateSet.getString("itemName") + "</a>" + " hasn't been updated for more than 30 days." + "</br>";
            }
        }
        return reminder;
    }

    /**
     * Read MySQL information from config and connect to MySQL.
     * @return the connection that connection this application to MySQL
     * @throws FileNotFoundException exception of file not found
     */
    public static Connection getConnection() throws FileNotFoundException {
        Gson gson = new Gson();
        JsonObject config = gson.fromJson(new FileReader("config.json"),JsonObject.class);
        try{
            String driver = "com.mysql.jdbc.Driver";
            String url = config.get("url").getAsString();
            String username = config.get("username").getAsString();
            String password = config.get("password").getAsString();
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

    /**
     * content of login form
     * @return html for login form
     */
    public static String getContent() {
        String html = "<form action='/login' method='post' accept-charset='utf-8'>";
        html += "<div class='form-group'><label for='usrname'><span class='glyphicon glyphicon-user'></span><b>Username<b></label>";
        html += "<input id='username' type='text' class='form-control' id='usrname' name='username' placeholder='Enter email'/>";
        html += "</div><div class='form-group'>";
        html += "<label for='psw'><span class='glyphicon glyphicon-eye-open'></span> Password</label>";
        html += "<input id='password' type='password' class='form-control' id='psw' name='password' placeholder='Enter password'/></div>";
        html += "<button id='login' type='submit' class='btn btn-success btn-block'><span class='glyphicon glyphicon-off'></span> Login</button>";
        html += "<a style='font-size: 10px; margin-left: 45px;' >No account yet?</a>";
        html += "<a href='/signup' style='color:#000000; size: 10px; margin-left:5px;'>Sign up</a></form>";
        return html;
    }
}
