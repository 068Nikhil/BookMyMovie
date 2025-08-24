package service;

import java.util.Scanner;

public class BookMyMovieApp {
    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);

        System.out.println("********************** Book My Movie **********************");
        System.out.println("                          Welcome                          ");


        while(true) {
            System.out.println();
            System.out.println("1. Book ticket");
            System.out.println("2. Print already booked ticket");
            System.out.println("3. Cancel ticket");
            System.out.println("0. Exit from App\n");
            System.out.print("Select the service - ");

            String choice = sc.next();
            System.out.println();

            if(choice.equals("0")) {
                System.out.println("EXITED from App");
                break;
            }

            switch(choice) {
                case "1":
                    System.out.print("Enter Email ID  : ");
                    String email = sc.next();
                    String phone = "";
                    int showId = 0;
                    boolean exit = false;

                    if(BookMyMovieSys.alreadyExists(email)) {
                        System.out.println();
                        System.out.println("<TICKET ALREADY BOOKED WITH BY USER>\n" +
                                "Do you want to book with this user again ? (Y/N) : ");
                        String ans = sc.next();
                        if(ans.equals("N")) {
                            System.out.println();
                            break;
                        }
                    } else {
                        System.out.print("Enter Phone Number : ");
                        phone = sc.next();
                    }

                    System.out.println();

                    BookMyMovieSys.displayMovies();

                    while(true) {
                        System.out.println();
                        System.out.print("Please select a movie number - ");
                        int movieId = sc.nextInt();
                        if(movieId == 0) {
                            exit = true;
                            break;
                        }
                        System.out.print("Please enter city name - ");
                        String city = sc.next();
                        if(city.equals("0")) {
                            exit = true;
                            break;
                        }
                        System.out.println();
                        boolean hasTheater = BookMyMovieSys.displayTheaters(city, movieId);
                        if(hasTheater) break;
                    }
                    if(exit) {
                        System.out.println("EXITED to Menu");
                        break;
                    }


                    while(true) {
                        System.out.println();
                        System.out.print("Select Show ID from Available Theaters - ");
                        showId = sc.nextInt();
                        if(showId == 0) {
                            exit = true;
                            break;
                        }
                        System.out.println();
                        boolean isHouseful = BookMyMovieSys.displaySeats(showId);
                        if (!isHouseful) break;
                    }
                    if(exit) {
                        System.out.println("EXITED to Menu");
                        break;
                    }

                    System.out.println();
                    BookMyMovieSys.bookTicket(email, phone, showId);
                    break;

                case "2":
                    System.out.print("Enter email - ");
                    String email2 = sc.next();
                    System.out.println();
                    BookMyMovieSys.printTicket(email2);
                    System.out.println();
                    break;

                case "3":
                    System.out.println("Enter the email to Cancel ticket - ");
                    String email3 = sc.next();
                    boolean cancelSuccess = BookMyMovieSys.cancelTicket(email3);
                    if(cancelSuccess) {
                        System.out.println();
                        System.out.println("Ticket Cancelled Successfully");
                        System.out.println();
                    }
                    break;

                default:
                    System.out.println("Invalid Input!");

            }


        }




    }
}
