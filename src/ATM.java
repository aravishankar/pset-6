import java.io.IOException;
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
    public static final int FIRST_NAME_WIDTH = 20;
    public static final int LAST_NAME_WIDTH = 30;

    public static final int INVALID = 0;
    public static final int INSUFFICIENT = 1;
    public static final int SUCCESS = 2;
    public static final int OVERFLOW = 3;

    /**
     * Constructs a new instance of the ATM class.
     */

        public ATM() {
            in = new Scanner(System.in);
            try {
    			this.bank = new Bank();
    		} catch (IOException e) {
                in.close();
    		}
        }
        
        public long checkAccountNo(String accountNoString) {
        	
        	long accountNo;
        	
        	if (accountNoString.isEmpty()) {
                accountNo = 0;
            } else if (accountNoString.charAt(0) == '+') {
                accountNo = -2;
            } else if (accountNoString.matches("[0-9]+")) {
                accountNo = Long.parseLong(accountNoString);
            } else if (accountNoString.matches("-")) {
                accountNo = 0;
            } else if (!(accountNoString.matches("[0-9]+")) && !(accountNoString.contains("-")) ) {
                accountNo = 0;
            } else if (Long.parseLong(accountNoString) == -1) {
                accountNo = -1;
            } else {
                accountNo = 0;
            }
        	
        	return accountNo;
        }
        
        public int checkPin(String pinString) {
        	int pin;
        	
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
        	
        	return pin;
        }

        public void startup() {
        	long accountNo;
        	int pin;
            System.out.println("Welcome to the AIT ATM!");

            while (true) {
                System.out.print("\nAccount No.: ");
                String accountNoString = in.nextLine();
                
                accountNo = checkAccountNo(accountNoString);

                if (accountNo != -2) {
                    System.out.print("PIN        : ");
                    String tempPin = in.nextLine();

                    pin = checkPin(tempPin);
                    
                    if (isValidLogin(accountNo, pin)) {
                    	activeAccount = bank.login(accountNo, pin);
                        System.out.println("\nHello, again, " + activeAccount.getAccountHolder().getFirstName() + "!\n");
                        boolean validLogin = true;
                        while (validLogin) {
                            switch (getSelection()) {
                                case VIEW: showBalance(); break;
                                case DEPOSIT: deposit(); break;
                                case WITHDRAW: withdraw(); break;
                                case TRANSFER: transfer(); break;
                                case LOGOUT: bank.update(activeAccount); bank.save(); validLogin = false; in.nextLine(); break;
                                default: System.out.println("\nInvalid selection.\n"); break;
                            }
                        }
                    } else {
                        if (accountNo == -1 && pin == -1) {
                            shutdown();
                        } else {
                            System.out.println("\nInvalid account number and/or PIN.");
                        }
                    }
                } else {
                    createAccount();
                }
            }
        }
        
        public void createAccount() {
        	int pin = 0;
        	System.out.print("\nFirst Name: ");
        	String firstName = 	in.nextLine();
        	if (firstName != null && firstName.length() <= 20 && firstName.length() > 0) {
        		System.out.print("Last Name: ");
            	String lastName = in.nextLine();
            	if(lastName != null && lastName.length() <= 30 && lastName.length() > 0){
            		System.out.print("Pin: ");     
            		String pinPlaceHolder = in.nextLine();
                	if (pinPlaceHolder.isEmpty()) {
                		pin = 0;
                	} else if (pinPlaceHolder.matches("[0-9]+")){
                		pin = Integer.valueOf(pinPlaceHolder);
                	}
                	if (pin >= 1000 && pin <= 9999) {
                		newUser = new User(firstName, lastName);
                       	
                       	BankAccount newAccount = bank.createAccount(pin, newUser);
                       	System.out.println("\nThank you. Your account number is " + newAccount.getAccountNo() + ".");
                       	System.out.println("Please login to access your newly created account.\n");
                       	bank.update(newAccount);
                       	bank.save();
               		} else {
               			System.out.println("\nPlease ensure your PIN is a numeric value between 1000 and 9999.\n");
               		}         	
            	} else {
            		System.out.println("\nPlease ensure your last name is between 1 and 30 characters long\n");
            	}
        	} else {
        		System.out.println("\nPlease ensure your first name is between 1 and 20 characters long\n");
        	}
        }

        public boolean isValidLogin(long accountNo, int pin) {
        	boolean valid = false;
        	try {
        		valid = bank.login(accountNo, pin) != null ? true : false;
        	} catch (Exception e) {
        		valid = false;
        	}
            return valid;
        }

        public int getSelection() {
            System.out.println("[1] View balance");
            System.out.println("[2] Deposit money");
            System.out.println("[3] Withdraw money");
            System.out.println("[4] Transfer money");
            System.out.println("[5] Logout");

            if (in.hasNextInt()) {
            	return in.nextInt();
            } else {
                in.nextLine();
                return 6;
            }
        }

        public void showBalance() {
            System.out.println("\nCurrent balance: " + activeAccount.getBalance() + "\n");
        }

        public void deposit() {
        	double amount = 0;
            boolean validAmount = true;
    		System.out.print("\nEnter amount : ");
    		try {
    			amount = in.nextDouble();
    		} catch(Exception e) {
    			validAmount = false;
    			in.nextLine();
    		}

    		if (validAmount) {
    			int status = activeAccount.deposit(amount);
                if (status == ATM.INVALID) {
                    System.out.println("\nDeposit rejected. Amount must be greater than $0.00.\n");
                } else if (status == ATM.OVERFLOW) {
                	System.out.print("\nDeposit rejected. ");
                    System.out.println("Amount would cause balance to exceed $999,999,999,999.99.\n");
                } else if (status == ATM.SUCCESS) {
                    System.out.println("\nDeposit accepted.\n");
                    bank.update(activeAccount);
                    bank.save();
                }
    		} else {
    			System.out.println("\nDeposit rejected. Enter vaild amount.\n");
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
        	boolean isValid = true;
            System.out.print("\nEnter the other account no. : ");
            long secondAccountNo = in.nextLong();
            System.out.print("Enter amount                : ");
            double amount = in.nextDouble();
            if (bank.getAccount(secondAccountNo) == null) {
            	isValid = false;
            }
            if (isValid) {
            	BankAccount transferAccount = bank.getAccount(secondAccountNo);
            	int status = activeAccount.withdraw(amount);
                if (activeAccount == transferAccount) {
                    System.out.println("\nTransfer rejected. Destination account matches origin.\n");
                } else if (status == ATM.INVALID) {
                    System.out.println("\nTransfer rejected. Amount must be greater than $0.00.\n");
                } else if (status == ATM.INSUFFICIENT) {
                    System.out.println("\nTransfer rejected. Insufficient funds.\n");
                } else if (status == ATM.SUCCESS) {
                	int newDeposit = transferAccount.deposit(amount);
                    if (newDeposit == ATM.OVERFLOW) {
                        System.out.println("\nTransfer rejected. Amount would cause destination balance to exceed $999,999,999,999.99.\n");
                    } else if (newDeposit == ATM.SUCCESS) {
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

    /*
     * Application execution begins here.
     */

    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.startup();
    }
}