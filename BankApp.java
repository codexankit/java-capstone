package com.scb.bank.app;

import com.scb.bank.model.*;
import com.scb.bank.repository.InMemoryAccountRepository;
import com.scb.bank.service.AccountService;
import com.scb.bank.serviceimpl.AccountServiceImpl;
import com.scb.bank.util.ConsoleIO;

import java.util.List;

public class BankApp {

    private final ConsoleIO io = new ConsoleIO();
    private final AccountService service =
            new AccountServiceImpl(new InMemoryAccountRepository());

    public static void main(String[] args) {
        new BankApp().run();
    }

    private void run() {
        System.out.println("Welcome to Standard Chartered Bank.");
        while (true) {
            printMenu();
            int choice = io.readInt("Please enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> addAccountFlow();
                    case 2 -> displayAllFlow();
                    case 3 -> displayByIdFlow();
                    case 4 -> performPaymentFlow();
                    case 5 -> performDepositFlow();
                    case 6 -> deleteAccountFlow();
                    case 7 -> { System.out.println("Exiting..."); return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("""
            1 for Add new account
            2 for Display all accounts
            3 for Display account by ID
            4 for Perform payment
            5 for Perform deposit
            6 for Delete selected account
            7 for Exit the bank application
            """);
    }

    // --- Flows matching sample screens ---

    private void addAccountFlow() {
        System.out.println("Please enter account details");
        String type = io.readString("Enter account type (Credit or Debit): ").toLowerCase();

        int id = io.readInt("Enter account number: ");
        String owner = io.readString("Enter account owner: ");
        double bal = io.readDouble("Enter balance: ");

        if (type.startsWith("credit")) {
            double limit = io.readDouble("Enter limit: ");
            Account acc = new CreditAccount(id, owner, bal, limit);
            service.addAccount(acc);
            System.out.println("Account has been successfully added!");
        } else if (type.startsWith("debit")) {
            String pwd = io.readString("Enter password: ");
            Account acc = new DebitAccount(id, owner, bal, pwd);
            service.addAccount(acc);
            System.out.println("Account has been successfully added!");
        } else {
            System.out.println("Unknown type. Use Credit or Debit.");
        }
    }

    private void displayAllFlow() {
        List<Account> all = service.getAll();
        if (all.isEmpty()) {
            System.out.println("No accounts found.");
        } else {
            all.forEach(System.out::println);
        }
    }

    private void displayByIdFlow() {
        int id = io.readInt("Please enter the account id: ");
        Account acc = service.getById(id);
        System.out.println(acc);
    }

    private void performPaymentFlow() {
        int id = io.readInt("Please enter the account id: ");
        double amt = io.readDouble("Please enter the amount: ");
        Account acc = service.getById(id);
        String pwd = (acc instanceof DebitAccount)
                ? io.readString("Enter the password: ")
                : null;
        service.performPayment(id, amt, pwd);
        System.out.println("Payment successful. New balance: " + service.getById(id).getBalance());
    }

    private void performDepositFlow() {
        int id = io.readInt("Please enter the account id: ");
        double amt = io.readDouble("Please enter the amount: ");
        Account acc = service.getById(id);
        String pwd = (acc instanceof DebitAccount)
                ? io.readString("Enter the password: ")
                : null;
        service.performDeposit(id, amt, pwd);
        System.out.println("Deposit successful. New balance: " + service.getById(id).getBalance());
    }

    private void deleteAccountFlow() {
        int id = io.readInt("Please enter the account id: ");
        service.deleteById(id);
        System.out.println("Account has been deleted successfully!");
    }
}
