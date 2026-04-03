# High-Performance Java Search Engine

A robust command-line search utility designed to process large text datasets using an **Inverted Index**. This engine provides $O(1)$ search performance for individual terms and supports complex Boolean retrieval strategies (**ALL**, **ANY**, and **NONE**).

## 🚀 Key Features

* **Inverted Index Architecture:** Unlike linear search engines that scan every line for every query, this engine builds a map of unique tokens to line-number sets during initialization. This ensures that search time remains nearly constant regardless of file size.
* **Boolean Strategy Patterns:** 
    * **ALL:** Returns records containing *every* word in the query (Logical Intersection).
    * **ANY:** Returns records containing *at least one* word from the query (Logical Union).
    * **NONE:** Returns records that do not contain *any* of the query terms (Logical Complement).
* **Strict Token Matching:** Prevents "partial match" errors (e.g., a search for `mail` will correctly ignore `harrington@gmail.com`).
* **Modern Java Implementation:** Leverages the **Java Stream API**, `AtomicInteger` for thread-safe indexing, and the **Collections Framework** (HashMaps/HashSets) for optimal memory management.

## 🛠️ Technical Specifications

### Data Structures
* **Map<String, Set<Integer>>:** The core Inverted Index. Maps lowercase tokens to a Set of line indices to ensure automatic deduplication and $O(1)$ lookups.
* **Enum-based Strategies:** Search logic is decoupled from the main execution flow using a strategy-based approach.

### Performance
* **Indexing:** $O(N)$ where $N$ is the total number of words in the source file.
* **Search:** $O(1)$ for single-word lookups; $O(M \times K)$ for multi-word Boolean operations (where $M$ is the number of query terms and $K$ is the average number of occurrences).

---

## 💻 Usage

### Prerequisites
* Java JDK 17 or higher.

### Installation & Execution
1. Clone the repository:
   ```bash
   git clone https://github.com/Vinesse-Nevertheless/Simple-Search-Engine-Java-.git

2. Navigate to the source directory and compile:
   ```bash
   javac search/*.java

3. Run the application by providing a data source via the '--data' flag:
   ```bash
   java search.Main --data people.txt

### Example Input File (`people.txt`)
```text
Katie Jacobs
Erick Harrington harrington@gmail.com
Myrtle Medina
Erick Burgess
```

### Example Search (Strategy: ALL)
**Query:** `Erick Harrington`  
**Result:** `1 persons found: Erick Harrington harrington@gmail.com`

### 🏗️ Architecture
The project is structured into distinct classes to follow **SOLID** principles:

* **Main:** Handles application entry and file ingestion.
* **SearchEngine:** Contains the pure mathematical set logic for search strategies.
* **UserInputRequester:** Manages stateful CLI interaction and input sanitization.
* **Printer:** Handles UI formatting and result display.

   
