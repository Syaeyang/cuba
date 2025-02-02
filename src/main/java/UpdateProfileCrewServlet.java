

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
 * Servlet implementation class UpdateProfileCrewServlet
 */
public class UpdateProfileCrewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateProfileCrewServlet() {
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
		
		int crewid = Integer.parseInt(request.getParameter("crewid"));
        String crewfirstname = request.getParameter("crewfirstname");
        String crewlastname = request.getParameter("crewlastname");
        String crewemail = request.getParameter("crewemail");
        String crewphone = request.getParameter("crewphone");

        try {
        	
        	
            // Database connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "ZOOTOPIA", "system");

            // SQL query to update customer details
            String sql = "UPDATE CREW SET CREWFIRSTNAME = ?, CREWLASTNAME = ?, CREWEMAIL = ?, CREWPHONENO = ? WHERE CREWTID = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, crewfirstname);
            ps.setString(2, crewlastname);
            ps.setString(3, crewemail);
            ps.setString(4, crewphone);
            ps.setInt(5, crewid);

            int rowsUpdated = ps.executeUpdate();

            con.close();

            if (rowsUpdated > 0) {
                // Redirect to profile page with updated details
                response.sendRedirect("CrewProfile.jsp?id=" + crewid);
            } else {
                response.getWriter().println("Error: Unable to update the account.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
	}

}
