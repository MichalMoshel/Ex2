# Spreadsheet GUI with Basic Formulas and Cell Referencing

This project is a Java-based graphical user interface (GUI) application for managing an Excel-like spreadsheet. It enables users to input data, use formulas, and reference cells efficiently.

## Features

- **Basic Arithmetic Formulas**:
  - Supports operations like addition, subtraction, multiplication, and division.
  - Examples: `=1`, `=1+2`, `=1+2*3`, `=(1+2)*3-1`.

- **Cell Referencing**:
  - Reference values from other cells using their coordinates.
  - Examples: `=A1`, `=A2+3`, `=(2+A3)/A2`.

- **Error Handling**:
  - Detects circular references and invalid formulas.
  - Displays error messages like `#CYCLE` or `#FORM` when issues arise.

- **GUI Features**:
  - Dynamic, interactive interface for inputting and managing data.
  - Utilizes the `StdDraw` library for rendering the spreadsheet.
  - Real-time updates to cell values based on formulas and references.

- **File Operations**:
  - Save and load spreadsheets as CSV files.

## Installation and Usage

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

## How to Use

1. **Launching the Application**:
  - Open the application to see an empty spreadsheet grid.

2. **Entering Data**:
  - Click on a cell to activate input.
  - Type a number, text, or formula prefixed with `=`.

3. **Using Formulas**:
  - Example: Enter `=A1+B2` to add the values of cells A1 and B2.

4. **Saving and Loading**:
  - Save your spreadsheet to a CSV file using the `save()` method.
  - Load a previously saved file using the `load()` method.

## Example Formulas

| Input            | Description                             |
|------------------|-----------------------------------------|
| `=1+2`          | Adds 1 and 2, displays 3                |
| `=A1`           | Displays the value of cell A1           |
| `=(2+A3)/A2`    | Performs arithmetic with cell references |

## Screenshots

![Application Screenshot](example.png)

_**Figure 1**: Spreadsheet GUI showcasing formulas and results._

## Contributing

Contributions are welcome! Feel free to fork the repository, create a branch, and submit a pull request.

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

