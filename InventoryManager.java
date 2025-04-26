import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class InventoryManager {
    Inventory inventory;
    String datafileName;
    Scanner scanner;

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

    public static void main(String[] args) {
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
        System.out.println();
        System.out.println();
        System.out.println("Welcome to Inventory Manager");
        System.out.println();
        System.out.println("(Press any key to proceed)");
        manager.mainMenu();
        try {
            manager.inventory.saveData(manager.datafileName);
        } catch (IOException ex) {
            System.out.println("Error saving data");
            System.err.println(ex.getMessage());
        }
        System.out.println();
        System.out.println();
    }

    public void mainMenu() {
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("Browse Inventory");
        menuOptions.add("Add a New Item");
        menuOptions.add("Quit");
        while (true) {
            flushScanner();
            System.out.println();
            System.out.println("Main Menu");
            System.out.println();
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();

            System.out.print("Enter Option Number: ");
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println();
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                System.out.println();
                continue;
            }
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

    private void flushScanner() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    public void departmentsMenu() {
        while (true) {
            List<String> menuOptions = new ArrayList<>();
            flushScanner();
            for (String departmentName : inventory.getInventory().keySet()) {
                menuOptions.add(departmentName);
            }
            menuOptions.add("Main Menu");
            String departmentName = "";

            System.out.println();
            System.out.println("Choose Department");
            System.out.println();
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();

            System.out.print("Enter Option Number: ");
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println();
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                System.out.println();
                continue;
            }
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

    public void addItem() {
        flushScanner();
        System.out.println();
        System.out.print("Enter Department (Category): ");
        String departmentName = nextLineFromCLI();
        System.out.print("Enter Sub-Category: ");
        String subCategoryName = nextLineFromCLI();
        System.out.print("Enter Item Name: ");
        String itemName = nextLineFromCLI();
        System.out.print("Enter Item Description: ");
        String itemDescription = nextLineFromCLI();
        try {
            inventory.addItem(departmentName, subCategoryName, itemName, itemDescription);
        } catch (DuplicateKeyException ex) {
            System.out.print("Item with this name is already in inventory");
            System.out.print("Please try again with a uniquely-named item");

        }

    }

    private String nextLineFromCLI() {
        return scanner.nextLine().strip();
    }

    public boolean subDepartmentsMenu(String departmentName) {
        while (true) {
            List<String> menuOptions = new ArrayList<>();
            HashMap<String, HashMap<String, Item>> subDepartments = inventory.getInventory().get(departmentName);
            flushScanner();
            for (String subDepartmentName : subDepartments.keySet()) {
                menuOptions.add(subDepartmentName);
            }
            menuOptions.add("Go Back");
            menuOptions.add("Main Menu");
            String subDepartmentName = "";

            System.out.println();
            System.out.println("Choose Sub-Department");
            System.out.println();
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();

            System.out.print("Enter Option Number: ");
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println();
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                System.out.println();
                continue;
            }
            if (input <= menuOptions.size()-2 && input > 0) {
                subDepartmentName = menuOptions.get(input-1);
                boolean mainMenu = itemsMenu(departmentName, subDepartmentName);
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


    public boolean itemsMenu(String departmentName, String subDepartmentName) {
        HashMap<String, HashMap<String, Item>> department = inventory.getInventory().get(departmentName);
        HashMap<String, Item> items = department.get(subDepartmentName);
        while (true) {
            List<String> menuOptions = new ArrayList<>();
            flushScanner();
            for (String itemName : items.keySet()) {
                menuOptions.add(itemName);
            }
            menuOptions.add("Go Back");
            menuOptions.add("Main Menu");
            String itemName = "";

            System.out.println();
            System.out.println("Choose Item:");
            System.out.println();
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();

            System.out.print("Enter Option Number: ");
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                continue;
            }
            if (input <= menuOptions.size()-2 && input > 0) {
                itemName = menuOptions.get(input-1);
                boolean mainMenu;
                try {
                    mainMenu = viewItem(departmentName, subDepartmentName, itemName);
                } catch (NoSuchElementException ex) {
                    System.out.println("Something went wrong. Item not found");
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


    public void setItemName(Item item) {
        flushScanner();
        System.out.print("Enter Item Name: ");
        String itemName = nextLineFromCLI();
        item.setName(itemName);
        System.out.println();
    }

    public void setItemDescription(Item item) {
        flushScanner();
        System.out.print("Enter Item Description: ");
        String itemDescription = nextLineFromCLI();
        item.setDescription(itemDescription);
        System.out.println();
    }

    public boolean deleteItem(
        String departmentName,
        String subDepartmentName,
        String itemName
    ) {
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("Yes, Delete");
        menuOptions.add("No, Go Back");
        while (true) {
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();

            System.out.print("Enter Option Number: ");
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                continue;
            }
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

    public boolean viewItem(String departmentName, String subDepartmentName, String itemName) throws NoSuchElementException {
        Item item = inventory.getInventory()
                                .get(departmentName)
                                .get(subDepartmentName)
                                .get(itemName);
        if (item == null) {
            throw new NoSuchElementException("Item not found");
        }
        
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("Update Name");
        menuOptions.add("Update Description");
        menuOptions.add("Delete Item");
        menuOptions.add("Main Menu");
        System.out.println();
        while (true) {
            System.out.println();
            System.out.println(item);
            for (int i=0; i<menuOptions.size(); i++) {
                System.out.println(i+1 + ". " + menuOptions.get(i));
            }
            System.out.println();

            System.out.print("Enter Option Number: ");
            int input = 0;
            try {
                input = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println("Invalid Input. Please ensure you are entering an integer.");
                continue;
            }
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