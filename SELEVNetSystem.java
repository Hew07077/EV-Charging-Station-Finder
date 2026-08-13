import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SELEVNetSystem {

    // Structure representing an EV Station Node with Real-World Coordinates
    static class Station {
        int id;
        String name;
        double lat;
        double lon;

        public Station(int id, String name, double lat, double lon) {
            this.id = id;
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
    }

    // Graph Data Structure for SELEV-Net
    static class EVGraph {
        private final Map<Integer, Station> stationMap = new HashMap<>();
        private final Map<Integer, List<Integer>> adjacencyList = new HashMap<>();
        private int nextAutoId = 1;

        public void addStation(int id, String name, double lat, double lon) {
            stationMap.put(id, new Station(id, name, lat, lon));
            adjacencyList.putIfAbsent(id, new ArrayList<>());
            if (id >= nextAutoId) {
                nextAutoId = id + 1;
            }
        }

        public int addStation(String name, double lat, double lon) {
            int id = nextAutoId++;
            stationMap.put(id, new Station(id, name, lat, lon));
            adjacencyList.putIfAbsent(id, new ArrayList<>());
            return id;
        }

        public boolean removeStation(int targetId) {
            if (!stationMap.containsKey(targetId)) return false;

            stationMap.remove(targetId);
            adjacencyList.remove(targetId);
            for (List<Integer> neighbors : adjacencyList.values()) {
                neighbors.remove(Integer.valueOf(targetId));
            }
            return true;
        }

        public void addEdge(int u, int v) {
            if (u == v) return;
            if (stationMap.containsKey(u) && stationMap.containsKey(v)) {
                if (!adjacencyList.get(u).contains(v)) adjacencyList.get(u).add(v);
                if (!adjacencyList.get(v).contains(u)) adjacencyList.get(v).add(u);
            }
        }

        public boolean addEdgeByIds(int id1, int id2) {
            if (id1 == id2) return false;
            if (!stationMap.containsKey(id1) || !stationMap.containsKey(id2)) return false;
            if (adjacencyList.get(id1).contains(id2)) return false;

            addEdge(id1, id2);
            return true;
        }

        public boolean removeEdgeByIds(int id1, int id2) {
            if (!stationMap.containsKey(id1) || !stationMap.containsKey(id2)) return false;

            boolean r1 = adjacencyList.get(id1).remove(Integer.valueOf(id2));
            boolean r2 = adjacencyList.get(id2).remove(Integer.valueOf(id1));
            return r1 || r2;
        }

        public Integer findStationIdByName(String name) {
            for (Station st : stationMap.values()) {
                if (st.name.equalsIgnoreCase(name.trim())) {
                    return st.id;
                }
            }
            return null;
        }

        // Helper to check if a station with the exact same latitude and longitude already exists
        public boolean hasStationAtCoordinates(double lat, double lon) {
            for (Station st : stationMap.values()) {
                // Using a small epsilon threshold for floating point coordinate comparison
                if (Math.abs(st.lat - lat) < 1e-6 && Math.abs(st.lon - lon) < 1e-6) {
                    return true;
                }
            }
            return false;
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
                    if (visited.get(neighbor) != null && !visited.get(neighbor)) {
                        visited.put(neighbor, true);
                        parent.put(neighbor, current);
                        hopDistance.put(neighbor, hopDistance.get(current) + 1);
                        queue.add(neighbor);
                    }
                }
            }

            if (!found) return new BFSResult(new ArrayList<>(), -1);

            List<Integer> path = new ArrayList<>();
            Integer curr = targetId;
            while (curr != null) {
                path.add(0, curr);
                curr = parent.get(curr);
            }

            return new BFSResult(path, hopDistance.get(targetId));
        }
    }

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

            switch (choice) {
                case "1":
                    handleCreateGraphMenu();
                    break;
                case "2":
                    handleEmergencyRouting();
                    break;
                case "3":
                    displayGraphGUI(null);
                    break;
                case "4":
                    displayAllStations();
                    break;
                case "5":
                    displayAdjacencyList();
                    break;
                case "0":
                    System.out.println("\nExiting system. Safe driving!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid selection. Try again.\n");
            }
        }
    }

    private static void showWelcomeBanner() {
        System.out.println("--------------------------------------------------");
        System.out.println(" Welcome to SELEV-Net (Selangor EV GIS Routing)   ");
        System.out.println(" Staff ID: MAS01                                  ");
        System.out.println("--------------------------------------------------\n");
    }

    private static void displayMainMenu() {
        System.out.println("Main Menu (Press '0' to exit)");
        System.out.println("--------------------------------------------------");
        System.out.println("1. Create / Edit Graph");
        System.out.println("2. Search Station / Emergency Route");
        System.out.println("3. View GIS World Map (GUI)");
        System.out.println("4. View All Stations");
        System.out.println("5. View Adjacency List");
        System.out.println("0. Exit");
        System.out.println("--------------------------------------------------");
    }

    private static void handleCreateGraphMenu() {
        while (true) {
            System.out.println("\nCreate & Edit Graph Network");
            System.out.println("--------------------------------------------------");
            System.out.println("1. Add a Vertex (Station)");
            System.out.println("2. Remove a Vertex (Station)");
            System.out.println("3. Add an Edge (Route Connection)");
            System.out.println("4. Remove an Edge (Route Connection)");
            System.out.println("0. Return to Main Menu");
            System.out.println("--------------------------------------------------");
            System.out.print("Selection: ");
            String subChoice = scanner.nextLine().trim();

            switch (subChoice) {
                case "1":
                    handleAddVertex();
                    break;
                case "2":
                    handleRemoveVertex();
                    break;
                case "3":
                    handleAddEdge();
                    break;
                case "4":
                    handleRemoveEdge();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid selection. Enter 1 to 4 or 0.");
            }
        }
    }

    private static void handleAddVertex() {
        Random random = new Random();
        while (true) {
            System.out.println("\n---------------- ADD NEW STATION ----------------");
            System.out.print("Enter Station Name (or press Enter to cancel): ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Cancelled adding station.");
                break;
            }

            if (evNetwork.findStationIdByName(name) != null) {
                System.out.println("❌ Error: A station with the name '" + name + "' already exists.");
            } else {
                double defaultLat = 2.7 + (3.3 - 2.7) * random.nextDouble();
                double defaultLon = 101.2 + (101.8 - 101.2) * random.nextDouble();

                double lat = defaultLat;
                double lon = defaultLon;

                System.out.printf("Enter Latitude  [Press Enter for default %.4f]: ", defaultLat);
                String latInput = scanner.nextLine().trim();
                if (!latInput.isEmpty()) {
                    try { lat = Double.parseDouble(latInput); } 
                    catch (NumberFormatException e) { System.out.println("Invalid input. Using default Latitude " + String.format("%.4f", defaultLat) + "."); }
                }

                System.out.printf("Enter Longitude [Press Enter for default %.4f]: ", defaultLon);
                String lonInput = scanner.nextLine().trim();
                if (!lonInput.isEmpty()) {
                    try { lon = Double.parseDouble(lonInput); } 
                    catch (NumberFormatException e) { System.out.println("Invalid input. Using default Longitude " + String.format("%.4f", defaultLon) + "."); }
                }

                // Check if a station already exists at these exact coordinates
                if (evNetwork.hasStationAtCoordinates(lat, lon)) {
                    System.out.println("❌ Error: A station already exists at coordinates (Lat: " + String.format("%.4f", lat) + ", Lon: " + String.format("%.4f", lon) + "). Station not added.");
                } else {
                    int newId = evNetwork.addStation(name, lat, lon);
                    System.out.println("✅ Success! Station '" + name + "' added as Node ID: [" + newId + "]");
                }
            }

            System.out.print("\nAdd another station? (Y/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) break;
        }
    }

    private static void handleRemoveVertex() {
        while (true) {
            System.out.println("\n-------------- REMOVE A STATION --------------");
            printQuickStationSummary();
            System.out.print("Enter Station Name or Node ID to remove (or Enter to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) break;

            Integer targetId = parseStationInput(input);

            if (targetId == null) {
                System.out.println("❌ Error: Could not find station '" + input + "'.");
            } else {
                Station st = evNetwork.getStation(targetId);
                String stName = st.name;
                if (evNetwork.removeStation(targetId)) {
                    System.out.println("✅ Success! Station [" + targetId + "] '" + stName + "' and all connected routes were removed.");
                } else {
                    System.out.println("❌ Error removing station.");
                }
            }

            System.out.print("\nRemove another station? (Y/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) break;
        }
    }

    private static void handleAddEdge() {
        while (true) {
            System.out.println("\n---------------- ADD ROUTE CONNECTION ----------------");
            printQuickStationSummary();
            
            System.out.print("Enter 1st Station (ID or Name): ");
            String input1 = scanner.nextLine().trim();
            Integer id1 = parseStationInput(input1);

            if (id1 == null) {
                System.out.println("❌ Error: Station '" + input1 + "' does not exist.");
            } else {
                System.out.print("Enter 2nd Station (ID or Name): ");
                String input2 = scanner.nextLine().trim();
                Integer id2 = parseStationInput(input2);

                if (id2 == null) {
                    System.out.println("❌ Error: Station '" + input2 + "' does not exist.");
                } else if (id1.equals(id2)) {
                    System.out.println("❌ Error: Cannot connect a station to itself.");
                } else {
                    boolean success = evNetwork.addEdgeByIds(id1, id2);
                    if (success) {
                        System.out.println("✅ Success! Route established between [" + id1 + "] " + evNetwork.getStation(id1).name + 
                                           " and [" + id2 + "] " + evNetwork.getStation(id2).name + ".");
                    } else {
                        System.out.println("❌ Error: Connection already exists between these stations.");
                    }
                }
            }

            System.out.print("\nAdd another route connection? (Y/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) break;
        }
    }

    private static void handleRemoveEdge() {
        while (true) {
            System.out.println("\n-------------- REMOVE ROUTE CONNECTION --------------");
            
            System.out.print("Enter 1st Station (ID or Name): ");
            String input1 = scanner.nextLine().trim();
            Integer id1 = parseStationInput(input1);

            if (id1 == null) {
                System.out.println("❌ Error: Station '" + input1 + "' does not exist.");
            } else {
                System.out.print("Enter 2nd Station (ID or Name): ");
                String input2 = scanner.nextLine().trim();
                Integer id2 = parseStationInput(input2);

                if (id2 == null) {
                    System.out.println("❌ Error: Station '" + input2 + "' does not exist.");
                } else {
                    boolean success = evNetwork.removeEdgeByIds(id1, id2);
                    if (success) {
                        System.out.println("✅ Success! Route between [" + id1 + "] and [" + id2 + "] has been removed.");
                    } else {
                        System.out.println("❌ Error: No direct route exists between these two stations.");
                    }
                }
            }

            System.out.print("\nRemove another route connection? (Y/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) break;
        }
    }

    private static void printQuickStationSummary() {
        Map<Integer, Station> stations = evNetwork.getAllStations();
        System.out.println(" Available Stations (" + stations.size() + " total):");
        int count = 0;
        for (Station s : stations.values()) {
            System.out.printf("  [%2d] %-30s", s.id, s.name);
            count++;
            if (count % 2 == 0) System.out.println();
        }
        if (count % 2 != 0) System.out.println();
        System.out.println("------------------------------------------------------");
    }

    private static void displayAllStations() {
        Map<Integer, Station> stations = evNetwork.getAllStations();
        System.out.println("\nRegistered Stations (" + stations.size() + ")");
        System.out.println("------------------------------------------------------------------");
        for (Map.Entry<Integer, Station> entry : stations.entrySet()) {
            Station s = entry.getValue();
            System.out.printf("[%2d] %-38s (Lat: %.4f, Lon: %.4f)%n", s.id, s.name, s.lat, s.lon);
        }
        System.out.println("------------------------------------------------------------------\n");
    }

    private static void displayAdjacencyList() {
        Map<Integer, Station> stations = evNetwork.getAllStations();
        System.out.println("\nNetwork Adjacency List");
        System.out.println("--------------------------------------------------");
        for (Map.Entry<Integer, Station> entry : stations.entrySet()) {
            int id = entry.getKey();
            Station s = entry.getValue();
            List<Integer> neighbors = evNetwork.getNeighbors(id);
            System.out.printf("[%2d] %-35s -> %s%n", s.id, s.name, neighbors.toString());
        }
        System.out.println("--------------------------------------------------\n");
    }

    private static void handleEmergencyRouting() {
        System.out.println("\nEmergency Route Calculation");
        System.out.println("--------------------------------------------------");

        printQuickStationSummary();
        System.out.print("Enter Start Station (ID or Name): ");
        String startInput = scanner.nextLine().trim();

        System.out.print("Enter Target Station (ID or Name): ");
        String targetInput = scanner.nextLine().trim();

        Integer startId = parseStationInput(startInput);
        Integer targetId = parseStationInput(targetInput);

        if (startId == null || targetId == null) {
            System.out.println("Error: Invalid station input.\n");
            return;
        }

        if (startId.equals(targetId)) {
            System.out.println("Start and Target stations are the same.\n");
            return;
        }

        Station startStation = evNetwork.getStation(startId);
        Station targetStation = evNetwork.getStation(targetId);

        BFSResult result = evNetwork.findEmergencyRoute(startId, targetId);

        if (result == null || result.totalHops == -1) {
            System.out.println("No path exists between " + startStation.name + " and " + targetStation.name + ".\n");
        } else {
            System.out.println("\nRoute Found");
            System.out.println("--------------------------------------------------");
            System.out.println("From: " + startStation.name + " [" + startId + "]");
            System.out.println("To  : " + targetStation.name + " [" + targetId + "]");
            System.out.println("Hops: " + result.totalHops);
            System.out.println("Path Sequence:");

            for (int i = 0; i < result.pathNodeIds.size(); i++) {
                int nodeId = result.pathNodeIds.get(i);
                Station st = evNetwork.getStation(nodeId);
                System.out.printf("  Step %2d: [%2d] %-35s (%.4f, %.4f)%n", i, st.id, st.name, st.lat, st.lon);
            }
            System.out.println("--------------------------------------------------\n");

            System.out.print("Render on Interactive GIS Map? (Y/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                displayGraphGUI(result.pathNodeIds);
            }
        }
    }

    private static Integer parseStationInput(String input) {
        try {
            int id = Integer.parseInt(input);
            if (evNetwork.getStation(id) != null) return id;
        } catch (NumberFormatException ignored) {
        }
        return evNetwork.findStationIdByName(input);
    }

    private static void initializeSELEVNetData() {
        evNetwork.addStation(1, "The Starling Mall", 3.1348, 101.6229);
        evNetwork.addStation(2, "XPark Sunway Serene", 3.0872, 101.6033);
        evNetwork.addStation(3, "Jaguar Land Rover, Ara Damansara", 3.1205, 101.5810);
        evNetwork.addStation(4, "Petronas NKVE", 3.1098, 101.5921);
        evNetwork.addStation(5, "BYD Ara Damansara (TNBX)", 3.1221, 101.5835);
        evNetwork.addStation(6, "MG Motor Petaling Jaya by DC Handal", 3.1112, 101.6310);
        evNetwork.addStation(7, "Perdana View Condo", 3.1678, 101.6111);
        evNetwork.addStation(8, "Lebuhraya Damansara-Puchong (LDP)", 3.1190, 101.6150);
        evNetwork.addStation(9, "Megah Rise Mall", 3.1182, 101.6135);
        evNetwork.addStation(10, "Hyundai Ara Damansara (TNBX)", 3.1235, 101.5828);
        evNetwork.addStation(11, "Jaya Shopping Centre", 3.1107, 101.6360);
        evNetwork.addStation(12, "Thomson Hospital", 3.1492, 101.5801);
        evNetwork.addStation(13, "Gentari @ Menara KLK", 3.1580, 101.6130);
        evNetwork.addStation(14, "Paradigm Mall", 3.1051, 101.5960);
        evNetwork.addStation(15, "The Summit USJ", 3.0592, 101.5930);
        evNetwork.addStation(16, "Damansara Mall (3 Damansara)", 3.1305, 101.6272);
        evNetwork.addStation(17, "Persiaran Bakti", 3.1388, 101.5885);
        evNetwork.addStation(18, "Kelana D’Putera Condominium", 3.1085, 101.5982);
        evNetwork.addStation(19, "Subang Parade", 3.0820, 101.5862);
        evNetwork.addStation(20, "Subang Jaya Medical Centre", 3.0782, 101.5875);
        evNetwork.addStation(21, "The Glades Plaza", 3.0210, 101.5812);
        evNetwork.addStation(22, "Nu Empire Shopping Gallery", 3.0811, 101.5830);
        evNetwork.addStation(23, "Main Place Mall", 3.0250, 101.5820);
        evNetwork.addStation(24, "E10", 2.9810, 101.6500);
        evNetwork.addStation(25, "UOA Business Park Glenmarie", 3.0875, 101.5851);
        evNetwork.addStation(26, "XPENG Glenmarie", 3.0890, 101.5732);
        evNetwork.addStation(27, "Concorde Hotel Shah Alam", 3.0760, 101.5200);
        evNetwork.addStation(28, "Smile Hotel Shah Alam", 3.0710, 101.5150);
        evNetwork.addStation(29, "KSL Automobile Enterprise", 3.0112, 101.4510);
        evNetwork.addStation(30, "Sime Darby Auto Selection Glenmarie", 3.0910, 101.5680);
        evNetwork.addStation(31, "Zaibar Automobile Industries", 3.0600, 101.5300);
        evNetwork.addStation(32, "Columbia Hospital Bukit Rimau", 3.0010, 101.5312);
        evNetwork.addStation(33, "Jalan Usahawan 2", 2.9210, 101.6410);
        evNetwork.addStation(34, "Petronas Bukit Jelutong ELITE", 3.1010, 101.5320);
        evNetwork.addStation(35, "IOI Mall Puchong", 3.0450, 101.6210);
        evNetwork.addStation(36, "IOI Rio City Puchong", 3.0410, 101.6250);
        evNetwork.addStation(37, "Quayside Mall Gamuda Kemuning", 2.9980, 101.5410);
        evNetwork.addStation(38, "Shell LDP D'Alpinia", 2.9812, 101.6610);
        evNetwork.addStation(39, "Petronas Dengkil R&R Northbound", 2.8810, 101.6810);
        evNetwork.addStation(40, "Petronas Temasya Federal Highway", 3.0840, 101.5790);

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

    static class MapPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
        private final List<Integer> highlightedPath;
        private double zoomFactor = 1.0;
        private double offsetX = 0;
        private double offsetY = 0;
        private Point dragStart;
        private Integer focusedNodeId = null;

        public MapPanel(List<Integer> highlightedPath) {
            this.highlightedPath = highlightedPath;
            setBackground(new Color(24, 28, 36));
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
        }

        public void focusOnStation(int id) {
            this.focusedNodeId = id;
            Map<Integer, Station> allStations = evNetwork.getAllStations();
            Station station = allStations.get(id);
            if (station == null) return;

            int width = getWidth();
            int height = getHeight();

            double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
            double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

            for (Station s : allStations.values()) {
                if (s.lat < minLat) minLat = s.lat;
                if (s.lat > maxLat) maxLat = s.lat;
                if (s.lon < minLon) minLon = s.lon;
                if (s.lon > maxLon) maxLon = s.lon;
            }

            double latMargin = Math.max(0.01, (maxLat - minLat) * 0.1);
            double lonMargin = Math.max(0.01, (maxLon - minLon) * 0.1);
            minLat -= latMargin; maxLat += latMargin;
            minLon -= lonMargin; maxLon += lonMargin;

            double px = (((station.lon - minLon) / (maxLon - minLon)) * (width - 380) + 80);
            double py = ((1.0 - (station.lat - minLat) / (maxLat - minLat)) * (height - 180) + 80);

            this.zoomFactor = 1.8;
            this.offsetX = (width / 2.0) - px;
            this.offsetY = (height / 2.0) - py;

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            Map<Integer, Station> allStations = evNetwork.getAllStations();
            if (allStations.isEmpty()) return;

            double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
            double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

            for (Station s : allStations.values()) {
                if (s.lat < minLat) minLat = s.lat;
                if (s.lat > maxLat) maxLat = s.lat;
                if (s.lon < minLon) minLon = s.lon;
                if (s.lon > maxLon) maxLon = s.lon;
            }

            double latMargin = Math.max(0.01, (maxLat - minLat) * 0.1);
            double lonMargin = Math.max(0.01, (maxLon - minLon) * 0.1);
            minLat -= latMargin; maxLat += latMargin;
            minLon -= lonMargin; maxLon += lonMargin;

            Map<Integer, Point> screenPositions = new HashMap<>();
            for (Station s : allStations.values()) {
                int px = (int) (((s.lon - minLon) / (maxLon - minLon)) * (width - 380) + 80);
                int py = (int) ((1.0 - (s.lat - minLat) / (maxLat - minLat)) * (height - 180) + 80);

                int finalX = (int) ((px - width / 2.0) * zoomFactor + width / 2.0 + offsetX);
                int finalY = (int) ((py - height / 2.0) * zoomFactor + height / 2.0 + offsetY);

                screenPositions.put(s.id, new Point(finalX, finalY));
            }

            Set<String> drawnEdges = new HashSet<>();
            g2d.setStroke(new BasicStroke((float) (1.8 * Math.sqrt(zoomFactor))));
            g2d.setColor(new Color(75, 85, 99));

            for (int id : allStations.keySet()) {
                Point p1 = screenPositions.get(id);
                for (int neighbor : evNetwork.getNeighbors(id)) {
                    String edgeKey = Math.min(id, neighbor) + "-" + Math.max(id, neighbor);
                    if (!drawnEdges.contains(edgeKey) && screenPositions.containsKey(neighbor)) {
                        drawnEdges.add(edgeKey);
                        Point p2 = screenPositions.get(neighbor);
                        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }

            if (highlightedPath != null && highlightedPath.size() > 1) {
                g2d.setStroke(new BasicStroke((float) (4.5 * Math.sqrt(zoomFactor))));
                g2d.setColor(new Color(239, 68, 68));

                for (int i = 0; i < highlightedPath.size() - 1; i++) {
                    Point p1 = screenPositions.get(highlightedPath.get(i));
                    Point p2 = screenPositions.get(highlightedPath.get(i + 1));
                    if (p1 != null && p2 != null) {
                        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }

            int baseRadius = (int) Math.max(10, 12 * Math.sqrt(zoomFactor));
            for (int id : allStations.keySet()) {
                Point p = screenPositions.get(id);

                if (highlightedPath != null && highlightedPath.contains(id)) {
                    if (id == highlightedPath.get(0)) {
                        g2d.setColor(new Color(34, 197, 94));
                    } else if (id == highlightedPath.get(highlightedPath.size() - 1)) {
                        g2d.setColor(new Color(249, 115, 22));
                    } else {
                        g2d.setColor(new Color(239, 68, 68));
                    }
                } else {
                    g2d.setColor(new Color(59, 130, 246));
                }

                g2d.fillOval(p.x - baseRadius, p.y - baseRadius, baseRadius * 2, baseRadius * 2);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(p.x - baseRadius, p.y - baseRadius, baseRadius * 2, baseRadius * 2);

                g2d.setFont(new Font("SansSerif", Font.BOLD, (int) Math.max(9, 11 * Math.sqrt(zoomFactor))));
                FontMetrics fm = g2d.getFontMetrics();
                String idStr = String.valueOf(id);
                g2d.drawString(idStr, p.x - (fm.stringWidth(idStr) / 2), p.y + (fm.getAscent() / 2) - 2);

                if (focusedNodeId != null && focusedNodeId == id) {
                    g2d.setColor(Color.YELLOW);
                    g2d.setStroke(new BasicStroke(3.0f));
                    int auraRadius = baseRadius + 10;
                    g2d.drawOval(p.x - auraRadius, p.y - auraRadius, auraRadius * 2, auraRadius * 2);

                    Station focusSt = allStations.get(id);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
                    g2d.setColor(Color.YELLOW);
                    g2d.drawString("📍 " + focusSt.name, p.x + baseRadius + 12, p.y + 4);
                }
            }

            g2d.setColor(new Color(15, 23, 42, 220));
            g2d.fillRoundRect(15, 15, 280, 80, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString("SELEV-Net Geographic EV Map", 25, 35);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2d.drawString("• Mouse Scroll: Zoom In / Out", 25, 53);
            g2d.drawString("• Click List Item: Center & Locate Node", 25, 70);
        }

        @Override public void mouseWheelMoved(MouseWheelEvent e) {
            if (e.getWheelRotation() < 0) {
                zoomFactor *= 1.15;
            } else {
                zoomFactor /= 1.15;
                if (zoomFactor < 0.3) zoomFactor = 0.3;
            }
            repaint();
        }

        @Override public void mousePressed(MouseEvent e) { dragStart = e.getPoint(); }
        @Override public void mouseDragged(MouseEvent e) {
            if (dragStart != null) {
                offsetX += e.getX() - dragStart.x;
                offsetY += e.getY() - dragStart.y;
                dragStart = e.getPoint();
                repaint();
            }
        }
        @Override public void mouseReleased(MouseEvent e) { dragStart = null; }
        @Override public void mouseClicked(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
        @Override public void mouseMoved(MouseEvent e) {}
    }

    private static void displayGraphGUI(List<Integer> highlightedPath) {
        JFrame frame = new JFrame("SELEV-Net GIS Map");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1250, 820);
        frame.setLocationRelativeTo(null);

        JLayeredPane layeredPane = new JLayeredPane();

        MapPanel mapPanel = new MapPanel(highlightedPath);
        mapPanel.setBounds(0, 0, 1250, 820);
        layeredPane.add(mapPanel, JLayeredPane.DEFAULT_LAYER);

        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(new Color(15, 23, 42));

        JLabel titleLabel = new JLabel(" STATION INDEX (Click to Locate)");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendPanel.add(titleLabel);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        Map<Integer, Station> stations = evNetwork.getAllStations();
        List<Integer> sortedIds = new ArrayList<>(stations.keySet());
        Collections.sort(sortedIds);

        for (int id : sortedIds) {
            Station st = stations.get(id);
            JLabel item = new JLabel(String.format(" [%2d]  %s", st.id, st.name));
            item.setFont(new Font("Monospaced", Font.PLAIN, 11));
            item.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (highlightedPath != null && highlightedPath.contains(id)) {
                if (id == highlightedPath.get(0)) {
                    item.setForeground(new Color(34, 197, 94));
                } else if (id == highlightedPath.get(highlightedPath.size() - 1)) {
                    item.setForeground(new Color(249, 115, 22));
                } else {
                    item.setForeground(new Color(239, 68, 68));
                }
            } else {
                item.setForeground(new Color(229, 231, 235));
            }

            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mapPanel.focusOnStation(st.id);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    item.setFont(new Font("Monospaced", Font.BOLD, 11));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    item.setFont(new Font("Monospaced", Font.PLAIN, 11));
                }
            });

            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            legendPanel.add(item);
        }

        JScrollPane scrollPane = new JScrollPane(legendPanel);
        scrollPane.setBounds(910, 15, 300, 380);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(75, 85, 99), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        layeredPane.add(scrollPane, JLayeredPane.PALETTE_LAYER);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = frame.getWidth();
                int h = frame.getHeight();
                mapPanel.setBounds(0, 0, w, h);
                scrollPane.setBounds(w - 330, 15, 300, Math.min(420, h - 80));
            }
        });

        frame.add(layeredPane);
        frame.setVisible(true);
    }
}