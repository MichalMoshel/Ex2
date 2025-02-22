# Spreadsheet GUI with Basic Formulas and Cell Referencing

This project is a Java-based graphical user interface (GUI) application for managing an Excel-like spreadsheet. It enables users to input data, use formulas, and reference cells efficiently.

## Features

### Basic Arithmetic Formulas
- Supports operations like addition, subtraction, multiplication, and division.
- Examples:
    - `=1`
    - `=1+2`
    - `=1+2*3`
    - `=(1+2)*3-1`

### Cell Referencing
- Reference values from other cells using their coordinates.
- Examples:
    - `=A1`
    - `=A2+3`
    - `=(2+A3)/A2`

### Error Handling
- Detects circular references and invalid formulas.
- Displays error messages:
    - `#CYCLE` for circular references.
    - `#FORM` for invalid formula structures.
- Examples of issues:
    - Circular reference: `=A1` in cell `A2` where `A2` references `A1`.
    - Invalid formula: `=A1++B2` or `=A1+`.

### GUI Features
- Dynamic, interactive interface for inputting and managing data.
- Utilizes the `StdDraw` library for rendering the spreadsheet.
- Real-time updates to cell values based on formulas and references.

### File Operations
- Save and load spreadsheets in CSV format.
- Ensures compatibility for importing and exporting data seamlessly.
- **CSV Format**:
    - Each row includes `x,y,value`.
    - Example:
      ```csv
      0,0,=A1+B2
      1,1,42
      2,2,Hello
      ```

## Installation and Usage

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/MichalMoshel/Ex2.git
   ```

2. Compile the project:
   ```bash
   javac Ex2GUI.java
   ```

3. Run the application:
   ```bash
   java Ex2GUI
   ```

### Usage

#### Launching the Application
- Open the application to see an empty spreadsheet grid.

#### Entering Data
- Click on a cell to activate input.
- Type a number, text, or formula prefixed with `=`.

#### Using Formulas
- Examples:
    - Enter `=A1+B2` to add the values of cells `A1` and `B2`.
    - Use parentheses for precedence: `=(A1+B2)*C3`.

#### Saving and Loading
- Save your spreadsheet to a CSV file using the `save()` method.
- Load a previously saved file using the `load()` method.

### Example Formulas

| Input            | Description                             | Expected Output      |
|------------------|-----------------------------------------|----------------------|
| `=1+2`          | Adds 1 and 2.                          | `3`                  |
| `=A1`           | Displays the value of cell `A1`.       | (Value of `A1`)      |
| `=(2+A3)/A2`    | Performs arithmetic with cell references. | (Calculated Value)   |
| `=A1+(B2*C3)/D4`| Complex formula with multiple operations. | (Calculated Value)   |

## Code Structure

### Key Components
- **`SCell`**: Manages individual cell logic, including:
    - Value parsing.
    - Formula evaluation.
    - Dependency management.
    - **Key Methods**:
        - `setData(String s)`: Updates cell data and recalculates type and dependencies.
        - `getDependencies()`: Retrieves a list of all dependent cells in a formula.
    - **Example**:
      ```java
      SCell cell = new SCell("=A1+B2");
      System.out.println(cell.getDependencies()); // Prints [A1, B2]
      ```
- **`Ex2Sheet`**: Handles the spreadsheet grid, including:
    - Cell storage.
    - Recalculation of formulas.
    - File operations (save/load).
    - **Key Methods**:
        - `eval()`: Recalculates all formulas in the sheet.
        - `depth()`: Computes dependency depth for cycle detection.
    - **Example**:
      ```java
      Ex2Sheet sheet = new Ex2Sheet(10, 10);
      sheet.set(0, 0, "=A1+B2");
      sheet.eval();
      System.out.println(sheet.value(0, 0)); // Prints the computed value
      ```

## Screenshots

![Application Screenshot](example.png)

_**Figure 1**: Spreadsheet GUI showcasing formulas and results._

## Contributing

Contributions are welcome! Follow these steps to contribute:

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Description of changes"
   ```
4. Push to your branch:
   ```bash
   git push origin feature-name
   ```
5. Submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgments

- Inspired by the Java programming community.
- Special thanks to the authors of the `StdDraw` library used in the GUI.

