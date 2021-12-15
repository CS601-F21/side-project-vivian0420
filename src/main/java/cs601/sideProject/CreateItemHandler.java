package cs601.sideProject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * CreateItemHandler. Allow users to create new items and store the information of items into database.
 */
public class CreateItemHandler implements Handler{

    /**
     * Read data from users' input and store the information of items into database
     * @param request the request that the server received
     * @param response the response that server sent out
     */
    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        if(request.getRequestMethod().equals("GET")) {
            response.setCode(405);
            response.response("Method not allowed.");
            return;
        }
        String sessionCookie = null;
        Map<String, String> headers = request.getHeaders();
        //if the current user haven't login, force the user to the login page.
        if (!headers.containsKey("cookie")  || !headers.get("cookie").contains("session=")) {
            response.setCode(302);
            response.addHeader("location", "/login");
            response.response("<html>302 found</html>");
            return;
        } else {
            String cookies = headers.get("cookie");
            for (String cookie : cookies.split(";")) {
                if (cookie.trim().startsWith("session=")) {
                    sessionCookie = cookie.split("=")[1];
                }
            }
        }
        //Read the data from the create new item form.
        String itenName = new Gson().fromJson(request.getContent(),JsonObject.class).get("itemname").getAsString();
        String brand = new Gson().fromJson(request.getContent(),JsonObject.class).get("brand").getAsString();
        String category = new Gson().fromJson(request.getContent(),JsonObject.class).get("category").getAsString();
        double price = new Gson().fromJson(request.getContent(),JsonObject.class).get("price").getAsDouble();
        int qty = new Gson().fromJson(request.getContent(), JsonObject.class).get("quantity").getAsInt();
        String comment = new Gson().fromJson(request.getContent(),JsonObject.class).get("comment").getAsString();

        //Connect MySQL database
        try(Connection con = HomeHandler.getConnection();){

            PreparedStatement query = con.prepareStatement("select categoryID from category where categoryName = ?");
            query.setString(1,category);
            ResultSet resultSet = query.executeQuery();
            resultSet.next();
            int categeoryId = resultSet.getInt("categoryID");

            PreparedStatement userIdQuery = con.prepareStatement("SELECT user_id FROM User_sessions WHERE session=?");
            userIdQuery.setString(1,sessionCookie);
            ResultSet userIdResult = userIdQuery.executeQuery();
            userIdResult.next();
            int userId = userIdResult.getInt("user_id");

            String insertContactSql = "INSERT INTO Item (itemName, brand, categoryID, price, quantity, description, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement posted2 = con.prepareStatement(insertContactSql);
            posted2.setString(1,itenName);
            posted2.setString(2,brand);
            posted2.setInt(3, categeoryId);
            posted2.setDouble(4, price);
            posted2.setInt(5, qty);
            posted2.setString(6, comment);
            posted2.setInt(7,userId);
            posted2.executeUpdate();
        } catch (SQLException | FileNotFoundException throwables) {
            throwables.printStackTrace();
        }
        JsonObject json = new JsonObject();
        json.addProperty("test", "success");
        response.response(json.toString());
    }
}
