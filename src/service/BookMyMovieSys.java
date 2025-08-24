package service;

import config.DataBaseConfig;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BookMyMovieSys {


    public static void displayMovies() {
        try{
            Connection conn = DataBaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from movies");

            System.out.println("-------------------- Available Movies --------------------");
            while(rs.next()) {
                System.out.println(rs.getInt("movie_id") +
                        ". " + rs.getString("title") +
                        " (" + rs.getString("genre") + ") " +
                        "| " + rs.getString("lang") +
                        " | " + rs.getInt("duration") + " mins | ");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }


    }


    public static boolean displayTheaters(String city, int movieId) {
        boolean hasData = false;
        try{
            Connection conn = DataBaseConfig.getConnection();
            String query = "select * from " +
                    "theaters th inner join shows s on th.theater_id = s.theater_id " +
                    "inner join movies m on m.movie_id = s.movie_id " +
                    "where th.city = ? and m.movie_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1,city);
            pstmt.setInt(2,movieId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("---------------------------Available Theaters---------------------------");
            while(rs.next()) {
                hasData = true;

                    System.out.println("Show ID: " + rs.getInt("show_id") + "| "
                            + rs.getString("name") +
                            " (" + rs.getString("city") + ")" +
                            " [" + rs.getString("timing") + ", " +
                            rs.getInt("available_seats") + " Seats Available]");


            }

            if(!hasData) {
                System.out.println("*! No Theater Available for movie [" +
                        movieId + "] in city [" + city + "] !*");
            }


        }catch(Exception e) {
            e.printStackTrace();
        }
        return hasData;
    }


    public static boolean displaySeats(int showId) {

        boolean isHouseful = true;

        try{
            Connection conn = DataBaseConfig.getConnection();
            String query = "select * from shows s" +
                    " inner join seats st on s.show_id = st.show_id" +
                    " where s.show_id = ? and st.is_booked = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, showId);
            pstmt.setBoolean(2, false);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("----------------------------Available Seats-----------------------------");
            int x = 1;
            while(rs.next()) {
                isHouseful = false;
                System.out.print("[" + rs.getString("seat_number") +
                        " => Rs " + rs.getDouble("price") + "]  ");
                if(x%4 == 0) {
                    System.out.println();
                }
                x++;
            }
            if(isHouseful) {
                System.out.println("                        << H-O-U-S-E-F-U-L-L >>                        ");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return isHouseful;
    }


    public static boolean alreadyExists(String email) {
        try {
            Connection conn = DataBaseConfig.getConnection();

            String query6 = "select * from users where email = ?";
            PreparedStatement pstmt6 = conn.prepareStatement(query6);
            pstmt6.setString(1, email);
            ResultSet rs3 = pstmt6.executeQuery();
            while(rs3.next()) {
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public static void bookTicket(String email, String phone, int showId){

        int userId = 0;
        Scanner sc = new Scanner(System.in);

        System.out.println("Please select seats (write them separated by comma without any space) : ");
        String seatsSelected = sc.next();

        List<String> list = Arrays.asList(seatsSelected.split(","));

        double totalPrice = 0;
        String alreadyBookedSeats = "";
        double alreadyBookedSeatsPrice = 0;


        try {
            Connection conn = DataBaseConfig.getConnection();
            conn.setAutoCommit(false);

            boolean alreadyExists = alreadyExists(email);

            if(!alreadyExists) {
                String query5 = "insert into users(email, phone) values (?, ?)";
                String query9 = "select user_id from users where email = ? or phone = ?";
                PreparedStatement pstmt5 = conn.prepareStatement(query5);
                pstmt5.setString(1, email);
                pstmt5.setString(2, phone);
                pstmt5.execute();

                PreparedStatement pstmt9 = conn.prepareStatement(query9);
                pstmt9.setString(1, email);
                pstmt9.setString(2, phone);

                ResultSet rs9 = pstmt9.executeQuery();
                while(rs9.next()) {
                    userId = rs9.getInt("user_id");
                }

            } else {

                String query6 = "select * from users where email = ? or phone = ?";
                PreparedStatement pstmt6 = conn.prepareStatement(query6);
                pstmt6.setString(1, email);
                pstmt6.setString(2, phone);
                ResultSet rs3 = pstmt6.executeQuery();
                while(rs3.next()) {
                    userId = rs3.getInt("user_id");
                }


                String query7 = "select seats_booked, total_price from bookings where user_id = ?";
                PreparedStatement pstmt7 = conn.prepareStatement(query7);
                pstmt7.setInt(1, userId);
                ResultSet rs4 = pstmt7.executeQuery();
                while(rs4.next()) {
                    alreadyBookedSeats = rs4.getString("seats_booked");
                    alreadyBookedSeatsPrice = rs4.getDouble("total_price");
                }
            }




            String query2 = "select price from seats" +
                    " where seat_number = ?";

            for(String i : list) {
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setString(1, i);
                ResultSet rs2 = pstmt2.executeQuery();
                while(rs2.next()) {
                    totalPrice += rs2.getDouble("price");
                }
            }


            if(!alreadyExists) {
                String query3 = "insert into bookings (user_id, show_id, seats_booked, total_price) values " +
                        "(?, ?, ?, ?)";

                PreparedStatement pstmt3 = conn.prepareStatement(query3);
                pstmt3.setInt(1, userId);
                pstmt3.setInt(2, showId);
                pstmt3.setString(3, seatsSelected);
                pstmt3.setDouble(4, totalPrice);
                pstmt3.execute();

            } else {

                String updatedSeats = alreadyBookedSeats + "," + seatsSelected;
                double updatedTotalPrice = alreadyBookedSeatsPrice + totalPrice;
                String query8 = "update bookings" +
                        " set seats_booked = ?, total_price = ?" +
                        " where user_id = ?";

                PreparedStatement pstmt5 = conn.prepareStatement(query8);
                pstmt5.setString(1, updatedSeats);
                pstmt5.setDouble(2, updatedTotalPrice);
                pstmt5.setInt(3, userId);
                pstmt5.execute();
            }

            String query4 = "update seats st" +
                    " inner join shows s on st.show_id = s.show_id" +
                    " set st.is_booked = TRUE, s.available_seats = s.available_seats - 1" +
                    " where st.seat_number = ? and s.show_id = ?";

            for(String i : list) {
                PreparedStatement pstmt4 = conn.prepareStatement(query4);
                pstmt4.setString(1, i);
                pstmt4.setInt(2, showId);
                pstmt4.execute();
            }

            System.out.println("Book Ticket (Enter Y to confirm) - ");
            String confirm = sc.next();

            if(confirm.equals("Y")) {
                conn.commit();
                System.out.println("Ticket Booked Successfully. [Seats : " + list +
                        "] [Total Price : Rs " + totalPrice + "]\n");
            } else {
                System.out.println("Ticket Not Booked!");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void printTicket(String userEmail) {
        System.out.println("-------------------------TICKET-------------------------");
        try{
            Connection conn = DataBaseConfig.getConnection();
            String query = "select * from bookings b" +
                    " inner join users u on b.user_id = u.user_id" +
                    " inner join shows s on s.show_id = b.show_id" +
                    " inner join theaters th on th.theater_id = s.theater_id" +
                    " inner join movies m on m.movie_id = s.movie_id" +
                    " where u.email = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();

            boolean isAvailable = false;

            while(rs.next()) {
                isAvailable = true;
                System.out.println("Email : " + rs.getString("email") +
                        " | Phone : " + rs.getString("phone"));
                System.out.println("THEATER : " + rs.getString("name") + ", " + rs.getString("city"));
                System.out.println("MOVIE : " + rs.getString("title") + " |" + rs.getInt("duration") + " minutes| " +
                        " [GENRE - " + rs.getString("genre") + "]");
                List<String> seatList = Arrays.asList(rs.getString("seats_booked").split(","));
                System.out.println("SEATS BOOKED : " + seatList);
                System.out.println("SHOW TIMING : " + rs.getString("timing"));
                System.out.println("...........................");
                System.out.println("TOTAL PRICE : Rs " + rs.getDouble("total_price") + "/-");
                System.out.println("...........................");
            }
            if(!isAvailable) {
                System.out.println("No Ticket booked with this Email.");
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------------------------------------");
    }

    public static boolean cancelTicket(String email) {
        Scanner sc = new Scanner(System.in);
        String seatsBooked = "";
        int showId = 0;
        int userId = 0;
        boolean isUser = false;

        try{

            Connection conn = DataBaseConfig.getConnection();
            conn.setAutoCommit(false);

            String query1 = "select * from bookings b" +
                    " inner join users u on u.user_id = b.user_id" +
                    " where email = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(query1);
            pstmt1.setString(1,email);
            ResultSet rs1 = pstmt1.executeQuery();
            while(rs1.next()) {
                isUser = true;
                seatsBooked = rs1.getString("seats_booked");
                showId = rs1.getInt("show_id");
                userId = rs1.getInt("user_id");
            }

            if(!isUser) {
                System.out.println("No ticket found with this user email ID!");
                return false;
            }


            //updating seats from booked to unbooked
            List<String> list = Arrays.asList(seatsBooked.split(","));
            String query2 = "update seats" +
                    " set is_booked = FALSE" +
                    " where seat_number = ?";
            PreparedStatement pstmt2 = conn.prepareStatement(query2);
            for(String i : list) {
                pstmt2.setString(1, i);
                pstmt2.execute();
            }


            //updating available seats
            String query3 = "update shows" +
                    " set available_seats = available_seats + ?" +
                    " where show_id = ?";
            PreparedStatement pstmt3 = conn.prepareStatement(query3);
            pstmt3.setInt(1, list.size());
            pstmt3.setInt(2, showId);
            pstmt3.execute();

            //removing booking from database
            String query4_1 = "delete from bookings where user_id = ?";


            PreparedStatement pstmt4_1 = conn.prepareStatement(query4_1);
            pstmt4_1.setInt(1, userId);
            pstmt4_1.execute();


            //removing user from database
            String query5_1 = "delete from users where user_id = ?";


            PreparedStatement pstmt5_1 = conn.prepareStatement(query5_1);
            pstmt5_1.setInt(1, userId);
            pstmt5_1.execute();

            System.out.println("Cancel Ticket (Enter Y to confirm) - ");
            String confirm = sc.next();

            if(confirm.equals("Y")) {
                conn.commit();
                return true;
            }


        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}

