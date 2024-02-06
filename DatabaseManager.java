import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public List<BeautyProcedure> getAvailableProcedures() {
        List<BeautyProcedure> procedures = new ArrayList<>();

        try (Connection connection = connect()) {
            String selectQuery = "SELECT * FROM procedures";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectQuery)) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int price = resultSet.getInt("price");

                    BeautyProcedure procedure = new BeautyProcedure(name, price);
                    procedures.add(procedure);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return procedures;
    }
    public void insertUser(User user) {
        try (Connection connection = connect()) {
            String insertQuery = "INSERT INTO users (name, phoneNumber, countSubscribers) VALUES (?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getPhoneNumber());
                preparedStatement.setInt(3, user.getSubscribers());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User added to the database successfully");
                } else {
                    System.out.println("Failed to add user to the database");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertBooking(User user, BeautyProcedure procedure, String bookingDate, String bookingTime) {
        try (Connection connection = connect()) {
            // Получаем id пользователя из базы данных по номеру телефона
            int userId = getUserIdByPhoneNumber(user.getPhoneNumber());

            // Получаем id процедуры из базы данных по названию
            int procedureId = getProcedureIdByName(procedure.getName());

            // Вставляем бронирование
            String insertQuery = "INSERT INTO booking (user_id, procedure_id, booking_date, booking_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, procedureId);
                preparedStatement.setString(3, bookingDate);
                preparedStatement.setString(4, bookingTime);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Booking added to the database successfully");

                    // Получаем сгенерированный базой данных id для бронирования
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int bookingId = generatedKeys.getInt(1);
                        System.out.println("Booking ID: " + bookingId);
                    }
                } else {
                    System.out.println("Failed to add booking to the database");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для получения id пользователя по номеру телефона
    private int getUserIdByPhoneNumber(String phoneNumber) {
        int userId = 0;

        try (Connection connection = connect()) {
            String selectQuery = "SELECT id FROM users WHERE phoneNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, phoneNumber);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    userId = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }

    // Метод для получения id процедуры по названию
    private int getProcedureIdByName(String procedureName) {
        int procedureId = 0;

        try (Connection connection = connect()) {
            String selectQuery = "SELECT id FROM procedures WHERE name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, procedureName);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    procedureId = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return procedureId;
    }

}
