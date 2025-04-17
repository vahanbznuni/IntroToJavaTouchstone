package Program;

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

    @Override
    public String toString() {
        String output = "Item Name: ";
        output += name + "\n";
        output += "Item Description: ";
        output += description + "\n";

        return output;
    }

}
