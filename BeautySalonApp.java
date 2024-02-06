import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



class Kazashka {
    private int subscribers;

    public Kazashka(int subscribers) {
        this.subscribers = subscribers;
    }

    public int getSubscribers() {
        return subscribers;
    }

    // New method to calculate discount
    public double calculateDiscount() {
        if (subscribers > 50000) {
            // VipKazashka gets the procedure for free
            return 1.0; // 100% discount
        } else if (subscribers > 25000) {
            // MidVipKazashka gets a 50% discount
            return 0.5; // 50% discount
        } else {
            // OrdinaryKazashka pays the full price
            return 0.0; // 0% discount
        }
    }
}

class VipKazashka extends Kazashka {
    public VipKazashka(int subscribers) {
        super(subscribers);
    }
}

class MidVipKazashka extends Kazashka {
    public MidVipKazashka(int subscribers) {
        super(subscribers);
    }
}

class OrdinaryKazashka extends Kazashka {
    public OrdinaryKazashka(int subscribers) {
        super(subscribers);
    }
}

class User extends Kazashka {
    private String name;
    private String phoneNumber;

    public User(String name, String phoneNumber, int subscribers) {
        super(subscribers);
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}

class BeautyProcedure {
    private String name;
    private double price;

    public BeautyProcedure(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

class Booking {
    private User user;
    private BeautyProcedure beautyProcedure;
    private String date;
    private String time;

    public Booking(User user, BeautyProcedure beautyProcedure, String date, String time) {
        this.user = user;
        this.beautyProcedure = beautyProcedure;
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public BeautyProcedure getBeautyProcedure() {
        return beautyProcedure;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getTotalCost() {
        return beautyProcedure.getPrice();
    }
}

class BeautySalon {
    private List<BeautyProcedure> availableProcedures;
    private List<Booking> bookings;
    private DatabaseManager databaseManager;
    public BeautySalon() {
        availableProcedures = new ArrayList<>();
        bookings = new ArrayList<>();
        this.databaseManager = new DatabaseManager();
    }

    public void addProcedure(BeautyProcedure procedure) {
        availableProcedures.add(procedure);
    }

    public List<BeautyProcedure> getAvailableProcedures() {
        return databaseManager.getAvailableProcedures();
    }

    public void displayAvailableProcedures() {
        List<BeautyProcedure> procedures = databaseManager.getAvailableProcedures();

        if (procedures.isEmpty()) {
            System.out.println("No available procedures found.");
        } else {
            System.out.println("Available Procedures:");
            for (BeautyProcedure procedure : procedures) {
                System.out.println("Name: " + procedure.getName());
                System.out.println("Price: " + procedure.getPrice());
                System.out.println("------------");
            }
        }
    }

    public void makeBooking(User user, BeautyProcedure procedure, String date, String time) {
        Booking booking = new Booking(user, procedure, date, time);
        bookings.add(booking);

        databaseManager.insertBooking(user, procedure, date, time);

        System.out.println("Booking successful!");
        System.out.println("Details:");
        System.out.println("User: " + user.getName());
        System.out.println("Procedure: " + procedure.getName());
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);

        double discount = user.calculateDiscount();
        double totalCost = procedure.getPrice() * (1 - discount);

        System.out.println("Total Cost: TG" + totalCost + " (Discount: " + (discount * 100) + "%)" + " We wait for your review! Please, tag our insta account @assem_evance");
        System.out.println();
    }



    public void displayBookings() {
        System.out.println("All Bookings:");
        for (Booking booking : bookings) {
            System.out.println("User: " + booking.getUser().getName());
            System.out.println("Procedure: " + booking.getBeautyProcedure().getName());
            System.out.println("Date: " + booking.getDate());
            System.out.println("Total Cost: TG" + booking.getTotalCost());
            System.out.println();
        }
    }

    public void cancelBooking(int bookingIndex) {
        if (bookingIndex >= 0 && bookingIndex < bookings.size()) {
            Booking canceledBooking = bookings.remove(bookingIndex);
            System.out.println("Booking canceled successfully!");
            System.out.println("Canceled Booking Details:");
            System.out.println("User: " + canceledBooking.getUser().getName());
            System.out.println("Procedure: " + canceledBooking.getBeautyProcedure().getName());
            System.out.println("Date: " + canceledBooking.getDate());
            System.out.println("Total Cost: TG" + canceledBooking.getTotalCost());
            System.out.println();
        } else {
            System.out.println("Invalid booking index. Please enter a valid index.");
        }
    }
}

public class BeautySalonApp {
    public static void main(String[] args) {
        BeautySalon beautySalon = new BeautySalon();
        DatabaseManager databaseManager = new DatabaseManager();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to the Beauty Salon!");
            System.out.println("1. Display Available Procedures");
            System.out.println("2. Make a Booking");
            System.out.println("3. Display All Bookings");
            System.out.println("4. Cancel a Booking");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    beautySalon.displayAvailableProcedures();
                    break;
                case 2:
                    System.out.print("Enter your name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Enter your phone number: ");
                    String userPhoneNumber = scanner.nextLine();
                    System.out.print("Enter the number of subscribers: ");
                    int userSubscribers = scanner.nextInt();

                    Kazashka userKazashka;
                    if (userSubscribers > 50000) {
                        userKazashka = new VipKazashka(userSubscribers);
                    } else if (userSubscribers > 25000) {
                        userKazashka = new MidVipKazashka(userSubscribers);
                    } else {
                        userKazashka = new OrdinaryKazashka(userSubscribers);
                    }

                    User user = new User(userName, userPhoneNumber, userKazashka.getSubscribers());
                    databaseManager.insertUser(user);

                    beautySalon.displayAvailableProcedures();
                    System.out.print("Choose a procedure: ");
                    int procedureIndex = scanner.nextInt();
                    BeautyProcedure selectedProcedure = beautySalon.getAvailableProcedures().get(procedureIndex - 1);

                    System.out.print("Enter booking date (MM/DD/YYYY): ");
                    String bookingDate = scanner.next();

                    System.out.print("Enter booking time (XX:YY): ");
                    String bookingTime = scanner.next();

                    beautySalon.makeBooking(user, selectedProcedure, bookingDate, bookingTime);
                    break;
                case 3:
                    beautySalon.displayBookings();
                    break;
                case 4:
                    System.out.print("Enter the index of the booking to cancel: ");
                    int bookingIndex = scanner.nextInt();
                    beautySalon.cancelBooking(bookingIndex);
                    break;
                case 5:
                    System.out.println("Thank you for booking in our salon. See you soon!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
