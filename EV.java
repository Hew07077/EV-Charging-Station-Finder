import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SELEVNetSystem {

    // Structure representing an EV Station Node
    static class Station {
        int id;
        String name;

        public Station(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    // Graph Data Structure for SELEV-Net
    static class EVGraph {
        private final Map<Integer, Station> stationMap = new HashMap<>();
        private final Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

        public void addStation(int id, String name) {
            stationMap.put(id, new Station(id, name));
            adjacencyList.putIfAbsent(id, new ArrayList<>());
        }

        public void addEdge(int u, int v) {
            if (adjacencyList.containsKey(u) && adjacencyList.containsKey(v)) {
                if (!adjacencyList.get(u).contains(v)) adjacencyList.get(u).add(v);
                if (!adjacencyList.get(v).contains(u)) adjacencyList.get(v).add(u);
            }
        }

        public Station getStation(int id) {
            return stationMap.get(id);
        }

        public Map<Integer, Station> getAllStations() {
            return stationMap;
        }

        public List<Integer> getNeighbors(int id) {
            return adjacencyList.getOrDefault(id, new ArrayList<>());
        }

        // BFS Shortest Path Algorithm for Emergency EV Routing
        public BFSResult findEmergencyRoute(int startId, int targetId) {
            if (!stationMap.containsKey(startId) || !stationMap.containsKey(targetId)) {
                return null;
            }

            Queue<Integer> queue = new LinkedList<>();
            Map<Integer, Boolean> visited = new HashMap<>();
            Map<Integer, Integer> parent = new HashMap<>();
            Map<Integer, Integer> hopDistance = new HashMap<>();

            for (int id : stationMap.keySet()) {
                visited.put(id, false);
                parent.put(id, null);
                hopDistance.put(id, 0);
            }

            visited.put(startId, true);
            queue.add(startId);

            boolean found = false;

            while (!queue.isEmpty()) {
                int current = queue.poll();

                if (current == targetId) {
                    found = true;
                    break;
                }

                for (int neighbor : adjacencyList.get(current)) {
                    if (!visited.get(neighbor)) {
                        visited.put(neighbor, true);
                        parent.put(neighbor, current);
                        hopDistance.put(neighbor, hopDistance.get(current) + 1);
                        queue.add(neighbor);
                    }
                }
            }

            if (!found) return new BFSResult(new ArrayList<>(), -1);

            // Reconstruct the shortest path sequence
            List<Integer> path = new ArrayList<>();
            Integer curr = targetId;
            while (curr != null) {
                path.add(0, curr);
                curr = parent.get(curr);
            }

            return new BFSResult(path, hopDistance.get(targetId));
        }
    }

    // Result Wrapper for BFS Computation
    static class BFSResult {
        List<Integer> pathNodeIds;
        int totalHops;

        public BFSResult(List<Integer> pathNodeIds, int totalHops) {
            this.pathNodeIds = pathNodeIds;
            this.totalHops = totalHops;
        }
    }

    private static final EVGraph evNetwork = new EVGraph();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeSELEVNetData();
        showWelcomeBanner();

        while (true) {
            displayMainMenu();
            System.out.print("Selection: ");
            String choice = scanner.nextLine().trim();

            switch (choice.toUpperCase()) {
                case "1":
                case "A":
                    displayAllStations();
                    break;
                case "2":
                case "B":
                    handleEmergencyRouting();
                    break;
                case "3":
                case "C":
                    displayAdjacencyList();
                    break;
                case "4":
                case "D":
                    displayGraphGUI(null);
                    break;
                case "0":
                case "EXIT":
                    System.out.println("\nExiting SELEV-Net Routing System. Safe driving!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid selection. Please try again.\n");
            }
        }
    }

    private static void initializeSELEVNetData() {
        // Register 40 Selangor EV Charging Stations
        evNetwork.addStation(1, "The Starling Mall");
        evNetwork.addStation(2, "XPark Sunway Serene");
        evNetwork.addStation(3, "Jaguar Land Rover, Ara Damansara");
        evNetwork.addStation(4, "Petronas NKVE");
        evNetwork.addStation(5, "BYD Ara Damansara (TNBX)");
        evNetwork.addStation(6, "MG Motor Petaling Jaya by DC Handal");
        evNetwork.addStation(7, "Perdana View Condo");
        evNetwork.addStation(8, "Lebuhraya Damansara-Puchong (LDP)");
        evNetwork.addStation(9, "Megah Rise Mall");
        evNetwork.addStation(10, "Hyundai Ara Damansara (TNBX)");
        evNetwork.addStation(11, "Jaya Shopping Centre");
        evNetwork.addStation(12, "Thomson Hospital");
        evNetwork.addStation(13, "Gentari @ Menara KLK");
        evNetwork.addStation(14, "Paradigm Mall");
        evNetwork.addStation(15, "The Summit USJ");
        evNetwork.addStation(16, "Damansara Mall (3 Damansara)");
        evNetwork.addStation(17, "Persiaran Bakti");
        evNetwork.addStation(18, "Kelana D’Putera Condominium");
        evNetwork.addStation(19, "Subang Parade");
        evNetwork.addStation(20, "Subang Jaya Medical Centre");
        evNetwork.addStation(21, "The Glades Plaza");
        evNetwork.addStation(22, "Nu Empire Shopping Gallery");
        evNetwork.addStation(23, "Main Place Mall");
        evNetwork.addStation(24, "E10");
        evNetwork.addStation(25, "UOA Business Park Glenmarie");
        evNetwork.addStation(26, "XPENG Glenmarie");
        evNetwork.addStation(27, "Concorde Hotel Shah Alam");
        evNetwork.addStation(28, "Smile Hotel Shah Alam");
        evNetwork.addStation(29, "KSL Automobile Enterprise");
        evNetwork.addStation(30, "Sime Darby Auto Selection Glenmarie");
        evNetwork.addStation(31, "Zaibar Automobile Industries");
        evNetwork.addStation(32, "Columbia Hospital Bukit Rimau");
        evNetwork.addStation(33, "Jalan Usahawan 2");
        evNetwork.addStation(34, "Petronas Bukit Jelutong ELITE");
        evNetwork.addStation(35, "IOI Mall Puchong");
        evNetwork.addStation(36, "IOI Rio City Puchong");
        evNetwork.addStation(37, "Quayside Mall Gamuda Kemuning");
        evNetwork.addStation(38, "Shell LDP D'Alpinia");
        evNetwork.addStation(39, "Petronas Dengkil R&R Northbound");
        evNetwork.addStation(40, "Petronas Temasya Federal Highway");

        // Map the 52 Highway Segments (Edges)
        int[][] connections = {
            {1, 4}, {1, 6}, {1, 8}, {1, 11},
            {2, 6}, {2, 8}, {2, 14}, {2, 30},
            {3, 4}, {3, 5}, {3, 10}, {3, 17},
            {4, 5}, {4, 18},
            {5, 10}, {5, 25},
            {6, 11}, {6, 40},
            {7, 8}, {7, 13}, {7, 16},
            {8, 9}, {8, 35}, {8, 38},
            {9, 11}, {9, 15}, {9, 16},
            {10, 12}, {10, 17},
            {11, 16},
            {12, 13},
            {14, 18}, {14, 20}, {14, 25},
            {15, 16}, {15, 20}, {15, 28},
            {17, 19},
            {19, 20}, {19, 22}, {19, 23}, {19, 26},
            {20, 23},
            {21, 23}, {21, 27}, {21, 37},
            {22, 23}, {22, 28},
            {24, 33}, {24, 39},
            {25, 26}, {25, 30}, {25, 40},
            {26, 30}, {26, 31},
            {27, 31}, {27, 34},
            {29, 31}, {29, 32},
            {31, 34},
            {32, 37},
            {33, 39},
            {35, 30}, {35, 36},
            {36, 38},
            {38, 39}
        };

        for (int[] edge : connections) {
            evNetwork.addEdge(edge[0], edge[1]);
        }
    }

    private static void showWelcomeBanner() {
        System.out.println("==========================================================================");
        System.out.println(" Welcome to SELEV-Net (Selangor EV Routing System) ");
        System.out.println(" Unweighted BFS Network Traversal | Emergency Station Locator ");
        System.out.println("==========================================================================\n");
    }

    private static void displayMainMenu() {
        System.out.println("Main Menu (Press '0' to Exit)");
        System.out.println("--------------------------------------------------");
        System.out.println("Option 1 [A]: View All 40 EV Stations");
        System.out.println("Option 2 [B]: Find Emergency BFS Shortest Route (Minimum Hops)");
        System.out.println("Option 3 [C]: View Network Connectivity (Adjacency List)");
        System.out.println("Option 4 [D]: Launch Interactive Network GUI Map");
        System.out.println("--------------------------------------------------");
    }

    private static void displayAllStations() {
        System.out.println("\n================ REGISTERED EV STATIONS (V = 40) ================");
        for (Map.Entry<Integer, Station> entry : evNetwork.getAllStations().entrySet()) {
            System.out.printf("[%2d] %s%n", entry.getKey(), entry.getValue().name);
        }
        System.out.println("=================================================================\n");
    }

    private static void displayAdjacencyList() {
        System.out.println("\n================ NETWORK ADJACENCY LIST ================");
        for (int i = 1; i <= 40; i++) {
            Station s = evNetwork.getStation(i);
            List<Integer> neighbors = evNetwork.getNeighbors(i);
            System.out.printf("[%2d] %-38s -> %s%n", s.id, s.name, neighbors.toString());
        }
        System.out.println("========================================================\n");
    }

    private static void handleEmergencyRouting() {
        System.out.println("\n---------------- EMERGENCY ROUTE COMPUTATION ----------------");
        try {
            System.out.print("Enter Start Station ID (1-40): ");
            int startId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Target Station ID (1-40): ");
            int targetId = Integer.parseInt(scanner.nextLine().trim());

            Station startStation = evNetwork.getStation(startId);
            Station targetStation = evNetwork.getStation(targetId);

            if (startStation == null || targetStation == null) {
                System.out.println("Error: Invalid station ID entered.\n");
                return;
            }

            BFSResult result = evNetwork.findEmergencyRoute(startId, targetId);

            if (result == null || result.totalHops == -1) {
                System.out.println("\nNo reachable path exists between the selected stations.\n");
            } else {
                System.out.println("\n================ EMERGENCY BFS ROUTE FOUND ================");
                System.out.println("From  : " + startStation.name + " [Node " + startId + "]");
                System.out.println("To    : " + targetStation.name + " [Node " + targetId + "]");
                System.out.println("Total Interchanges (Hops): " + result.totalHops);
                System.out.println("-----------------------------------------------------------");
                System.out.println("Optimal Route Sequence:");

                for (int i = 0; i < result.pathNodeIds.size(); i++) {
                    int nodeId = result.pathNodeIds.get(i);
                    Station st = evNetwork.getStation(nodeId);
                    System.out.printf("  Step %2d: [%2d] %s%n", i, st.id, st.name);
                }
                System.out.println("===========================================================\n");

                // Offer to visualize the computed route in GUI
                System.out.print("Do you want to render this route on the GUI Map? (Y/N): ");
                String guiChoice = scanner.nextLine().trim();
                if (guiChoice.equalsIgnoreCase("Y")) {
                    displayGraphGUI(result.pathNodeIds);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numerical IDs between 1 and 40.\n");
        }
    }

    // Graphical Representation Rendering (Swing Map Window)
    private static void displayGraphGUI(List<Integer> highlightedPath) {
        JFrame frame = new JFrame("SELEV-Net Topology Map");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // Compute circular positions for all 40 vertices
                Map<Integer, Point> positions = new HashMap<>();
                double centerX = width / 2.0;
                double centerY = height / 2.0;
                double radius = Math.min(width, height) * 0.38;

                for (int id = 1; id <= 40; id++) {
                    double angle = 2 * Math.PI * (id - 1) / 40.0;
                    int x = (int) (centerX + radius * Math.cos(angle));
                    int y = (int) (centerY + radius * Math.sin(angle));
                    positions.put(id, new Point(x, y));
                }

                Set<String> drawnEdges = new HashSet<>();

                // 1. Draw Default Edges
                g2d.setStroke(new BasicStroke(1));
                g2d.setColor(new Color(200, 200, 200));

                for (int id = 1; id <= 40; id++) {
                    Point p1 = positions.get(id);
                    for (int neighbor : evNetwork.getNeighbors(id)) {
                        String edgeKey = Math.min(id, neighbor) + "-" + Math.max(id, neighbor);
                        if (!drawnEdges.contains(edgeKey)) {
                            drawnEdges.add(edgeKey);
                            Point p2 = positions.get(neighbor);
                            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                    }
                }

                // 2. Highlight Shortest Path (If available)
                if (highlightedPath != null && highlightedPath.size() > 1) {
                    g2d.setStroke(new BasicStroke(4));
                    g2d.setColor(new Color(220, 20, 60)); // Crimson line for route

                    for (int i = 0; i < highlightedPath.size() - 1; i++) {
                        Point p1 = positions.get(highlightedPath.get(i));
                        Point p2 = positions.get(highlightedPath.get(i + 1));
                        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }

                // 3. Draw Nodes (Vertices)
                int nodeRadius = 12;
                for (int id = 1; id <= 40; id++) {
                    Point p = positions.get(id);

                    if (highlightedPath != null && highlightedPath.contains(id)) {
                        if (id == highlightedPath.get(0)) {
                            g2d.setColor(new Color(34, 139, 34)); // Start Node: Green
                        } else if (id == highlightedPath.get(highlightedPath.size() - 1)) {
                            g2d.setColor(new Color(255, 69, 0)); // Target Node: Orange-Red
                        } else {
                            g2d.setColor(new Color(220, 20, 60)); // Path Node: Crimson
                        }
                    } else {
                        g2d.setColor(new Color(70, 130, 180)); // Regular Node: Steel Blue
                    }

                    g2d.fillOval(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

                    // Render Node ID
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                    g2d.drawString(String.valueOf(id), p.x - 4, p.y + 4);
                }
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }
}