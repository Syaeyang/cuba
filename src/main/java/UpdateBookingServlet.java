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

@WebServlet("/UpdateBookingServlet")
public class UpdateBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String bookingIdStr = request.getParameter("bookingId");
        String date = request.getParameter("date");
        String adultsStr = request.getParameter("adults");
        String childrenStr = request.getParameter("children");
        String slotIdStr = request.getParameter("slot");
        String packageIdStr = request.getParameter("packageId");

        if (bookingIdStr == null || date == null || adultsStr == null || childrenStr == null || slotIdStr == null || packageIdStr == null) {
            request.setAttribute("errorMessage", "Missing form data.");
            request.getRequestDispatcher("UpdateBooking.jsp").forward(request, response);
            return;
        }

        int bookingId, adults, children, slotId, packageId;
        try {
            bookingId = Integer.parseInt(bookingIdStr);
            adults = Integer.parseInt(adultsStr);
            children = Integer.parseInt(childrenStr);
            slotId = Integer.parseInt(slotIdStr);
            packageId = Integer.parseInt(packageIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid number format.");
            request.getRequestDispatcher("UpdateBooking.jsp").forward(request, response);
            return;
        }

        int totalPeople = adults + children;

        try (Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "ZOOTOPIA", "system")) {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Check if the slot has enough availability for the update
            String checkAvailabilitySql = "SELECT AVAILABILITY FROM SLOT WHERE SLOTID = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkAvailabilitySql);
            checkStmt.setInt(1, slotId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int availability = rs.getInt("AVAILABILITY");

                if (availability >= totalPeople) {
                    // Update the booking
                    String sql = "UPDATE BOOKING SET BOOKINGDATE = ?, BOOKINGADULTS = ?, BOOKINGCHILDREN = ?, SLOTID = ?, PACKAGEID = ? " +
                            "WHERE BOOKINGID = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setString(1, date);
                    ps.setInt(2, adults);
                    ps.setInt(3, children);
                    ps.setInt(4, slotId);
                    ps.setInt(5, packageId);
                    ps.setInt(6, bookingId);
                    int rowsUpdated = ps.executeUpdate();

                    if (rowsUpdated > 0) {
                        // Fetch updated booking details
                        String fetchBookingSql = "SELECT b.BOOKINGID, b.BOOKINGDATE, b.BOOKINGADULTS, b.BOOKINGCHILDREN, " +
                                "s.SLOTSTART, s.SLOTEND, p.PACKAGENAME, p.PACKAGEPRICE " +
                                "FROM BOOKING b " +
                                "JOIN SLOT s ON b.SLOTID = s.SLOTID " +
                                "JOIN PACKAGE p ON b.PACKAGEID = p.PACKAGEID " +
                                "WHERE b.BOOKINGID = ?";
                        PreparedStatement fetchStmt = con.prepareStatement(fetchBookingSql);
                        fetchStmt.setInt(1, bookingId);
                        ResultSet bookingDetailsRs = fetchStmt.executeQuery();

                        if (bookingDetailsRs.next()) {
                            request.setAttribute("bookingId", bookingDetailsRs.getInt("BOOKINGID"));
                            request.setAttribute("bookingDate", bookingDetailsRs.getString("BOOKINGDATE"));
                            request.setAttribute("adults", bookingDetailsRs.getInt("BOOKINGADULTS"));
                            request.setAttribute("children", bookingDetailsRs.getInt("BOOKINGCHILDREN"));
                            request.setAttribute("slotStart", bookingDetailsRs.getString("SLOTSTART"));
                            request.setAttribute("slotEnd", bookingDetailsRs.getString("SLOTEND"));
                            request.setAttribute("packageName", bookingDetailsRs.getString("PACKAGENAME"));
                            request.setAttribute("packagePrice", bookingDetailsRs.getDouble("PACKAGEPRICE"));

                            // Forward to the booking summary page
                            request.getRequestDispatcher("BookingSummary.jsp").forward(request, response);
                        }
                    } else {
                        request.setAttribute("errorMessage", "Failed to update booking. Please try again.");
                        request.getRequestDispatcher("UpdateBooking.jsp").forward(request, response);
                    }
                } else {
                    request.setAttribute("errorMessage", "Not enough availability for the selected slot.");
                    request.getRequestDispatcher("UpdateBooking.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("UpdateBooking.jsp").forward(request, response);
        }
    }
}
