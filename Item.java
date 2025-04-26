/*
 * Class to represent an inventory item.
 * Includes Name and Description fields, accessor methods,
 * and a toString override to present the item's namd and description.
 */
public class Item {
    private String name;
    private String description;

    public Item(String name, String descripiton) {
        this.name = name;
        this.description = descripiton;
    }

    public String getName() {
        return name;

    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * Output item's name and description in a readable format.
     */
    @Override
    public String toString() {
        String output = "\nItem: \n" + name + "\n";
        output += "\nDescription: \n" + description + "\n";

        return output;
    }

}
