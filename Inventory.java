import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/*
 * Main data structure of the program.
 * Uses nested hashmaps (HashMap) to hiearchically store departments,
 * subcategories within departments, and list of items in each department
 * 
 * The class provides an interface for programmatic CRUD operations, and it is up to
 * a driver/orchestrator class to provide the logic for CLI interaction by the user.
 */
public class Inventory {
    private HashMap<String, HashMap<String, HashMap<String, Item>>> inventory;

    private Inventory() {
        inventory = new HashMap<String, HashMap<String, HashMap<String, Item>>>();
    }

    public HashMap<String, HashMap<String, HashMap<String, Item>>> getInventory() {
        return inventory;
    }

    /*
     * Instantiate a new Inventory object, load data from provided CSV file, and return it
     */
    public static Inventory loadFromCSV(String fileName) throws DuplicateKeyException, CorruptDataException {
        Inventory inventory = new Inventory();
        inventory.loadData(fileName);
        return inventory;
    }

    /*
     * Load data from a provided CSV file
     * Note, this method iterates over all the rows in the provided file and adds the data
     * to the internal data structure. It does not automaticallly delete any existing data.
     * The method also assumes that data consists of rows of 4 columns each. 
     */
    public void loadData(String fileName) throws DuplicateKeyException, CorruptDataException {
        File file = new File(fileName);
        List<String> rows = new ArrayList<>();
        
        try {
            rows = Files.readAllLines(file.toPath());
            // start iteration at index 1, since we expect a header row at idx 0
            for (int i=1; i<rows.size(); i++) {
                String[] rowItems = rows.get(i).split(",");
                // Check to ensure the row contains 4 elements
                if (rowItems.length!=4) {
                    throw new CorruptDataException("Unexpected input file: extected row length of 4 but found " + rowItems.length);
                }
                
                String departmentName = rowItems[0].strip();
                String subCategoryName = rowItems[1].strip();
                String itemName = rowItems[2].strip();
                String itemDescription = rowItems[3].strip();
                if ((
                    itemDescription.length()>=2 && 
                    itemDescription.startsWith("\"") &&  
                    itemDescription.endsWith("\""))
                ) {
                    itemDescription = itemDescription.substring(1, itemDescription.length()-1);
                } 
                addItem(departmentName, subCategoryName, itemName, itemDescription);
            }
        } catch (IOException ex) {
            System.err.println("Error reading file: " + ex.getMessage());
        }
    }

    /*
     * Save data to a CSV file.
     * Note: this method iterates through the classe's internal data structure,
     * extracts each item with it's corresponding informaiton, and writes it to the CSV on disk.
     */
    public void saveData(String fileName) throws IOException {
        String backupFileName = getBackupFileName(fileName);
        File file = new File(fileName);

        // Make a backup copy of the data file
        File backupFile = new File(backupFileName);
        Files.move(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // Iterate depth-first over each department, each subCategory within, each
        // Item within, and write each item's information - along with corresponding
        // department and SubCategory as a row onto the CSV file
        for (String departmentName : inventory.keySet()) {
            HashMap<String, HashMap<String, Item>> department = inventory.get(departmentName);
            for (String subCategoryName : department.keySet()) {
                HashMap<String, Item> subCategory = department.get(subCategoryName);
                for (String itemName : subCategory.keySet()) {
                    Item item = subCategory.get(itemName);
                    // Just a sanity check
                    assert itemName.equals(item.getName());

                    // Add quotation symbols to item description string (unless already there)
                    // so that any potential commas (,) do not corrupt the CSV format
                    String itemDescription = item.getDescription();
                    if (!(
                        itemDescription.charAt(0) == '"' && 
                        itemDescription.charAt(itemDescription.length()-1) == '"')
                    ) {
                        itemDescription = '"' + itemDescription + '"';
                    } 

                    // Build row string from data elements, separated by commas
                    String row = (
                        departmentName + ", " +
                        subCategoryName + ", " +
                        itemName + ", " + 
                        itemDescription + "\n"
                    );

                    Files.writeString(file.toPath(), row, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            }
        }
    }

    /*
     * Utility method to generate file name for backup file
     */
    private String getBackupFileName(String fileName) throws IllegalArgumentException {
        String[] fileNameElements = fileName.split("\\.");
        if (fileNameElements.length != 2) {
            throw new IllegalArgumentException("Unexpected file name.");
        }
        String baseName = fileNameElements[0];
        String extension = fileNameElements[1];
        String backupFileName = baseName + "_bak." + extension;
        return backupFileName;
    }

    /*
     * Add provided item with it's corresponding fields to the internal data structure (i.e. the inventory)
     */
    public void addItem(
        String departmentName, 
        String subCategoryName, 
        String itemName, 
        String itemDescription
        ) throws DuplicateKeyException {

        HashMap<String, HashMap<String, Item>> subCategories;
        HashMap<String, Item> items;
        
        if (this.inventory.containsKey(departmentName)) {
            subCategories = inventory.get(departmentName);
        } else {
            subCategories = new HashMap<String, HashMap<String, Item>>();
            inventory.put(departmentName, subCategories);
        }

        if (subCategories.containsKey(subCategoryName)) {
            items = subCategories.get(subCategoryName);
        } else {
            items = new HashMap<String, Item>();
            subCategories.put(subCategoryName, items);
        }

        if (items.containsKey(itemName)) {
            throw new DuplicateKeyException("Item with that name already exists. You can update it, or delete it first");
        }

        if (
            itemDescription.charAt(0) == '"' && 
            itemDescription.charAt(itemDescription.length()-1) == '"') {
                itemDescription = itemDescription.substring(1, itemDescription.length()-1);
            }

        Item item = new Item(itemName, itemDescription);
        items.put(itemName, item);
    }

    /* Check if provided item is in the inventory */
    public boolean hasItem(String departmentName, String subCategoryName, String itemName) {
        if (!inventory.containsKey(departmentName)) {
            return false;
        }
        HashMap<String, HashMap<String, Item>> subCategories = inventory.get(departmentName);

        if (!subCategories.containsKey(subCategoryName)) {
            return false;
        }
        HashMap<String, Item> items = subCategories.get(subCategoryName);

        return items.containsKey(itemName);
    }

    /* If provided item is in the inventory, return it (the Item object)
     * Note: returns null otherwise
     */
    public Item getItem(String departmentName, String subCategoryName, String itemName) {
        if (hasItem(departmentName, subCategoryName, itemName)) {
            return inventory.get(departmentName).get(subCategoryName).get(itemName);
        } else {
            return null;
        }
    }

    /* If provided item is in the inventory, delete it (the Item object)
     * Note: silently returns otherwise
     */
    public void deleteItem(String departmentName, String subCategoryName, String itemName) {
        if (hasItem(departmentName, subCategoryName, itemName)) {
            inventory.get(departmentName).get(subCategoryName).remove(itemName);
        } 
    }

}