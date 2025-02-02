

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
 * Servlet implementation class UpdateSlotServlet
 */
public class UpdateSlotServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateSlotServlet() {
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
		
		 // Retrieve input from HTML
		int slotid = Integer.parseInt(request.getParameter("slotid"));
		String slotNo = request.getParameter("slotNo");
		String slotStart = request.getParameter("slotStart");
		String slotEnd = request.getParameter("slotEnd");
		

        try {
        	
        	
            // Database connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "ZOOTOPIA", "system");

            // SQL query to update customer details
            String sql = "UPDATE CUSTOMER SET SLOTNO=?, SLOTSTART=?, SLOTEND=? WHERE SLOTID=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, slotNo);
			ps.setString(2, slotStart);
			ps.setString(3, slotEnd);
			ps.setInt(5, slotid);

            int rowsUpdated = ps.executeUpdate();

            con.close();

            if (rowsUpdated > 0) {
                // Redirect to profile page with updated details
                response.sendRedirect("ListSLot.jsp?id=" + slotid);
            } else {
                response.getWriter().println("Error: Unable to update the account.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    
	}

}
