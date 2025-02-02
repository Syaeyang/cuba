

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Servlet implementation class AddActivityServlet
 */
public class AddActivityServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddActivityServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
		    String activityName = request.getParameter("activityName");
	        String activityType = request.getParameter("activityType");
	        
	        // Get child-specific features
	        String rideFeature = request.getParameter("rideFeature");
	        String sportFeature = request.getParameter("sportFeature");
	        
	        Connection con = null;
	        PreparedStatement ps = null;

	        try {
	            // Database connection
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "ZOOTOPIA", "system");

	            // Insert into ACTIVITY table (parent table)
	            String activitySql = "INSERT INTO ACTIVITY (ACTID, ACTNAME) VALUES (ACT_ID_SEQ.NEXTVAL, ?)";
	            ps = con.prepareStatement(activitySql);
	            ps.setString(1, activityName);
	            ps.executeUpdate();

	            // Get the generated ACTID for the newly inserted activity
	            Statement stmt = con.createStatement();
	            ResultSet rs = stmt.executeQuery("SELECT ACT_ID_SEQ.CURRVAL FROM DUAL");
	            rs.next();
	            int actId = rs.getInt(1);

	            // Insert into the respective child table based on activityType
	            if ("extremeRide".equals(activityType)) {
	                String extremeRideSql = "INSERT INTO EXTREMERIDE (ACTID, RIDEFEATURE) VALUES (?, ?)";
	                ps = con.prepareStatement(extremeRideSql);
	                ps.setInt(1, actId);
	                ps.setString(2, rideFeature);
	                ps.executeUpdate();
	            } else if ("extremeSport".equals(activityType)) {
	                String extremeSportSql = "INSERT INTO EXTREMESPORT (ACTID, SPORTFEATURE) VALUES (?, ?)";
	                ps = con.prepareStatement(extremeSportSql);
	                ps.setInt(1, actId);
	                ps.setString(2, sportFeature);
	                ps.executeUpdate();
	            }

	            con.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        // Redirect to a page showing the updated activity list
	        RequestDispatcher req = request.getRequestDispatcher("ListPackageUpdate.jsp");
	        req.forward(request, response);
	    
	}

}
