package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            PreparedStatement ps = Main.db.prepareStatement("SELECT UserID, Name FROM Users");  //selecting UserID and Name from the table Users
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
    public String UsersAdd(@FormDataParam("UserID") Integer UserID, @FormDataParam("Name") String Name, @FormDataParam("Email") String Email, @FormDataParam("admin") Boolean Admin, @FormDataParam("Password") String Password,  @FormDataParam("SessionToken") String Cookie) {
        System.out.println("Invoked Users.AddUser()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Users (UserID, Password, Name, Email) VALUES (?, ?, ?, ?)");
            ps.setInt(1, UserID);
            ps.setString(2, Password);
            ps.setString(3, Name);
            ps.setString(4, Email);
            ps.execute();
            return "{\"OK\": \"Added user.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }
    //logging in
    @POST
    @Path("login")
    public String UsersLogin(@FormDataParam("Email") String Email, @FormDataParam("Password") String Password) {
        System.out.println("Invoked loginUsers" +
                "() on path users/login");
        try {
            PreparedStatement ps1 = Main.db.prepareStatement("SELECT PassWord FROM Users WHERE Email = ?");
            ps1.setString(1, Email);
            ResultSet loginResults = ps1.executeQuery();
            if (loginResults.next() == true) {
                String correctPassword = loginResults.getString(1);
                if (Password.equals(correctPassword)) {
                    String Token = UUID.randomUUID().toString();
                    PreparedStatement ps2 = Main.db.prepareStatement("UPDATE Users SET Token = ? WHERE Email = ?");
                    ps2.setString(1, Token);
                    ps2.setString(2, Email);
                    ps2.executeUpdate();
                    JSONObject userDetails = new JSONObject();
                    userDetails.put("Email",Email);
                    userDetails.put("Token", Token);
                    return userDetails.toString();
                } else {
                    return "{\"Error\": \"Incorrect username or password\"}";
                }
            } else {
                return "{\"Error\": \"Incorrect username or password\"}";
            }
        } catch (Exception exception) {
            System.out.println("Database error during /users/login: " + exception.getMessage());
            return "{\"Error\": \"Server side error!\"}";
        }
    }

}


