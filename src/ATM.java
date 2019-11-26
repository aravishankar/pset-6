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
        System.out.println("Welcome to the AIT ATM!\n");
     
        while (true) {
            System.out.print("Account No.: ");
            String accountNumberPlaceHolder = in.nextLine();
            if(accountNumberPlaceHolder.isEmpty()) {
            	accountNo = 0;
            	pin = getPin();
            	login(accountNo, pin);
            }else if(accountNumberPlaceHolder.strip().equals("+")){
            	accountNo = 0;
            	createAccount();
            }else if(isAccountNumber(accountNumberPlaceHolder)) {
            	accountNo = Long.valueOf(accountNumberPlaceHolder);
            	pin = getPin();
            	login(accountNo, pin);
            }else if(accountNumberPlaceHolder.equals("-1")){
            	accountNo = -1;
            	pin = getPin();
            	login(accountNo, pin);             
            }else {
            	accountNo = 0;
            	pin = getPin();
            	login(accountNo, pin);
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
        double amount = 0;
        boolean validAmount = true;
        System.out.print("\nEnter amount : ");
        try {
            amount = in.nextDouble();
        } catch (Exception e) {
            validAmount = false;
            in.nextLine();
        }
        if (validAmount) {
            int status = activeAccount.withdraw(amount);
            if (status == ATM.INVALID) {
                System.out.println("\nWithdrawal rejected. Amount must be greater than $0.00.\n");
            } else if (status == ATM.INSUFFICIENT) {
                System.out.println("\nWithdrawal rejected. Insufficient funds.\n");
            } else if (status == ATM.SUCCESS) {
                System.out.println("\nWithdrawal accepted.\n");
                bank.update(activeAccount);
                bank.save();
            }
        } else {
            System.out.println("\nWithdrawal rejected. Enter vaild amount.\n");
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
    
    public void createAccount() {
    	int pin;
    	
        System.out.print("\nFirst Name: ");
    	String fName = 	in.nextLine();

    		System.out.print("Last Name: ");
        	String lName = in.nextLine();

        		System.out.print("Pin: ");
               	if (in.hasNextInt()) {
            		pin = in.nextInt();
            		in.nextLine();

            		if (pin >= 1000 && pin <= 9999) {
            			newUser = new User(fName, lName);
                    	BankAccount newAccount = bank.createAccount(pin, newUser);
                        System.out.println(newAccount.getAccountNo() + ".");
                    	bank.update(newAccount);
                    	bank.save();
            		} else {
            		}
                } else {
                	in.nextLine();
                }
        	} else {
        	}
    	} else {
    	}
    }
    
    public static void main(String[] args) {
        ATM atm = new ATM();
        
        atm.startup();
    }
}