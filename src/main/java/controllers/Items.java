package controllers;


import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("items/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)

public class Items{ ;
        //list of items
        @GET
        @Path("list")
        public String getItemsList() {
            System.out.println("Invoked Items.ItemsList()");
            JSONArray response = new JSONArray();
            try {
                PreparedStatement ps = Main.db.prepareStatement("SELECT ItemID, Name FROM Items");  //selecting UserID and Name from the table Items
                ResultSet results = ps.executeQuery();
                while (results.next() == true) {
                    JSONObject row = new JSONObject();
                    row.put("ItemID", results.getInt(1));
                    row.put("Name", results.getString(2));
                    response.add(row);
                }
                return response.toString();
            } catch (Exception exception) {
                System.out.println("Database error: " + exception.getMessage());
                return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
            }
        }
        @GET
        @Path("get/{ItemID}")
        public String GetUser(@PathParam("ItemID") Integer ItemID) {
            System.out.println("Invoked Users.GetUser() with UserID " + ItemID);
            try {
                PreparedStatement ps = Main.db.prepareStatement("SELECT ItemID, Name, Price FROM Items WHERE ItemID = ?"); //selecting ItemID, Name, and Price from the table Items where the ItemID is x
                ps.setInt(1, ItemID);
                ResultSet results = ps.executeQuery();
                JSONObject response = new JSONObject();
                if (results.next() == true) {
                    response.put("ItemID", results.getString(1));
                    response.put("Name", results.getString(2));
                    response.put("Price", results.getFloat(3));
                }
                return response.toString();
            } catch (Exception exception) {
                System.out.println("Database error: " + exception.getMessage());
                return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
            }
        }
        @POST
        @Path("add")
        public String ItemsAdd(@FormDataParam("ItemID") Integer ItemID, @FormDataParam("Name") String Name, @FormDataParam("ResourceID") Integer ResourceID,@FormDataParam("CategoryID") Integer CategoryID, @FormDataParam("Price") Float Price, @FormDataParam("Status") Integer Status) {
            System.out.println("Invoked Items.ItemAdd()");
            try {
                PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Items (ItemID, Name, ResourceID, CategoryID, Price, Status) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setInt(1, ItemID);
                ps.setString(2, Name);
                ps.setInt(3, ResourceID);
                ps.setInt(4, CategoryID);
                ps.setFloat(5, Price);
                ps.setInt(6, Status);
                ps.execute();
                return "{\"OK\": \"Added Item.\"}";
            } catch (Exception exception) {
                System.out.println("Database error: " + exception.getMessage());
                return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
            }

        }


}
