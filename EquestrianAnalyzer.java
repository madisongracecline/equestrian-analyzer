import java.util.*;
import java.io.*;
import java.nio.file.*;

public class EquestrianAnalyzer {

    static Scanner scanner = new Scanner(System.in);
    static List<String[]> rounds = new ArrayList<>();

    public static void main(String[] args) {
        loadRounds();
        printBanner();

        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Log a new round");
            System.out.println("2. View my round history");
            System.out.println("3. Exit");
            System.out.print("Choose (1/2/3): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) logRound();
            else if (choice.equals("2")) viewHistory();
            else if (choice.equals("3")) { System.out.println("Good luck in your next round!"); break; }
            else System.out.println("Please type 1, 2 or 3.");
        }
    }

    static void logRound() {
        System.out.println("\n--- LOG A ROUND ---");

        //New entry information collection
        System.out.print("Your name: ");
        String rider = scanner.nextLine().trim();

        System.out.print("Horse name: ");
        String horse = scanner.nextLine().trim();

        System.out.print("Competition name (or press Enter to skip): ");
        String comp = scanner.nextLine().trim();

        System.out.print("Class (e.g. 1.30m): ");
        String className = scanner.nextLine().trim();

        System.out.print("How many fences on the course? ");
        int totalFences = readInt(12);

        // Enviornment collection
        System.out.println("\n-- Environment --");
        System.out.print("Indoor or outdoor? (i/o): ");
        String arena = scanner.nextLine().trim().toLowerCase().equals("i") ? "indoor" : "outdoor";

        System.out.print("Ground condition (firm/good/soft/heavy): ");
        String ground = scanner.nextLine().trim();
        if (ground.isEmpty()) ground = "good";

        boolean windy = askYesNo("Was it windy?");

        System.out.println("\n-- Warm Up --");
        boolean goodWarmup = askYesNo("Was your warm up good?");
        String warmupNotes = "";
        if (!goodWarmup) {
            System.out.print("What went wrong in warm up? ");
            warmupNotes = scanner.nextLine().trim();
        }

        // Data collection about rider feelings 
        System.out.println("\nHow were you feeling?");
        System.out.println("  1. Confident  2. Nervous  3. Tired  4. Focused  5. Normal");
        System.out.print("Choose (1-5): ");
        String[] feelings = {"", "Confident", "Nervous", "Tired", "Focused", "Normal"};
        int feelingIdx = readInt(5);
        if (feelingIdx < 1 || feelingIdx > 5) feelingIdx = 5;
        String feeling = feelings[feelingIdx];

        // Data collection about horses qualities
        System.out.println("\n-- About " + horse + " --");
        boolean rushes = askYesNo("Does " + horse + " tend to rush?");
        boolean backsOff = askYesNo("Does " + horse + " tend to back off fences?");
        boolean spooky = askYesNo("Is " + horse + " spooky?");
        boolean lazyBehind = askYesNo("Is " + horse + " lazy with hind legs?");

        // Round information collection
        System.out.println("\n-- Faults --");
        List<String[]> incidents = new ArrayList<>();

        if (askYesNo("Did you have any rails down?")) {
            collectRails(incidents, totalFences);
        }

        boolean timeFaults = askYesNo("Did you get time faults?");
        double timeSecs = 0;
        if (timeFaults) {
            System.out.print("How many seconds over? ");
            timeSecs = readDouble(1.0);
        }

        System.out.print("\nAny general notes? (Enter to skip): ");
        String notes = scanner.nextLine().trim();

        String[] round = {
            rider, horse, comp, className,
            String.valueOf(totalFences), arena, ground,
            String.valueOf(windy), String.valueOf(goodWarmup), warmupNotes,
            feeling, String.valueOf(rushes), String.valueOf(backsOff),
            String.valueOf(spooky), String.valueOf(lazyBehind),
            String.valueOf(timeFaults), String.valueOf(timeSecs), notes
        };
        rounds.add(round);
        saveRounds();

        analyzeRound(round, incidents, rushes, backsOff, spooky, lazyBehind, timeFaults, timeSecs, windy, goodWarmup, feeling);
    }

    static void collectRails(List<String[]> incidents, int totalFences) {
        boolean more = true;
        while (more) {
            System.out.println("\n  -- Rail details --");
            System.out.print("  Fence number: ");
            int fenceNum = readInt(1);

            int position = (int) Math.round(((double) fenceNum / totalFences) * 100);

            System.out.println("  Fence type:");
            System.out.println("    1. Vertical   2. Oxer   3. Combination Vertical");
            System.out.println("    4. Combination Oxer   5. Wall   6. Liverpool");
            System.out.print("  Choose (1-6): ");
            String[] fenceTypes = {"", "Vertical", "Oxer", "Combination Vertical", "Combination Oxer", "Wall", "Liverpool"};
            int ftIdx = readInt(1);
            if (ftIdx < 1 || ftIdx > 6) ftIdx = 1;
            String fenceType = fenceTypes[ftIdx];

            boolean isOxer = fenceType.toLowerCase().contains("oxer");
            boolean isCombo = fenceType.toLowerCase().contains("combination");

            String hindOrFront = "unknown";
            if (isOxer) {
                System.out.println("  Was it the front or hind rail?");
                System.out.println("    1. Front   2. Hind   3. Not sure");
                System.out.print("  Choose (1-3): ");
                int hf = readInt(3);
                hindOrFront = hf == 1 ? "front" : hf == 2 ? "hind" : "unknown";
            }

            boolean inCombo = false;
            int comboElement = 0;
            boolean correctStrides = true;
            int stridesRidden = 0;

            if (isCombo) {
                inCombo = true;
                System.out.print("  Which element? (1=A, 2=B, 3=C): ");
                comboElement = readInt(1);
                System.out.print("  How many strides did you ride between elements? ");
                stridesRidden = readInt(1);
                correctStrides = askYesNo("  Was that the correct number of strides?");
            }

            System.out.println("  How was your pace on approach?");
            System.out.println("    1. Too fast   2. Too slow   3. Correct");
            System.out.println("    4. Rushing    5. Backing off   6. Not sure");
            System.out.print("  Choose (1-6): ");
            String[] paces = {"", "Too fast", "Too slow", "Correct", "Rushing", "Backing off", "Not sure"};
            int pIdx = readInt(6);
            if (pIdx < 1 || pIdx > 6) pIdx = 6;
            String pace = paces[pIdx];

            boolean relatedDist = askYesNo("  Was this on a related distance from another fence?");

            incidents.add(new String[]{
                String.valueOf(fenceNum), fenceType, String.valueOf(position),
                pace, hindOrFront, String.valueOf(inCombo),
                String.valueOf(comboElement), String.valueOf(stridesRidden),
                String.valueOf(correctStrides), String.valueOf(relatedDist)
            });

            more = askYesNo("  Add another rail?");
        }
    }

    static void analyzeRound(String[] round, List<String[]> incidents,
            boolean rushes, boolean backsOff, boolean spooky, boolean lazyBehind,
            boolean timeFaults, double timeSecs, boolean windy,
            boolean goodWarmup, String feeling) {

        System.out.println("\n==========================================");
        System.out.println("         ROUND ANALYSIS REPORT");
        System.out.println("==========================================");
        System.out.println("Rider : " + round[0]);
        System.out.println("Horse : " + round[1]);
        if (!round[2].isEmpty()) System.out.println("Event : " + round[2] + " - " + round[3]);
        System.out.println("Arena : " + round[5] + " | Ground: " + round[6] + (windy ? " | WINDY" : ""));
        System.out.println("Rider feeling: " + feeling);
        System.out.println("Warm up: " + (goodWarmup ? "Good" : "Not great"));

        if (incidents.isEmpty() && !timeFaults) {
            System.out.println("\nNo faults recorded - great round!");
            return;
        }

        if (!incidents.isEmpty()) {
            System.out.println("\n-- FAULTS --");
            for (String[] i : incidents) {
                System.out.println("  Rail at fence " + i[0] + " (" + i[1] + ") - " + i[2] + "% through course");
                if (i[5].equals("true")) {
                    System.out.println("    Combination element " + elementLetter(Integer.parseInt(i[6])) +
                        " - " + i[7] + " strides ridden (" + (i[8].equals("true") ? "correct" : "INCORRECT") + ")");
                }
            }
        }
        if (timeFaults) System.out.println("  Time faults: " + timeSecs + " seconds over");

        System.out.println("\n-- PROBABLE CAUSES --");
        System.out.println("(% = how likely this caused the problem)");
        System.out.println("------------------------------------------");

        for (String[] i : incidents) {
            int fenceNum = Integer.parseInt(i[0]);
            String fenceType = i[1];
            int position = Integer.parseInt(i[2]);
            String pace = i[3];
            String hindFront = i[4];
            boolean inCombo = i[5].equals("true");
            boolean correctStrides = i[8].equals("true");
            boolean relatedDist = i[9].equals("true");
            boolean isOxer = fenceType.toLowerCase().contains("oxer");
            boolean lateInCourse = position > 65;
            boolean earlyInCourse = position < 30;

            System.out.println("\nFENCE " + fenceNum + " (" + fenceType + "):");

            if (pace.equals("Too fast") || pace.equals("Rushing")) {
                int prob = 70;
                if (isOxer) prob += 5;
                if (earlyInCourse) prob += 5;
                if (rushes) prob += 10;
                prob = Math.min(97, prob);
                String who = rushes ? "SHARED" : "RIDER";
                System.out.println("  " + prob + "% - Pace too fast on approach [" + who + "]");
                System.out.println("     -> Work on half-halts 5-6 strides out. Re-establish rhythm.");
            }

            if (pace.equals("Too slow") || pace.equals("Backing off")) {
                int prob = 65;
                if (isOxer) prob += 10;
                if (backsOff) prob += 10;
                if (lateInCourse) prob += 5;
                prob = Math.min(97, prob);
                String who = backsOff ? "HORSE" : "SHARED";
                System.out.println("  " + prob + "% - Insufficient pace / backing off [" + who + "]");
                System.out.println("     -> Ride positively with leg on. Keep engine going into the fence.");
            }

            if (isOxer && hindFront.equals("hind")) {
                int prob = 60;
                if (lazyBehind) prob += 15;
                if (lateInCourse) prob += 10;
                prob = Math.min(97, prob);
                String who = lazyBehind ? "HORSE" : "SHARED";
                System.out.println("  " + prob + "% - Horse lazy with hind legs over oxer [" + who + "]");
                System.out.println("     -> Grid work with bounce fences. Sharpen hind leg response.");
            }

            if (inCombo && !correctStrides) {
                System.out.println("  75% - Wrong strides between combination elements [RIDER]");
                System.out.println("     -> Walk the combination and count strides carefully.");
            }

            if (relatedDist) {
                System.out.println("  65% - Stride adjustment error on related distance [RIDER]");
                System.out.println("     -> Walk related distances and know if you need to add or leave out.");
            }

            if (lateInCourse) {
                System.out.println("  60% - Late course fatigue affecting accuracy [SHARED]");
                System.out.println("     -> Build fitness with longer canter sets for horse and rider.");
            }

            if (!goodWarmup && earlyInCourse) {
                System.out.println("  50% - Poor warm up carrying into early course [SHARED]");
                System.out.println("     -> Prioritise quality over quantity in warm up.");
            }

            if (spooky && windy) {
                System.out.println("  50% - Horse distracted by wind on approach [HORSE]");
                System.out.println("     -> Keep leg firmly on in windy conditions. Ride positively.");
            }

            if (feeling.equals("Nervous") && pace.equals("Correct")) {
                System.out.println("  45% - Rider tension affecting communication [RIDER]");
                System.out.println("     -> Focus on breathing and riding the rhythm, not the fence.");
            }
        }

        if (timeFaults) {
            System.out.println("\nTIME FAULTS (" + timeSecs + " seconds over):");
            if (timeSecs <= 2.0) {
                System.out.println("  65% - Slightly wide turns or conservative track [RIDER]");
                System.out.println("     -> Walk course looking for tighter lines.");
            } else {
                System.out.println("  75% - Overall pace too slow or too many steadying moments [RIDER]");
                System.out.println("     -> Calculate target speed for the class. Use a watch in training.");
            }
            if (backsOff) {
                System.out.println("  60% - Horse backing off reducing average speed [HORSE]");
                System.out.println("     -> Maintain rhythm between fences.");
            }
            if (feeling.equals("Nervous")) {
                System.out.println("  55% - Nerves causing subconsciously slower pace [RIDER]");
                System.out.println("     -> Count strides between fences to anchor your rhythm.");
            }
        }

        System.out.println("\n==========================================");
    }

    static void viewHistory() {
        if (rounds.isEmpty()) { System.out.println("\nNo rounds logged yet."); return; }
        System.out.println("\n-- ROUND HISTORY --");
        for (int i = 0; i < rounds.size(); i++) {
            String[] r = rounds.get(i);
            System.out.println((i+1) + ". " + r[0] + " on " + r[1] + " | " + r[3] + " | Time faults: " + r[15]);
        }
    }

    static void saveRounds() {
        try {
            new File("data").mkdir();
            PrintWriter w = new PrintWriter("data/rounds.txt");
            for (String[] r : rounds) w.println(String.join("|", r));
            w.close();
        } catch (Exception e) { System.out.println("Could not save: " + e.getMessage()); }
    }

    static void loadRounds() {
        try {
            File f = new File("data/rounds.txt");
            if (!f.exists()) return;
            for (String line : Files.readAllLines(f.toPath()))
                if (!line.trim().isEmpty()) rounds.add(line.split("\\|", -1));
        } catch (Exception e) {}
    }

    static void printBanner() {
        System.out.println("==========================================");
        System.out.println("      EQUESTRIAN ROUND ANALYZER");
        System.out.println("   Showjumping Fault Analysis Tool");
        System.out.println("==========================================");
    }

    static boolean askYesNo(String question) {
        while (true) {
            System.out.print(question + " (y/n): ");
            String a = scanner.nextLine().trim().toLowerCase();
            if (a.equals("y") || a.equals("yes")) return true;
            if (a.equals("n") || a.equals("no")) return false;
            System.out.println("Please type y or n.");
        }
    }

    static int readInt(int defaultVal) {
        try { return Integer.parseInt(scanner.nextLine().trim()); }
        catch (Exception e) { return defaultVal; }
    }

    static double readDouble(double defaultVal) {
        try { return Double.parseDouble(scanner.nextLine().trim()); }
        catch (Exception e) { return defaultVal; }
    }

    static String elementLetter(int el) {
        if (el == 1) return "A";
        if (el == 2) return "B";
        if (el == 3) return "C";
        return String.valueOf(el);
    }
}