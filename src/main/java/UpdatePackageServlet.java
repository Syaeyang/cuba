

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Servlet implementation class UpdatePackageServlet
 */
public class UpdatePackageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdatePackageServlet() {
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
		
		int packageid = Integer.parseInt(request.getParameter("packageid"));
		String packageName = request.getParameter("packageName");
		double price = Double.parseDouble(request.getParameter("price"));
		String duration = request.getParameter("duration");
		String details = request.getParameter("additionalDetails");

        try {
        	
        	
            // Database connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "ZOOTOPIA", "system");

            // SQL query to update customer details
            String sql = "UPDATE package SET packagename = ?, packageprice = ?, packageduration = ?, packagedetails = ? WHERE packageid = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, packageName);
            ps.setDouble(2, price);
            ps.setString(3, duration);
            ps.setString(4, details);
            ps.setInt(5, packageid);

            int rowsUpdated = ps.executeUpdate();

            con.close();

            if (rowsUpdated > 0) {
                // Redirect to profile page with updated details
                response.sendRedirect("ListPackageUpdate.jsp?id=" + packageid);
            } else {
                response.getWriter().println("Error: Unable to update the package.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
		
		
        }
	}

}
