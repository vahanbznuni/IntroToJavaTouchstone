**Problem Statement** 
This program is intended to be a basic custom inventory management solution. Users can deploy this program to manage personal inventories. It can also serve as a base for a professional inventory management solution, such as one for a store or a small business.

**Expected Input Data** 
The program accepts two types of inputs:
- User Input: Users can interact with the program through the CLI menu, where they can view, add, and delete items in the inventory, which will be broken up into categories and subcategories. For example, users could add (and then later access) an Outdoor Grill item in Garden and Outdoor category, Grills and Outdoor Cooking subcategory. 
- CSV File Input: On execution, the program will load a CSV file with inventory data, allowing pre-existing data to be accessed and updated. The CSV file is expected to adhere to a specific format, but the program will not perform validation on its content beyond possible basic structure checks.

For this project, a CSV file pre-filled with synthetic data (generated with a large language model) is used as the initial dataset.

**Solution Overview** 
Upon startup, the program will attempt to load inventory from the CSV file. If successful, the user will be presented with a menu of options. The user will choose an option by inputting the corresponding number, as directed by the prompt. Based on the user’s selections, additional menus will be displayed. Most of the menus will also include options to quit or go back to the previous menu. 

The following summarizes the primary functions, accessible from main menu and/or subsequent menus:
- Browse Inventory: Displays departments/categories to choose from, a choice from which leads to a menu of subcategories to choose from, a selection from which finally leads to a list of items in that subcategory.
- Add a New Item: Allows users to enter information for a new item, including category, subcategory, item name, and item description. Later versions of the program can expand to add price (if deploying for a business application), and other fields, as relevant.
- Edit or Delete Contact: While viewing an item’s details, users can choose to edit individual fields or delete the item altogether.
- Exit: Exits the program, saving any changes back to the CSV file for data persistence.

**Expected Output and Results** 
- CLI Output: The program will display menus for performing CRUD operations with the inventory to the CLI. 
- CSV File Output: Upon exiting, any modifications made to the inventory during the session are saved back to the CSV file, ensuring persistence between program runs. An optional feature (which may or may not be deployed with the current draft) will be to make a backup copy of the input CSV, in case the user makes undesired changes, or in case the program crashes with an inconsistent state.

The result will be a functional inventory management solution accessible via CLI, capable of loading, displaying, updating, and persisting an inventory through a CSV file.

**Additional Details** 
The program is implemented using object-oriented programming (OOP) principles, prioritizing scalability, modularity, and separation of concerns. Future extensions could include integrating with other applications, enhancing the CLI interface, or expanding data validation for the CSV input.

**Motivation** 
As with other projects of similar scope and complexity, the main motivation of working on this program for the project was to create a solution that balances simplicity of design and practical application. An inventory management solution project provides a great deal of range in complexity: from a very basic and simple application akin to a to-do-list, to a sophisticated e-commerce platform. That provides for a great opportunity to start small, utilize skills in basic programming to deploy a usable and practical application, and to iteratively build in complexity and funcitonality as needed.

