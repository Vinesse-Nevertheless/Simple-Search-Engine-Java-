package search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

enum Strategies {
    ALL, ANY, NONE
}

public class Main {
    static Map<String, Set<Integer>> invertedIndex = new HashMap<>();

    public static void main(String[] args) {
        new CommandArgValidator().validateCommands(args);

        Main m = new Main();
        m.readFile(args);

    }

    void readFile(String[] args) {
        Path path = Path.of(args[1]);
        List<String> lineList;

        try {
            lineList = Files.readAllLines(path);
            AtomicInteger lineNum = new AtomicInteger(0);
            lineList.stream().map(String::strip)
                    .map(x -> x.replaceAll(" {2}+", " "))
                    .map(String::toLowerCase)
                    .map(x -> x.split(" "))
                    .forEach(l -> createInvertedIndex(l, lineNum.getAndIncrement()));
            runProgram(lineList);
        } catch (IOException e) {
            System.out.println("File does not exist.");
            throw new RuntimeException(e);
        }
    }

    /*
    To optimize your program, you can use a data structure called an Inverted Index.
    It maps each word to all positions/lines/documents in which the word occurs. As a result,
    when we receive a query, we can immediately find the answer without any comparisons.
    You can implement it using the Map class. It connects an item with a list (or set)
    of indexes belonging to the lines that contain the item.
     */
    static void createInvertedIndex(String[] line, int lineNum) {
        for (int i = 0; i < line.length; i++) {
            if (invertedIndex.containsKey(line[i])) {
                invertedIndex.get(line[i]).add(lineNum);
            } else if (!line[i].isBlank()) {
                Set<Integer> lineIndexSet = new HashSet<>();
                lineIndexSet.add(lineNum);
                invertedIndex.put(line[i], lineIndexSet);
            }
        }
    }

    void runProgram(List<String> group) {
        UserInputRequester userInputRequest = new UserInputRequester();
        while (true) {
            Printer.printMenu();
            int menuNum = userInputRequest.getMenuNumber();

            switch (menuNum) {
                case 0 -> {
                    System.out.println();
                    System.out.println("Bye!");
                    userInputRequest.closeResources();
                    return;
                }
                case 1 -> {
                    Strategies strategy = userInputRequest.getMatchingStrategy();

                    Set<String> found = userInputRequest.getFoundPeopleSet(group, strategy);
                    Printer.printSearchResults(found);
                }
                case 2 -> Printer.printPeopleList(group);
                default -> {
                    System.out.println();
                    System.out.println("Incorrect option! Try again.");
                }
            }
        }
    }
}

class CommandArgValidator{

    void validateCommands(String[] args) {
        if (args.length < 2 || args[0].isEmpty() || args[1].isEmpty()) {
            System.out.println("Must specify --data command and filename");
            return;
        }
        if (!args[0].equalsIgnoreCase("--data")) {
            System.out.println("Must specify --data command");
            return;
        }
    }
}

class SearchEngine {
    Set<String> findALL(Set<String> splitTarget, List<String> group) {
        Set<String> found = new HashSet<>();

        Set<Integer> rowSet = null;
        Set<Integer> matchingRow = new HashSet<>();

        for (String target : splitTarget) {
            if (isValidInput(target) && Main.invertedIndex.containsKey(target)) {
                if (rowSet == null) {
                    rowSet = Main.invertedIndex.get(target);
                } else {
                    for (Integer row : Main.invertedIndex.get(target)) {
                        if (rowSet.contains(row)) {
                            matchingRow.add(row);
                        }
                    }
                }
            } else {
                matchingRow = new HashSet<>();
                break;
            }
        }
        if (matchingRow.size() == 1) {
            for (Integer row : matchingRow) {
                found.add(group.get(row));
            }
        }
        return found;
    }

    Set<String> findANY(String[] splitTarget, List<String> group) {
        Set<String> found = new HashSet<>();

        for (int i = 0; i < splitTarget.length; i++) {
            String target = splitTarget[i];
            if (isValidInput(target) && Main.invertedIndex.containsKey(target)) {
                for (Integer row : Main.invertedIndex.get(target)) {
                    found.add(group.get(row));
                }
            }
        }
        return found;
    }

    Set<String> findNONE(String[] splitTarget, List<String> group) {
        Set<String> found = new HashSet<>(group);
        for (int i = 0; i < splitTarget.length; i++) {
            String target = splitTarget[i];
            if (isValidInput(target) && Main.invertedIndex.containsKey(target)) {
                for (Integer row : Main.invertedIndex.get(target)) {
                    found.remove(group.get(row));
                }
            }
        }
        return found;
    }

    boolean isValidInput(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        return true;
    }
}

class Printer {
    private final static String[] menuOptions = {"0. Exit", "1. Find a person", "2. Print all people"};

    static void printSearchResults(Set<String> found) {
        System.out.println();
        if (found.isEmpty()) {
            System.out.println("No matching people found.");
        } else {
            System.out.println(found.size() + " persons found:");
            found.forEach(System.out::println);
        }
    }

    static void printPeopleList(List<String> group) {
        System.out.println();
        for (int i = 0; i < group.size(); i++) {
            System.out.println(group.get(i));
        }
    }

    static void printMenu() {
        System.out.println();
        System.out.println("=== Menu ===");
        for (int i = 1; i < menuOptions.length; i++) {
            System.out.println(menuOptions[i]);
        }

        System.out.println(menuOptions[0]);

    }
}

class UserInputRequester {
    Scanner in = new Scanner(System.in);

    int getMenuNumber() {
        int menuNum = -1;
        try {
            menuNum = in.nextInt();
            in.nextLine();
        } catch (RuntimeException ignore) {
        }

        return menuNum;
    }

    Strategies getMatchingStrategy() {

        while (true) {
            System.out.println();
            System.out.print("Select a matching strategy: ");

            for (int i = 0; i < Strategies.values().length; i++) {
                System.out.print(Strategies.values()[i]);
                if (i < Strategies.values().length - 1) {
                    System.out.print(", ");
                } else {
                    System.out.println();
                }
            }

            if (!in.hasNextLine()) {
                in.nextLine();
            }
            String strategy = in.nextLine();

            for (int i = 0; i < Strategies.values().length; i++) {
                if (strategy.equals(Strategies.values()[i].toString())) {
                    return Strategies.values()[i];
                }
            }

            System.out.println();
            System.out.println("No such strategy");
        }
    }

    Set<String> getFoundPeopleSet(List<String> group, Strategies strategy) {
        SearchEngine searchEngine = new SearchEngine();

        Set<String> found = new HashSet<>();
        System.out.println();

        System.out.println("Enter a name or email to search all suitable people.");

        String targetWord = in.nextLine().toLowerCase();
        targetWord = targetWord.replaceAll(" {2}+", " ");
        String[] splitTarget = targetWord.split(" ");


        switch (strategy) {
            case ALL -> {
                Set<String> splitSet = Arrays.stream(splitTarget).collect(Collectors.toSet());
                if (splitSet.size() == 1){
                    found = searchEngine.findANY(splitTarget, group);
                }else {
                    found = searchEngine.findALL(splitSet, group);
                }
            }
            case ANY -> {
                found = searchEngine.findANY(splitTarget, group);
            }
            case NONE -> {
                found = searchEngine.findNONE(splitTarget, group);
            }
        }

        return found;
    }

    void closeResources() {
        in.close();
    }
}