import java.util.Scanner;

public class ATM {

	private Scanner in;
    private BankAccount activeAccount;
    private Bank bank;
    private User newUser;
    
    public static final int VIEW = 1;
	 public static final int DEPOSIT = 2;
	 public static final int WITHDRAW = 3;
	 public static final int TRANSFER = 4;
	 public static final int LOGOUT = 5;
	 
	 public static final int INVALID = 0;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2; 
    public static final int OVERFILL = 3; 
    
	 public static final int FIRST_NAME_WIDTH = 20;
	 public static final int LAST_NAME_WIDTH = 30;	
	 public static final int PIN_MIN = 1000;
	 public static final int PIN_MAX = 9999;
	 public static final int ACCOUNT_NO_MIN = 100000001;
	 public static final int ACCOUNT_NO_MAX = 999999999;
	 
    
	 public ATM() {
         in = new Scanner(System.in);
         try {
 			this.bank = new Bank();
 		} catch (IOException e) {
             in.close();
 		}
     }
    
    public void startup() {
    	
    	long accountNo;
    	int pin;
    	boolean accountCreationPhase = false;
    	
        System.out.println("Welcome to the AIT ATM!\n");
        
        while (true) {
            System.out.print("Account No.: ");
            String accountNoString = in.nextLine();
            
            if (accountNoString.charAt(0) == '+') {
                accountNo = 0;
                accountCreationPhase = true;
                //createAccount();
            } else if (accountNoString.matches("[0-9]+")) {
                accountNo = Long.parseLong(accountNoString);
            } else if (accountNoString.matches("-")) {
                accountNo = 0;
            } else if (!(accountNoString.matches("[0-9]+")) && !(accountNoString.contains("-")) ) {
                accountNo = 0;
            } else if (accountNoString.isEmpty()) {
                accountNo = 0;
            } else if (Long.parseLong(accountNoString) == -1) {
                accountNo = -1;
            } else {
                accountNo = 0;
            }
            
            if (!accountCreationPhase) {
            	
            	System.out.print("PIN        : ");
                String pinString = in.nextLine();
                if (pinString.isEmpty()) {
                    pin = 0;
                } else if (pinString.matches("[0-9]+")) {
                    pin = Integer.parseInt(pinString);
                } else if (pinString.matches("-")) {
                    pin = 0;
                } else if (!(pinString.matches("[0-9]+")) && !(pinString.contains("-")) ) {
                    pin = 0;
                } else if (Integer.parseInt(pinString) == -1) {
                    pin = -1;
                } else {
                    pin = 0;
                }
            	
            	if (isValidLogin(accountNo, pin)) {
                    System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
                    
                    boolean validLogin = true;
                    while (validLogin) {
                        switch (getSelection()) {
                            case VIEW: showBalance(); break;
                            case DEPOSIT: deposit(); break;
                            case WITHDRAW: withdraw(); break;
                            case TRANSFER: transfer(); break;
                            case LOGOUT: validLogin = false; break;
                            default: System.out.println("\nInvalid selection.\n"); break;
                        }
                    }
                } else {
                    if (accountNo == -1 && pin == -1) {
                        shutdown();
                    } else {
                        System.out.println("\nInvalid account number and/or PIN.\n");
                    }
                }
            }
        }
    }
    
    public boolean isValidLogin(long accountNo, int pin) {
        return accountNo == activeAccount.getAccountNo() && pin == activeAccount.getPin();
    }
    
    public int getSelection() {
        System.out.println("[1] View balance");
        System.out.println("[2] Deposit money");
        System.out.println("[3] Withdraw money");
        System.out.println("[4] Withdraw money");
        System.out.println("[5] Logout");
        
        if (in.hasNextInt()) {
        	return in.nextInt();
        } else {
            in.nextLine();
            return 0;
        }
        
    }
    
    public void showBalance() {
        System.out.println("\nCurrent balance: " + activeAccount.getBalance());
    }
    
    public void deposit() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
            
        int status = activeAccount.deposit(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nDeposit accepted.\n");
        }
    }
    
    public void withdraw() {
        System.out.print("\nEnter amount: ");
        double amount = in.nextDouble();
            
        int status = activeAccount.withdraw(amount);
        if (status == ATM.INVALID) {
            System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
        } else if (status == ATM.INSUFFICIENT) {
            System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
        } else if (status == ATM.SUCCESS) {
            System.out.println("\nWithdrawal accepted.\n");
        }
    }
    
    public void transfer() {
    	boolean validAccount = true;
        System.out.print("\nEnter the other account no. : ");
        long secondedAccountNumber = in.nextLong();
        System.out.print("Enter amount                : ");
        double amount = in.nextDouble();
        if (bank.getAccount(secondedAccountNumber) == null) {
        	validAccount = false;
        }
        if (validAccount) {
        	BankAccount transferAccount = bank.getAccount(secondedAccountNumber);
        	int withdrawStatus = activeAccount.withdraw(amount);
            if (activeAccount == transferAccount) {
                System.out.println("\nTransfer rejected. Destination account matches origin.\n");
            } else if (withdrawStatus == ATM.INVALID) {
                System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
            } else if (withdrawStatus == ATM.INSUFFICIENT) {
                System.out.println("\nTransfer rejected. Insufficient funds.\n");
            } else if (withdrawStatus == ATM.SUCCESS) {
            	int depositStatus = transferAccount.deposit(amount);
                if (depositStatus == ATM.OVERFILL) {
                    System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99.\n");
                } else if (depositStatus == ATM.SUCCESS) {
                	System.out.println("\nTransfer accepted.\n");
                    bank.update(activeAccount);
                    bank.save();
                }
            }
        } else {
        	System.out.println("\nTransfer rejected. Destination account not found.\n");
        }
    }
    
    public void shutdown() {
        if (in != null) {
            in.close();
        }
        
        System.out.println("\nGoodbye!");
        System.exit(0);
    }
    
//    public void createAccount() {
//    	int pin;
//    	
//        System.out.print("\nFirst Name: ");
//    	String fName = 	in.nextLine();
//
//    	if (fName.length() <= FIRST_NAME_WIDTH && fName.length() > 0) {
//    		System.out.print("Last Name: ");
//        	String lName = in.nextLine();
//
//        	if (lName.length() <= LAST_NAME_WIDTH && lName.length() > 0) {
//        		System.out.print("Pin: ");
//               	if (in.hasNextInt()) {
//            		pin = in.nextInt();
//            		in.nextLine();
//
//            		if (pin >= 1000 && pin <= 9999) {
//            			newUser = new User(fName, lName);
//                    	BankAccount newAccount = bank.createAccount(pin, newUser);
//                    	System.out.print("\nThank you. Your account number is ");
//                        System.out.println(newAccount.getAccountNo() + ".");
//                    	System.out.println("Please login to access your newly created account.");
//                    	bank.update(newAccount);
//                    	bank.save();
//            		} else {
//            			System.out.println("\nYour pin must be between 1000 and 9999.\n");
//            		}
//                } else {
//                	in.nextLine();
//                	System.out.println("\nYour pin must be numeric.\n");
//                }
//        	} else {
//        		System.out.println("\nYour last name must be between 1 and 30 characters long.");
//        	}
//    	} else {
//    		System.out.println("\nYour first name must be between 1 and 20 characters long.");
//    	}
//    }
    
    public static void main(String[] args) {
        ATM atm = new ATM();
        
        atm.startup();
    }
}