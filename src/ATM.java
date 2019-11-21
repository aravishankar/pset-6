import java.io.IOException;
import java.util.Scanner;

public class ATM {
    
    private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;
    
    ////////////////////////////////////////////////////////////////////////////
    //                                                                        //
    // Refer to the Simple ATM tutorial to fill in the details of this class. //
    // You'll need to implement the new features yourself.                    //
    //                                                                        //
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a new instance of the ATM class.
     */
    
    public ATM() {
        this.in = new Scanner(System.in);
        
        activeAccount = new BankAccount(1234, 123456789, 0, new User("Ryan", "Wilson"));
        
        try {
			this.bank = new Bank();
			
		} catch (IOException e) {
			// cleanup any resources (i.e., the Scanner) and exit
		}
    }

    public void startup() {
        System.out.println("Welcome to the AIT ATM!");
        
        System.out.print("Account No.: ");
        long accountNo = in.nextLong();
        
        System.out.print("PIN        : ");
        int pin = in.nextInt();
        
        if (accountNo == activeAccount.getAccountNo() && pin == activeAccount.getPin()) {
        	System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
        	
        	System.out.println("[1] View balance");
        	System.out.println("[2] Deposit money");
        	System.out.println("[3] Withdraw money");
        	
        	int selection = in.nextInt();
        	switch (selection) {
        	
        		case 1: //view balance
        		case 2: //deposit money
        		case 3: //withdraw money
        		default: //invalid selection
        		
        	}
        
        } else {
        	System.out.println("\nInvalid account number and/or PIN.\n");
        }
        
    }
    
    /*
     * Application execution begins here.
     */
    
    public static void main(String[] args) {
        ATM atm = new ATM();
        
        atm.startup();
    }
}
