import java.sql.*;
import java.util.Scanner;

class ATM {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.println("---Welcome to the ATM---");

        System.out.print("Enter your account number: ");
        int acc_num = sc.nextInt();

        System.out.print("Enter your pin: ");
        int acc_pin = sc.nextInt();

        String url = "jdbc:mysql://localhost:3306/ATM";
        String username = "root";
        String password = "zamir456";

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql_query = "SELECT * FROM accounts WHERE account_number = ? AND account_pin = ?";
            PreparedStatement ps = conn.prepareStatement(sql_query);
            ps.setInt(1, acc_num);
            ps.setInt(2, acc_pin);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account acc = new Account();
                acc.firstName = rs.getString("first_name");
                acc.lastName = rs.getString("last_name");
                acc.accountNumber = rs.getInt("account_number");
                acc.pin = rs.getInt("account_pin");
                acc.balance = rs.getInt("balance");

                System.out.println("Welcome, " + acc.firstName + " " + acc.lastName);
                boolean running = true;

                while (running) {
                    System.out.println("\nChoose operation:");
                    System.out.println("1. Check Balance");
                    System.out.println("2. Deposit");
                    System.out.println("3. Withdraw");
                    System.out.println("4. Exit");
                    System.out.print("Enter choice: ");
                    int choice = sc.nextInt();

                    switch (choice) {
                        case 1:
                            System.out.println("Your balance: " + acc.balance);
                            break;

                        case 2:
                            System.out.print("Enter deposit amount: ");
                            int deposit = sc.nextInt();
                            acc.balance += deposit;
                            updateBalance(conn, acc.accountNumber, acc.balance);
                            System.out.println("Deposit successful. New balance: " + acc.balance);
                            break;

                        case 3:
                            System.out.print("Enter withdraw amount: ");
                            int withdraw = sc.nextInt();
                            if (withdraw > acc.balance) {
                                System.out.println("Insufficient funds.");
                            } else {
                                acc.balance -= withdraw;
                                updateBalance(conn, acc.accountNumber, acc.balance);
                                System.out.println("Withdrawal successful. New balance: " + acc.balance);
                            }
                            break;

                        case 4:
                            System.out.println("Thank you for using the ATM!");
                            running = false;
                            break;

                        default:
                            System.out.println("Invalid choice.");
                    }
                }

            } else {
                System.out.println("Invalid account number or pin.");
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to update balance in database
    public static void updateBalance(Connection conn, int accNumber, int newBalance) throws SQLException {
        String updateQuery = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        PreparedStatement ps = conn.prepareStatement(updateQuery);
        ps.setInt(1, newBalance);
        ps.setInt(2, accNumber);
        ps.executeUpdate();
    }
}

