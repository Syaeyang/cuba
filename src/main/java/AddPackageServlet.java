

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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Servlet implementation class AddPackageServlet
 */
public class AddPackageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddPackageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		 ArrayList<HashMap<String, Object>> packages = new ArrayList<>();
	        HashMap<Integer, ArrayList<HashMap<String, Object>>> activitiesByPackage = new HashMap<>();

	        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "ZOOTOPIA", "system");

	            // Fetch packages
	            String packageQuery = "SELECT packageid, packagename, packageprice, packageduration, packagedetails FROM package";
	            Statement packageStmt = con.createStatement();
	            ResultSet packageRs = packageStmt.executeQuery(packageQuery);

	            while (packageRs.next()) {
	                HashMap<String, Object> pkg = new HashMap<>();
	                int packageId = packageRs.getInt("packageid");
	                pkg.put("id", packageId);
	                pkg.put("name", packageRs.getString("packagename"));
	                pkg.put("price", packageRs.getDouble("packageprice"));
	                pkg.put("duration", packageRs.getString("packageduration"));
	                pkg.put("details", packageRs.getString("packagedetails"));
	                packages.add(pkg);

	                // Initialize an empty list for activities for this package
	                activitiesByPackage.put(packageId, new ArrayList<>());
	            }

	            // Fetch activities
	            String activityQuery = "SELECT ACTIVITY.ACTID, ACTIVITY.ACTNAME, ACTIVITY.ACTSAFETY, package.packageid " +
	                                   "FROM ACTIVITY " +
	                                   "INNER JOIN PACKAGE_ACTIVITY ON ACTIVITY.ACTID = PACKAGE_ACTIVITY.ACTID " +
	                                   "WHERE PACKAGE_ACTIVITY.packageid = ?";
	            PreparedStatement activityStmt = con.prepareStatement(activityQuery);

	            for (HashMap<String, Object> pkg : packages) {
	                int packageId = (int) pkg.get("id");
	                activityStmt.setInt(1, packageId);
	                ResultSet activityRs = activityStmt.executeQuery();

	                while (activityRs.next()) {
	                    HashMap<String, Object> act = new HashMap<>();
	                    act.put("id", activityRs.getInt("ACTID"));
	                    act.put("name", activityRs.getString("ACTNAME"));
	                    act.put("safety", activityRs.getString("ACTSAFETY"));
	                    activitiesByPackage.get(packageId).add(act);
	                }
	            }

	            con.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        request.setAttribute("packages", packages);
	        request.setAttribute("activitiesByPackage", activitiesByPackage);
	        RequestDispatcher rd = request.getRequestDispatcher("AddActivity.jsp");
	        rd.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		String name = request.getParameter("packageName");
		double price = Double.parseDouble(request.getParameter("price"));
		String duration = request.getParameter("duration");
		String details = request.getParameter("additionalDetails");
		
		int packageid = 0; 
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "ZOOTOPIA", "system");
			
			String sql = "INSERT INTO package(packageid, packagename, packageprice, packageduration, packagedetails) VALUES(product_id_seq.NEXTVAL,?,?,?,?)";
			PreparedStatement ps = con.prepareStatement(sql, new String[] { "packageid" });
			
			ps.setString(1, name);
			ps.setDouble(2, price);
			ps.setString(3, duration);
			ps.setString(4, details);
			
			ps.executeUpdate();
			
			 // Retrieve the generated package ID
	        ResultSet rs = ps.getGeneratedKeys();
	        if (rs.next()) {
	            packageid = rs.getInt(1);
	        }
	        
	     // Fetch all packages
            ArrayList<HashMap<String, Object>> packages = new ArrayList<>();
            Statement packageStmt = con.createStatement();
            ResultSet packageRs = packageStmt.executeQuery("SELECT * FROM package");
            while (packageRs.next()) {
                HashMap<String, Object> packageData = new HashMap<>();
                packageData.put("id", packageRs.getInt("packageid"));
                packageData.put("name", packageRs.getString("packagename"));
                packageData.put("price", packageRs.getDouble("packageprice"));
                packageData.put("duration", packageRs.getString("packageduration"));
                packageData.put("details", packageRs.getString("packagedetails"));
                packages.add(packageData);
            }
            
         // Fetch all activities
            ArrayList<HashMap<String, Object>> activities = new ArrayList<>();
            Statement activityStmt = con.createStatement();
            ResultSet activityRs = activityStmt.executeQuery("SELECT * FROM ACTIVITY");
            while (activityRs.next()) {
                HashMap<String, Object> activityData = new HashMap<>();
                activityData.put("id", activityRs.getInt("ACTID"));
                activityData.put("activityName", activityRs.getString("ACTNAME"));
                activityData.put("safety", activityRs.getString("ACTSAFETY"));
                activities.add(activityData);
            }
			
			con.close();
			
			// Set the data as request attributes
            request.setAttribute("packages", packages);
            request.setAttribute("activities", activities);
		}
		catch (Exception e){
			System.out.println(e);
			System.out.println("Error:" + e.getMessage());
		}
		
		RequestDispatcher req = request.getRequestDispatcher("ListPackageUpdate.jsp");
		req.forward(request, response);
	}

}
