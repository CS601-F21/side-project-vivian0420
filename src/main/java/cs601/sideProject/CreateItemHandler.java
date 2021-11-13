package cs601.sideProject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateItemHandler implements Handler{

    @Override
    public void handle(ServerRequest request, ServerResponse response) {
        String itenName = new Gson().fromJson(request.getContent(),JsonObject.class).get("itemname").getAsString();
        String brand = new Gson().fromJson(request.getContent(),JsonObject.class).get("brand").getAsString();
        String category = new Gson().fromJson(request.getContent(),JsonObject.class).get("category").getAsString();
        double price = new Gson().fromJson(request.getContent(),JsonObject.class).get("price").getAsDouble();
        int qty = new Gson().fromJson(request.getContent(), JsonObject.class).get("quantity").getAsInt();
        String comment = new Gson().fromJson(request.getContent(),JsonObject.class).get("comment").getAsString();

        try(Connection con = getConnection();){

            PreparedStatement query = con.prepareStatement("select categoryID from category where categoryName = ?");
            query.setString(1,category);
            ResultSet resultSet = query.executeQuery();
            resultSet.next();
            int categeoryId = resultSet.getInt("categoryID");

            String insertContactSql = "INSERT INTO Item (itemName, brand, categoryID, price, quantity, description) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement posted2 = con.prepareStatement(insertContactSql);
            posted2.setString(1,itenName);
            posted2.setString(2,brand);
            posted2.setInt(3, categeoryId);
            posted2.setDouble(4, price);
            posted2.setInt(5, qty);
            posted2.setString(6, comment);
            posted2.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        JsonObject json = new JsonObject();
        json.addProperty("test", "success");
        response.response(json.toString());
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
