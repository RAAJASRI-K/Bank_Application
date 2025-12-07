import java.sql.*;
import java.util.*;

class Account {
    private int accno;
    private String accname;
    private double balance;

    public Account(int accno, String accname, double balance) {
        this.accno = accno;
        this.accname = accname;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if(amount > 0) {
            balance += amount;
            System.out.println("Amount deposited.");
        } else {
            System.out.println("Invalid amount.");
        }
    }

    public void withdraw(double amount) {
        if(amount > 0 && balance >= amount) {
            balance -= amount;
            System.out.println("Amount withdrawn.");
        } else {
            System.out.println("Insufficient balance.");
        }
    }
}

public class main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bankdb",
                "root",
                "123456"
            );
            Statement stmt = con.createStatement();

            System.out.println("Enter account number: ");
            int acno = scan.nextInt();

            ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE accno=" + acno);

            Account acc = null;
            if(rs.next()) {
                String acname = rs.getString("accname");
                double balance = rs.getDouble("balance");
                acc = new Account(acno, acname, balance);
                System.out.println("Welcome, " + acname + "! Your balance is: " + balance);
            } else {
    // New user
    scan.nextLine(); // consume leftover newline
    System.out.print("Account not found! Enter your name to create a new account: ");
    String acname = scan.nextLine();
    acc = new Account(acno, acname, 0.0);

    // Insert into database
    stmt.executeUpdate(
        "INSERT INTO accounts(accno, accname, balance) VALUES( " + acno + ", '" + acname + "', 0)"
    );

    System.out.println("Account created successfully for " + acname + " with balance 0.");
}

            
            while(true) {
                System.out.println("\nAccount menu:");
                System.out.println("1. Check balance");
                System.out.println("2. Deposit amount");
                System.out.println("3. Withdraw amount");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");
                int ch = scan.nextInt();

                switch(ch) {
                    case 1:
                        System.out.println("Balance: " + acc.getBalance());
                        break;
                    case 2:
                        System.out.print("Enter the amount to deposit: ");
                        double ad = scan.nextDouble();
                        acc.deposit(ad);
                        stmt.executeUpdate(
                            "UPDATE accounts SET balance=" + acc.getBalance() + " WHERE accno=" + acno
                        );
                        break;
                    case 3:
                        System.out.print("Enter the amount to withdraw: ");
                        double wa = scan.nextDouble();
                        acc.withdraw(wa);
                        stmt.executeUpdate(
                            "UPDATE accounts SET balance=" + acc.getBalance() + " WHERE accno=" + acno
                        );
                        break;
                    case 4:
                        System.out.println("Exiting...");
                        scan.close();
                        con.close();
                        return;
                    default:
                        System.out.println("Invalid option");
                        break;
                }
            }

        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
