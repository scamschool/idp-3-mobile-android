// To save as "clicker\WEB-INF\classes\DisplayServlet.java".
import java.io.*;
import java.sql.*;
import jakarta.servlet.*;            // Tomcat 10 (Jakarta EE 9)
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
//import javax.servlet.*;            // Tomcat 9 (Java EE 8 / Jakarta EE 8)
//import javax.servlet.http.*;
//import javax.servlet.annotation.*;

@WebServlet("/display")   // Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
public class DisplayServlet extends HttpServlet {

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();
      // Print an HTML page as the output of the query
      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head><title>Display Response</title></head>");
      out.println("<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>");
      out.println("<body>");

      try (
         // Step 1: Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/clicker?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "myuser", "xxxx");   // For MySQL
               // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

         // Step 2: Allocate a 'Statement' object in the Connection
         Statement stmt = conn.createStatement();
      ) {
         // Step 3: Execute a SQL SELECT query
         // === Form the SQL command - BEGIN ===
         String sqlStr = "SELECT choice, COUNT(*) AS count FROM responses WHERE questionNo=8 GROUP BY choice;";
         // === Form the SQL command - END ===

         out.println("<h3>Thank you for your query.</h3>");
         out.println("<p>Your SQL statement is: " + sqlStr + "</p>"); // Echo for debugging
         ResultSet rset = stmt.executeQuery(sqlStr);  // Send the query to the serverß

         // Collect data for the chart
         StringBuilder labels = new StringBuilder();
         StringBuilder data = new StringBuilder();
         while(rset.next()) {
            labels.append("'" + rset.getString("choice") + "',");
            data.append(rset.getInt("count") + ",");
         }
         labels.deleteCharAt(labels.length() - 1); // Remove the trailing comma
         data.deleteCharAt(data.length() - 1); // Remove the trailing comma

         // Step 4: Process the query result set
         out.println("<canvas id=\"barChart\" width=\"50\" height=\"50\"></canvas>");
         out.println("<script>");
         out.println("var ctx = document.getElementById('barChart').getContext('2d');");
         out.println("var myChart = new Chart(ctx, {");
         out.println("    type: 'bar',");
         out.println("    data: {");
         out.println("        labels: [" + labels.toString() + "],");
         out.println("        datasets: [{");
         out.println("            label: 'Response Count',");
         out.println("            data: [" + data.toString() + "],");
         out.println("            backgroundColor: 'rgba(255, 99, 132, 0.2)',");
         out.println("            borderColor: 'rgba(255, 99, 132, 1)',");
         out.println("            borderWidth: 1");
         out.println("        }]");
         out.println("    },");
         out.println("    options: {");
         out.println("        scales: {");
         out.println("            y: {");
         out.println("                beginAtZero: true");
         out.println("            }");
         out.println("        }");
         out.println("    }");
         out.println("});");
         out.println("</script>");
         
         // === Step 4 ends HERE - Do NOT delete the following codes ===
      } catch(SQLException ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      } // Step 5: Close conn and stmt - Done automatically by try-with-resources (JDK 7)

      out.println("</body></html>");
      out.close();
   }
}