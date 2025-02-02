

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

/**
 * Servlet implementation class SignUpServlet
 */
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUpServlet() {
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
		
		String customerfirstname = request.getParameter("customerfirstname");
        String customerlastname = request.getParameter("customerlastname");
        String customeremail = request.getParameter("customeremail");
        String customerphone = request.getParameter("customerphone");
        String customerpassword = request.getParameter("customerpassword");
        
        int customerid = 0;

        try {
            // Database connection
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "ZOOTOPIA", "system");

            // SQL query to insert data and return the customer ID
            String sql = "INSERT INTO CUSTOMER (CUSTID, CUSTFIRSTNAME, CUSTLASTNAME, CUSTEMAIL, CUSTPHONENO, CUSTPASSWORD) VALUES (CUST_ID_SEQ.NEXTVAL,?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql, new String[] { "CUSTID" });
            
            ps.setString(1, customerfirstname);
            ps.setString(2, customerlastname);
            ps.setString(3, customeremail);
            ps.setString(4, customerphone);
            ps.setString(5, customerpassword);
            
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    customerid = rs.getInt(1); // Retrieve the generated CUSTID
	        }
			
            }
            con.close();
           
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
        
        
        if (customerid > 0) {
            response.sendRedirect("CustomerProfile.jsp?id=" + customerid);
        } else {
            response.getWriter().println("Error: Unable to create the account.");
	}

}
}
	
