package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Path("users/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Users { ;
    //gets a list of the users
    @GET
    @Path("list")
    public String getUsersList() {
        System.out.println("Invoked Users.UsersList()");
        JSONArray response = new JSONArray();
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, Name, Email FROM Users");  //selecting UserID and Name from the table Users
            ResultSet results = ps.executeQuery();
            while (results.next() == true) {
                JSONObject row = new JSONObject();
                row.put("UserID", results.getInt(1));
                row.put("Name", results.getString(2));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
    //gets the info about a User with x ID
    @GET
    @Path("get/{UserID}")
    public String GetUser(@PathParam("UserID") Integer UserID) {
        System.out.println("Invoked Users.GetUser() with UserID " + UserID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, Email, Name FROM Users WHERE UserID = ?"); //selecting UserID, Email and Name from the table Users where the UserID is x
            ps.setInt(1, UserID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next() == true) {
                response.put("UserID", results.getString(1)); //fetching UserID
                response.put("Email", results.getString(2)); //fetching Email
                response.put("Name", results.getString(3)); //fetching Name associated with Name
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }
    }
    //adding a new user
    @POST
    @Path("add")
    public String UsersAdd(@FormDataParam("Name") String Name, @FormDataParam("Email") String Email, @FormDataParam("Password") String Password,  @FormDataParam("SessionToken") String Cookie) throws SQLException {
        System.out.println("Invoked Users.AddUser()");
        PreparedStatement UserIncrement= Main.db.prepareStatement("SELECT MAX(UserID) FROM Users");
        ResultSet UserIDset=UserIncrement.executeQuery();
        int UserID = UserIDset.getInt(1)+1;
        boolean Admin = false;
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (UserID, Password, Name, Email, Admin) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, UserID);
            ps.setString(2, Password);
            ps.setString(3, Name);
            ps.setString(4, Email);
            ps.setBoolean(5, Admin);
            ps.execute();
            return "{\"OK\": \"Added user.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }
    //deleting a user
    @DELETE
    @Path("delete/{UserID}")
    public String UsersDelete(@PathParam("UserID") Integer UserID){
        System.out.println("Invoked Users.DeleteUser()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM USERS WHERE UserID = ? ");
            ps.setInt(1, UserID);
            ps.execute();
            return "{\"OK\": \"Deleted user.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete user, please see server console for more info.\"}";
        }

    }
    //logging in
    @POST
    @Path("login")
    public String UsersLogin(@FormDataParam("Email") String Email, @FormDataParam("Password") String Password) { //takes email and passwords as variables
        System.out.println("Invoked loginUser() on path users/login");
        try {
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT PassWord FROM Users WHERE Email = ?"); //retrieving password from database
            ps1.setString(1, Email);
            ResultSet loginResults = ps1.executeQuery();
            if (loginResults.next() == true) {
                String correctPassword = loginResults.getString(1);
                if (Password.equals(correctPassword)) {  //checking password
                    String Token = UUID.randomUUID().toString();
                    PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Users SET Token = ? WHERE Email = ?"); //generating and assigning cookie token
                    ps2.setString(1, Token);
                    ps2.setString(2, Email);
                    ps2.executeUpdate();
                    JSONObject userDetails = new JSONObject();
                    userDetails.put("Email", Email);
                    userDetails.put("Token", Token);  //assigning a token to the session
                    return userDetails.toString();
                } else {
                    return "{\"Error\": \"Incorrect username or password\"}";
                }
            } else {
                return "{\"Error\": \"Incorrect username or password\"}";
            }  //indicating that one or both of the details that were entered are incorrect
        } catch (Exception exception) {
            System.out.println("Database error during /users/login: " + exception.getMessage());
            return "{\"Error\": \"Server side error!\"}";
        }
    }
    @POST
    @Path("Logout")
    public static String Logout(@CookieParam("Token") String Token){
        try{
            System.out.println("users/logout "+ Token);
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID FROM Users WHERE Token=?");
            ps.setString(1, Token);
            ResultSet logoutResults = ps.executeQuery();
            if (logoutResults.next()){
                int UserID = logoutResults.getInt(1);
                //Set the token to null to indicate that the user is not logged in
                PreparedStatement ps1 = Main.db.prepareStatement("UPDATE Users SET Token = NULL WHERE UserID = ?");
                ps1.setInt(1, UserID);
                ps1.executeUpdate();
                return "{\"status\": \"OK\"}";
            } else {
                return "{\"error\": \"Invalid token!\"}";

            }
        } catch (Exception exception) {
            System.out.println("Database error during /users/logout: " + exception.getMessage());
            return "{\"error\": \"Server side error!\"}";
        }
    }
    public static int validToken(String token) {
        System.out.println("Invoked users.validToken(), Token value " + token);
        try {
            PreparedStatement statement = Main.db.prepareStatement("SELECT UserID FROM Users WHERE token = ?");
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("userID is " + resultSet.getInt("userID"));
            return resultSet.getInt("userID");  //Retrieve by column name  (should really test we only get one result back!)
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;  //rogue value indicating error
        }
    }

}



