import java.util.*;

public class EV {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String OPERATOR_ID = "EV01";
    private static final EVNetwork network = new EVNetwork();

    public static void main(String[] args) {
        // Initialize with default demo stations
        network.initDefaultGraph();

        while (true) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleGraphModificationMenu();
                    break;
                case "2":
                    handleRoutingSearch();
                    break;
                case "3":
                    network.displayNetworkVisual();
                    break;
                case "4":
                    System.out.println("\nExiting SELEV-Net System. Stay safe and drive electric!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("⚠️ Invalid selection! Please enter 1-4.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n============================================================");
        System.out.println("       Welcome to SELEV-Net (Selangor EV Routing System)    ");
        System.out.println("============================================================");
        System.out.printf("%54s: %s\n", "Operator ID", OPERATOR_ID);
        System.out.println("\nMain Menu (Press '0' at any prompt to return here)");
        System.out.println("------------------------------------------------------------");
        System.out.println("1. Create / Modify Graph Network");
        System.out.println("2. Route & Search for a Charging Station");
        System.out.println("3. View the SELEV-Net Routing Network");
        System.out.println("4. Exit");
        System.out.print("Selection: ");
    }

    private static void handleGraphModificationMenu() {
        while (true) {
            System.out.println("\n------------------------------------------------------------");
            System.out.println("Graph Modification Menu:");
            System.out.println("------------------------------------------------------------");
            System.out.println("1. Add a Station (Vertex)");
            System.out.println("2. Remove a Station (Vertex)");
            System.out.println("3. Add a Connection (Edge)");
            System.out.println("4. Remove a Connection (Edge)");
            System.out.println("5. Return to Main Menu");
            System.out.print("Selection: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("5") || choice.equals("0")) return;

            switch (choice) {
                case "1":
                    do {
                        System.out.print("\nEnter new charging station name: ");
                        String station = scanner.nextLine().trim();
                        if (station.equals("0")) break;
                        
                        if (network.addVertex(station)) {
                            System.out.println("✅ Station '" + station + "' added successfully.");
                        } else {
                            System.out.println("⚠️ Station already exists or name is invalid.");
                        }
                        System.out.print("Add another station? (Y/N): ");
                    } while (scanner.nextLine().trim().equalsIgnoreCase("Y"));
                    break;

                case "2":
                    do {
                        System.out.print("\nEnter station name to remove: ");
                        String station = scanner.nextLine().trim();
                        if (station.equals("0")) break;

                        if (network.removeVertex(station)) {
                            System.out.println("✅ Station '" + station + "' and all its connections removed.");
                        } else {
                            System.out.println("⚠️ Station not found.");
                        }
                        System.out.print("Remove another station? (Y/N): ");
                    } while (scanner.nextLine().trim().equalsIgnoreCase("Y"));
                    break;

                case "3":
                    do {
                        System.out.print("\nEnter 1st station name: ");
                        String s1 = scanner.nextLine().trim();
                        if (s1.equals("0")) break;
                        System.out.print("Enter 2nd station name: ");
                        String s2 = scanner.nextLine().trim();
                        if (s2.equals("0")) break;

                        if (network.addEdge(s1, s2)) {
                            System.out.println("✅ Bidirectional link created between [" + s1 + "] and [" + s2 + "].");
                        } else {
                            System.out.println("⚠️ Error: Check if stations exist or if you tried linking a station to itself.");
                        }
                        System.out.print("Add another connection? (Y/N): ");
                    } while (scanner.nextLine().trim().equalsIgnoreCase("Y"));
                    break;

                case "4":
                    do {
                        System.out.print("\nEnter 1st station name: ");
                        String s1 = scanner.nextLine().trim();
                        if (s1.equals("0")) break;
                        System.out.print("Enter 2nd station name: ");
                        String s2 = scanner.nextLine().trim();
                        if (s2.equals("0")) break;

                        if (network.removeEdge(s1, s2)) {
                            System.out.println("✅ Connection between [" + s1 + "] and [" + s2 + "] removed.");
                        } else {
                            System.out.println("⚠️ Error: Connection or stations do not exist.");
                        }
                        System.out.print("Remove another connection? (Y/N): ");
                    } while (scanner.nextLine().trim().equalsIgnoreCase("Y"));
                    break;

                default:
                    System.out.println("⚠️ Invalid selection! Please enter 1-5.");
            }
        }
    }

    private static void handleRoutingSearch() {
        System.out.println("\n--- EV Routing & Station Search ---");
        System.out.print("Enter Starting Charging Station: ");
        String start = scanner.nextLine().trim();
        if (start.equals("0")) return;

        System.out.print("Enter Destination Charging Station: ");
        String destination = scanner.nextLine().trim();
        if (destination.equals("0")) return;

        network.findShortestRoute(start, destination);
    }
}

/**
 * Encapsulates the Graph structural logic and algorithms.
 */
class EVNetwork {
    private final Map<String, List<String>> adjacencyList = new LinkedHashMap<>();

    public void initDefaultGraph() {
        addVertex("The Starling Mall");
        addVertex("Petronas NKVE");
        addVertex("Jaguar Land Rover");
        addVertex("MG Motor PJ");

        addEdge("The Starling Mall", "Petronas NKVE");
        addEdge("The Starling Mall", "MG Motor PJ");
        addEdge("Petronas NKVE", "Jaguar Land Rover");
    }

    public boolean addVertex(String name) {
        if (name == null || name.isEmpty() || adjacencyList.containsKey(name)) {
            return false;
        }
        adjacencyList.put(name, new ArrayList<>());
        return true;
    }

    public boolean removeVertex(String name) {
        if (!adjacencyList.containsKey(name)) {
            return false;
        }
        // Safely remove references from neighbors
        for (String neighbor : adjacencyList.get(name)) {
            adjacencyList.get(neighbor).remove(name);
        }
        adjacencyList.remove(name);
        return true;
    }

    public boolean addEdge(String source, String destination) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination) || source.equals(destination)) {
            return false;
        }
        // Avoid duplicate links
        if (!adjacencyList.get(source).contains(destination)) {
            adjacencyList.get(source).add(destination);
        }
        if (!adjacencyList.get(destination).contains(source)) {
            adjacencyList.get(destination).add(source);
        }
        return true;
    }

    public boolean removeEdge(String source, String destination) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            return false;
        }
        boolean r1 = adjacencyList.get(source).remove(destination);
        boolean r2 = adjacencyList.get(destination).remove(source);
        return r1 || r2;
    }

    /**
     * Finds the shortest route via Breadth-First Search (BFS)
     */
    public void findShortestRoute(String start, String target) {
        if (!adjacencyList.containsKey(start) || !adjacencyList.containsKey(target)) {
            System.out.println("⚠️ Error: One or both stations do not exist in the network grid.");
            return;
        }

        if (start.equalsIgnoreCase(target)) {
            System.out.println("📍 You are already at your destination station!");
            return;
        }

        Queue<String> queue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        boolean pathFound = false;

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equalsIgnoreCase(target)) {
                pathFound = true;
                break;
            }

            for (String neighbor : adjacencyList.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (pathFound) {
            // Reconstruct the route back to front
            List<String> route = new LinkedList<>();
            String step = target;
            while (step != null) {
                route.add(0, step); // Add to beginning
                step = parentMap.get(step);
            }

            System.out.println("\n🟢 Optimal EV Route Found (" + (route.size() - 1) + " hops):");
            for (int i = 0; i < route.size(); i++) {
                System.out.print("[" + route.size() + "]"); // Just an index indicator
                System.out.print(route.get(i));
                if (i < route.size() - 1) {
                    System.out.print(" ➔ ");
                }
            }
            System.out.println();
        } else {
            System.out.println("❌ Routing Error: No active topological pathway connects these two stations.");
        }
    }

    public void displayNetworkVisual() {
        System.out.println("\n--- SELEV-Net Active Topological Grid Layers ---");
        if (adjacencyList.isEmpty()) {
            System.out.println("[Grid is currently empty]");
        }
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            System.out.println("[" + entry.getKey() + "]");
            if (entry.getValue().isEmpty()) {
                System.out.println("   └───> (No active connections)");
            }
            for (String neighbor : entry.getValue()) {
                System.out.println("   └───> Connected to: (" + neighbor + ")");
            }
        }
        System.out.println("-------------------------------------------------");
    }
}