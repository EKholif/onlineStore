import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUserCheck {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/shop?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "0000";

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, tenant_id, photos, email FROM `'user'`")) {

            List<String> jsonObjects = new ArrayList<>();
            while (rs.next()) {
                long id = rs.getLong("id");
                Long tenantId = (Long) rs.getObject("tenant_id");
                String photos = rs.getString("photos");
                String email = rs.getString("email");
                
                // Simple manual JSON construction to avoid dependencies
                String cleanPhotos = (photos == null) ? "null" : "\"" + escape(photos) + "\"";
                String cleanEmail = (email == null) ? "null" : "\"" + escape(email) + "\"";
                String tId = (tenantId == null) ? "null" : String.valueOf(tenantId);
                
                String json = String.format("{\"id\": %d, \"tenant_id\": %s, \"photos\": %s, \"email\": %s}",
                        id, tId, cleanPhotos, cleanEmail);
                jsonObjects.add(json);
            }
            
            System.out.println("[");
            System.out.println(String.join(",\n", jsonObjects));
            System.out.println("]");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
