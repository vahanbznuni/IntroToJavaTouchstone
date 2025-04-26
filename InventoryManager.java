import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * Orchestrator class for the inventory management program.
 * Provides CLI for the user to perform CRUD operations.
 */
public class InventoryManager {
    Inventory inventory;
    String datafileName;
    Scanner scanner;

    /*
     * Public constructor. Instantiates an Inventory instances and handles corrupt data when loading
     */
    public InventoryManager(String dataFileName) throws DuplicateKeyException {
        try {
            this.inventory = Inventory.loadFromCSV(dataFileName);
        } catch (CorruptDataException ex) {
            System.out.println("Error loading data");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        this.datafileName = dataFileName;
        scanner = new Scanner(System.in);
    }

    /*
     * Driver main method.
     * Instanciates a new InventoryManager instance and handles duplicate keys in data when loading
     * Prints a welcome message to the user and calls the mainMenu method to start the program flow.
     * At the end, saves data (and handles any writing error) and exits.
     */
    public static void main(String[] args) {

        // Instantiate a new InvventoryManager, and handle duplicate data error
        String filename = "data.csv";
        InventoryManager manager;
        try {
            manager = new InventoryManager(filename);
        } catch (DuplicateKeyException ex) {
            System.out.println("Duplicate items detected in input data file. Please try again");
            System.err.println(ex.getMessage());
            return;
        }
        assert !manager.equals(null);

        // Print welcome messages
        System.out.println();
        System.out.println();
        System.out.println("****************************");
        System.out.println("Welcome to Inventory Manager");
        System.out.println();

        // Call mainMenu() method to get started with program flow
        manager.mainMenu();

        // Write updated data onto the data file, handling any IO errors
        try {
            manager.inventory.saveData(manager.datafileName);
        } catch (IOException ex) {
            System.out.println("Error saving data");
            System.err.println(ex.getMessage());
        }

        // Print empty lines for visual separation before exiting
        System.out.println();
        System.out.println();
    }

    /*
     * Main Menu: Entry point for the CLI program menu.
     * Present user with options to browswer inventory, add a new item, or quit.
     */
    public void mainMenu() {
        // Initialize and populate array for menu options
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("Browse Inventory");
        menuOptions.add("Add a New Item");
        menuOptions.add("Quit");

        // Loop through menu until user (correctly) selects to advance to a sub-menu or quit
        while (true) {
            // Iterate through menu options array and print them to the user, prompting user to make a selection
            System.out.println();
            System.out.println("Main Menu");
            System.out.println();
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();
            System.out.print("Enter Option Number: ");

            // input selection from user, handling invalid String input
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println();
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                System.out.println();
                continue;
            } finally {
                flushScanner(); // flushes out scanner (of standard input), in case it contains any leftover characters
            }

            // Switch to appropriate action based on input, calling the appropriate sub-menu (and handling out-of-range input)
            switch(input) {
                case 1:
                    departmentsMenu();
                    break;
                case 2:
                    addItem();
                    break;
                case 3:
                    return;
                default:
                    System.out.println();
                    System.out.println("Invalid Input. Please ensure the number you enter is within range of the options shown.");
                    System.out.println();
                    break;
            }
            
        }
    }

    // Private utility method to flush the scanner (of standard input), in case it contains any leftover characters
    private void flushScanner() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    // Menu that displays Departments (main category)
    public void departmentsMenu() {
        // Loop through menu until user (correctly) selects to advance to a sub-menu or go back to main menu
        while (true) {
            // Initialize and populate array for menu options, based on current inventory
            List<String> menuOptions = new ArrayList<>();
            for (String departmentName : inventory.getInventory().keySet()) {
                menuOptions.add(departmentName);
            }
            menuOptions.add("Main Menu");
            
            // Iterate through menu options array and print them to the user, prompting user to make a selection
            System.out.println();
            System.out.println("Choose Department");
            System.out.println();
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();
            System.out.print("Enter Option Number: ");
            
            // input selection from user, handling invalid String input
            String departmentName = "";
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println();
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                System.out.println();
                continue;
            } finally {
                flushScanner(); // flushes out scanner (of standard input), in case it contains any leftover characters
            }

            // Switch (using an if/if else/else block) to appropriate action based on input, 
            // calling the appropriate sub-menu (and handling out-of-range input)
            if (input <= menuOptions.size()-1 && input > 0) {
                departmentName = menuOptions.get(input-1);
                boolean mainMenu = subDepartmentsMenu(departmentName);
                if (mainMenu) {
                    return;
                }
                continue;
            } else if (input == menuOptions.size()) {
                return;
            } else {
                System.out.println();
                System.out.println("Invalid Input. Please ensure the number you enter is within range of the options shown.");
                System.out.println();
                continue;
            }
        }
    }

    // Menu to add a new item
    public void addItem() {
        // Prompt user to enter Item data and input selections
        System.out.println();
        System.out.print("Enter Department (Category): ");
        String departmentName = nextLineFromCLI();
        System.out.print("Enter Sub-Category: ");
        String subCategoryName = nextLineFromCLI();
        System.out.print("Enter Item Name: ");
        String itemName = nextLineFromCLI();
        System.out.print("Enter Item Description: ");
        String itemDescription = nextLineFromCLI();
        
        // Call approrpiate method to enter data into the inventory, handling duplicate input
        try {
            inventory.addItem(departmentName, subCategoryName, itemName, itemDescription);
        } catch (DuplicateKeyException ex) {
            System.out.print("Item with this name is already in inventory");
            System.out.print("Please try again with a uniquely-named item");
        }
    }

    // Private helper method to input a line from the CLI using the scanner
    private String nextLineFromCLI() {
        return scanner.nextLine().strip();
    }

    // Menu to dispplay SubDepartments (i.e. sub-categories)
    public boolean subDepartmentsMenu(String departmentName) {
        // Loop through menu until user (correctly) selects to advance to a sub-menu or go back
        while (true) {
            // Initialize and populate array for menu options, based on current inventory
            List<String> menuOptions = new ArrayList<>();
            HashMap<String, HashMap<String, Item>> subDepartments = inventory.getInventory().get(departmentName);
            for (String subDepartmentName : subDepartments.keySet()) {
                menuOptions.add(subDepartmentName);
            }
            menuOptions.add("Go Back");
            menuOptions.add("Main Menu");
            
            // Iterate through menu options array and print them to the user, prompting user to make a selection
            System.out.println();
            System.out.println("Choose Sub-Department");
            System.out.println();
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();
            System.out.print("Enter Option Number: ");

            // input selection from user, handling invalid String input
            String subDepartmentName = "";
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println();
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                System.out.println();
                continue;
            } finally {
                flushScanner(); // flushes out scanner (of standard input), in case it contains any leftover characters
            }

            // Switch (using an if/if else/else block) to appropriate action based on input, 
            // calling the appropriate sub-menu (and handling out-of-range input)
            if (input <= menuOptions.size()-2 && input > 0) {
                subDepartmentName = menuOptions.get(input-1);
                boolean mainMenu = itemsMenu(departmentName, subDepartmentName);
                if (mainMenu) {
                    return mainMenu;
                }
                continue;
            } else if (input == menuOptions.size()-1) {
                return false;
            } else if (input == menuOptions.size()) {
                return true;
            } else {
                System.out.println();
                System.out.println("Invalid Input. Please ensure the number you enter is within range of the options shown.");
                System.out.println();
                continue;
            }
        }
    }

    // Menu that displays Items 
    public boolean itemsMenu(String departmentName, String subDepartmentName) {
        HashMap<String, HashMap<String, Item>> department = inventory.getInventory().get(departmentName);
        HashMap<String, Item> items = department.get(subDepartmentName);
        // Loop through menu until user (correctly) selects to advance to a sub-menu or go back
        while (true) {
            // Initialize and populate array for menu options, based on current inventory
            List<String> menuOptions = new ArrayList<>();
            for (String itemName : items.keySet()) {
                menuOptions.add(itemName);
            }
            menuOptions.add("Go Back");
            menuOptions.add("Main Menu");
            
            // Iterate through menu options array and print them to the user, prompting user to make a selection
            System.out.println();
            System.out.println("Choose Item:");
            System.out.println();
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();
            System.out.print("Enter Option Number: ");
            
            // input selection from user, handling invalid String input
            String itemName = "";
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println();
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                System.out.println();
                continue;
            } finally {
                flushScanner(); // flushes out scanner (of standard input), in case it contains any leftover characters
            }

            // Switch (using an if/if else/else block) to appropriate action based on input, 
            // calling the appropriate sub-menu (and handling out-of-range input)
            if (input <= menuOptions.size()-2 && input > 0) {
                itemName = menuOptions.get(input-1);
                boolean mainMenu;
                try {
                    mainMenu = viewItem(departmentName, subDepartmentName, itemName);
                } catch (NoSuchElementException ex) {
                    System.out.println();
                    System.out.println("Something went wrong. Item not found");
                    System.out.println();
                    continue;
                }
                return mainMenu;
            } else if (input == menuOptions.size()-1) {
                return false;
            } else if (input == menuOptions.size()) {
                return true;
            } else {
                System.out.println();
                System.out.println("Invalid Input. Please ensure the number you enter is within range of the options shown.");
                System.out.println();
                continue;
            }
        }
    }

    // Update name of the provided iem
    public void setItemName(Item item) {
        System.out.print("Enter Item Name: ");
        String itemName = nextLineFromCLI();
        item.setName(itemName);
        System.out.println();
    }

    // Update descriptino of the provided item
    public void setItemDescription(Item item) {
        System.out.print("Enter Item Description: ");
        String itemDescription = nextLineFromCLI();
        item.setDescription(itemDescription);
        System.out.println();
    }

    // Delete the provided item form the inventory
    public boolean deleteItem(
        String departmentName,
        String subDepartmentName,
        String itemName
    ) {
        // Initialize and populate array for menu options
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("Yes, Delete");
        menuOptions.add("No, Go Back");

        // Loop through menu until user (correctly) selects an option to delete or go back
        while (true) {
            // Iterate through menu options array and print them to the user, prompting user to make a selection
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();
            System.out.print("Enter Option Number: ");

            // input selection from user, handling invalid String input
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println();
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                System.out.println();
                continue;
            } finally {
                flushScanner(); // flushes out scanner (of standard input), in case it contains any leftover characters
            }

            // Switch to appropriate action based on input, calling the appropriate sub-menu (and handling out-of-range input)
            switch(input) {
                case 1:
                    inventory.getInventory()
                                .get(departmentName)
                                .get(subDepartmentName)
                                .remove(itemName);

                    // Delete encompassing SubCategory if empty
                    if (inventory.getInventory()
                                .get(departmentName)
                                .get(subDepartmentName).isEmpty()) {
                                    inventory.getInventory()
                                        .get(departmentName)
                                        .remove(subDepartmentName);
                    }

                    // Delete encompassing Department if empty
                    if (inventory.getInventory().get(departmentName).isEmpty()) {
                        inventory.getInventory().remove(departmentName);
                    }
                    return true;
                case 2:
                    return false;
                default:
                    System.out.println();
                    System.out.println("Invalid Input. Please ensure the number you enter is within range of the options shown.");
                    System.out.println();
                    break;
            }
        }
    }

    // Menu to view item
    public boolean viewItem(String departmentName, String subDepartmentName, String itemName) throws NoSuchElementException {
        // Get specified item from inventory, throwing an exception if item is not found
        Item item = inventory.getInventory()
                                .get(departmentName)
                                .get(subDepartmentName)
                                .get(itemName);
        if (item == null) {
            throw new NoSuchElementException("Item not found");
        }
        
        // Initialize and populate array for menu options
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("Update Name");
        menuOptions.add("Update Description");
        menuOptions.add("Delete Item");
        menuOptions.add("Main Menu");

        System.out.println();
        // Loop through menu until user (correctly) selects to advance to a sub-menu or go back
        while (true) {
            // Iterate through menu options array and print them to the user, prompting user to make a selection
            System.out.println();
            System.out.println(item);
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();
            System.out.print("Enter Option Number: ");

            // input selection from user, handling invalid String input
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                continue;
            } finally {
                flushScanner(); // flushes out scanner (of standard input), in case it contains any leftover characters
            }

            // Switch to appropriate action based on input, calling the appropriate sub-menu (and handling out-of-range input)
            switch (input) {
                case 1:
                    setItemName(item);
                    break;
                case 2:
                    setItemDescription(item);
                    break;
                case 3:
                    boolean mainMenu = deleteItem(departmentName, subDepartmentName, itemName);
                    if (mainMenu) {
                        return true;
                    }
                    break;
                case 4:
                    return true;
                default:
                    System.out.println();
                    System.out.println("Invalid Input. Please ensure the number you enter is within range of the options shown.");
                    System.out.println();
                    break;
            }
        }
    }
}