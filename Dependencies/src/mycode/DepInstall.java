package mycode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class DepInstall {

    public final Integer MAXCOMS = 1000;
    public ArrayList<String> Installed;
    public ArrayList<String> ImplicitInstalled;
    public HashMap<String, ArrayList<String>> dependenciesMap;
    public boolean clashFree = true;
    public String curCycle = "";
    public int cycleCount = 0;

    public DepInstall() {
        Installed = new ArrayList<String>();
        ImplicitInstalled = new ArrayList<String>();
        dependenciesMap = new HashMap<String, ArrayList<String>>();
    }

    public void runNCommands(Vector<String> commands, Integer N) {
        // PRE: commands contains set of commands read in by
        // readCommandsFromFile()
        // POST: executed min(N, all) commands
        int count = 0;
        Iterator<String> i = commands.iterator();
        while (i.hasNext() && count < N) {
            count++;
            String curr = i.next();
            String arr[] = curr.split(" ", 2);
            System.out.println(curr);
            if (arr[0].equalsIgnoreCase("DEPEND")) {
                runDepend(arr[1]);
            }
            if (arr[0].equalsIgnoreCase("INSTALL")) {
                runInstall(arr[1]);
                ImplicitInstalled.add(arr[1]);
            }
            if (arr[0].equalsIgnoreCase("LIST")) {
                list();
            }
            if (arr[0].equalsIgnoreCase("REMOVE")) {
                runRemove(arr[1]);
            }
            if (arr[0].equalsIgnoreCase("END")) {
                break;
            }
        }

    }

    public void runRemove(String prog) {
        ArrayList<String> depnt = new ArrayList<String>();

        if (Installed.contains(prog)) {
            int count = 0;
            for (Entry<String, ArrayList<String>> entry : dependenciesMap.entrySet()) {

                if (entry.getKey().equals(prog)) {
                    depnt = entry.getValue();
                } else {
                    if (entry.getValue().contains(prog)) {
                        count++;
                        // If in map more than once then it still needed
                    }
                }
            }

            if (count > 1) {
                System.out.println("   " + prog + " is still needed");
            } else {
                System.out.println("   Removing " + prog);
                Installed.remove(prog);
                if (!depnt.isEmpty()) {
                    for (int i = depnt.size() - 1; i >= 0; i--) {
                        removeExtra(depnt.get(i).toString());
                    }
                }
            }
        } else {
            System.out.println("   " + prog + " is not installed");
        }
    }

    public void removeExtra(String prog) {
        int count = 0;
        Iterator<String> newt = Installed.iterator();
        while (newt.hasNext()) {
            String curr = newt.next();
            if (dependenciesMap.containsKey(curr)) {
                ArrayList<String> groundhog = dependenciesMap.get(curr);
                if (groundhog.contains(prog)) {
                    count++;
                }
            }
        }
        if (count == 0) { // If nothing else needs it remove
            if (!ImplicitInstalled.contains(prog)) {
                System.out.println("   Removing " + prog);
                Installed.remove(prog);
                if (dependenciesMap.containsKey(prog)) {
                    ArrayList<String> newOne = new ArrayList<String>();
                    newOne = dependenciesMap.get(prog);
                    Iterator<String> loop = newOne.iterator();
                    while (loop.hasNext()) {
                        removeExtra(loop.next());
                    }

                }
            }

        }
    }

    public void list() {
        Iterator<String> i = Installed.iterator();
        while (i.hasNext()) {
            System.out.println("   " + i.next());
        }
    }

    public void runInstall(String prog) {
        if (Installed.contains(prog)) {
            System.out.println("   " + prog + " is already installed");
        } else {
            if (dependenciesMap.containsKey(prog)) {
                ArrayList<String> depends = dependenciesMap.get(prog);
                for (int j = 0; j <= depends.size() - 1; j++) {
                    // Run backwards through the dependencies
                    if (!Installed.contains(depends.get(j))) {
                        runInstall(depends.get(j));
                        // Recall install with the new program to install
                    }
                }
            }
            System.out.println("   Installing " + prog);

            Installed.add(prog);
        }

    }

    public void runDepend(String command) {
        ArrayList<String> depList = new ArrayList<String>();
        for (String word : command.split(" ")) {
            depList.add(word);
        }
        ArrayList<String> depList2 = new ArrayList<String>();
        for (int i = 1; i < depList.size(); i++) {
            depList2.add(depList.get(i).toString());
        }
        dependenciesMap.put(depList.get(0).toString(), depList2);

    }

    public void runNCommandswCheck(Vector<String> commands, Integer N) {
        // PRE: commands contains set of commands read in by
        // readCommandsFromFile()
        // POST: executed min(N, all) commands, checking for cycles
        int count = 0;
        Iterator<String> i = commands.iterator();
        while (i.hasNext() && count < N) {
            String curr = i.next();
            String arr[] = curr.split(" ", 2);
            System.out.println(curr);
            if (clashFree) {
                if (arr[0].equals("DEPEND")) {
                    dependenciesCycleFree(arr[1]);
                }
                if (arr[0].equals("INSTALL")) {
                    runInstall(arr[1]);
                }
                if (arr[0].equals("LIST")) {
                    list();
                }
                if (arr[0].equalsIgnoreCase("REMOVE")) {
                    runRemove(arr[1]);
                }
                if (arr[0].equals("END")) {
                    break;
                }
            } else {
                if (arr[0].equals("DEPEND")) {
                    System.out.println("   Found cycle in dependencies");
                }
            }
            count++;
        }
    }

    public void dependenciesCycleFree(String string) {
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(string.split(" ")));
        String head = temp.get(0);
        temp.remove(0);
        for (int i = 0; i < temp.size(); i++) {
            checkForCycle(head, temp.get(i));
        }
        dependenciesMap.put(head, temp);
    }

    public void checkForCycle(String head, String string) {
        if (dependenciesMap.containsKey(string)) {
            ArrayList<String> depends = dependenciesMap.get(string);
            for (int j = 0; j < depends.size(); j++) {
                if (depends.get(j).equals(head)) {
                    System.out.println("   Found cycle in dependencies");
                    clashFree = false;
                }
                checkForCycle(head, depends.get(j));
            }
        }
    }

    public void runNCommandswCheckRecLarge(Vector<String> commands, Integer N) {
        // PRE: commands contains set of commands read in by
        // readCommandsFromFile()
        // POST: executed min(N, all) commands, checking for cycles and
        // recommending fix by removing largest cycle
        int count = 0;
        Iterator<String> i = commands.iterator();
        while (i.hasNext() && count < N) {
            String curr = i.next();
            String arr[] = curr.split(" ", 2);
            System.out.println(curr);
            if (clashFree) {
                if (arr[0].equals("DEPEND")) {
                    dependenciesCycleFreeLarge(arr[1], curr);
                }

                if (arr[0].equals("INSTALL")) {
                    runInstall(arr[1]);
                }
                if (arr[0].equals("LIST")) {
                    list();
                }
                if (arr[0].equalsIgnoreCase("REMOVE")) {
                    runRemove(arr[1]);
                }
                if (arr[0].equals("END")) {
                    break;
                }
            } else {
                if (arr[0].equals("DEPEND")) {
                    System.out.println("   Found cycle in dependencies");
                    int currentCount = countCycle(arr[1], curr);
                    if (currentCount > 0) {
                        if (currentCount > cycleCount) {
                            cycleCount = currentCount;
                            curCycle = curr;
                        }
                    } else if (currentCount == 0) {
                        dependenciesCycleFreeSmall(arr[1], curr);
                    }
                    System.out.println("   Suggest removing " + curCycle);
                }
            }
            count++;
        }
    }

    public void dependenciesCycleFreeLarge(String string, String curr) {
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(string.split(" ")));
        String head = temp.get(0);
        temp.remove(0);
        for (int i = 0; i < temp.size(); i++) {
            checkForCycleLarge(head, temp.get(i), curr);
        }
        if (curCycle != curr) {
            dependenciesMap.put(head, temp);
        }

    }

    public int countCycle(String string, String curr) {
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(string.split(" ")));

        String head = temp.get(0);
        int counter = 0;
        temp.remove(0);
        for (int i = 0; i < temp.size(); i++) {
            if (dependenciesMap.containsKey(temp.get(i))) {
                counter++;
                ArrayList<String> current = new ArrayList<String>();
                current = dependenciesMap.get(temp.get(i));
                if (current.contains(head)) {
                    return 1;
                }
                int temp1 = counting(current, head);
                if (temp1 > 0) {
                    counter += temp1;
                } else {
                    counter = 0;
                }
            }
        }
        return counter;
    }

    public int counting(ArrayList<String> current, String compare) {
        for (int i = 0; i < current.size(); i++) {
            if (dependenciesMap.containsKey(current.get(i))) {
                ArrayList<String> current1 = new ArrayList<String>();
                current1 = dependenciesMap.get(current.get(i));
                if (current1.contains(compare)) {
                    return 1;
                } else {
                    return counting(current1, compare);
                }
            }
        }
        return 0;
    }

    public void countCycleCheck(String head, String string, String curr) {
        if (dependenciesMap.containsKey(string)) {
            ArrayList<String> depends = dependenciesMap.get(string);
            for (int j = 0; j < depends.size(); j++) {
                if (depends.get(j).equals(head)) {
                    curCycle = curr;
                    clashFree = false;

                }
                countCycleCheck(head, depends.get(j), curr);
            }
        }
    }

    public void checkForCycleLarge(String head, String string, String curr) {
        if (dependenciesMap.containsKey(string)) {
            ArrayList<String> depends = dependenciesMap.get(string);
            for (int j = 0; j < depends.size(); j++) {
                if (depends.get(j).equals(head)) {
                    System.out.println("   Found cycle in dependencies");
                    clashFree = false;
                    int temp = countCycle(head + " " + string, curr);
                    if (temp > 0) {
                        if (temp > cycleCount) {
                            cycleCount = temp;
                            curCycle = curr;
                        }
                    }
                    System.out.println("   Suggest removing " + curCycle);

                }
                checkForCycleLarge(head, depends.get(j), curr);
            }
        }

    }

    public void dependenciesCycleFreeSmall(String string, String curr) {
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(string.split(" ")));
        String head = temp.get(0);
        temp.remove(0);
        for (int i = 0; i < temp.size(); i++) {
            checkForCycleSmall(head, temp.get(i), curr);
        }
        if (curCycle != curr) {
            dependenciesMap.put(head, temp);
        }

    }

    public void checkForCycleSmall(String head, String string, String curr) {
        if (dependenciesMap.containsKey(string)) {
            ArrayList<String> depends = dependenciesMap.get(string);
            for (int j = 0; j < depends.size(); j++) {
                if (depends.get(j).equals(head)) {
                    System.out.println("   Found cycle in dependencies");
                    clashFree = false;
                    int temp = countCycle(head + " " + string, curr);
                    if (temp > 0) {
                        if (temp <= cycleCount) {
                            cycleCount = temp;
                            curCycle = curr;
                        }
                    }
                    if (cycleCount == 0) {
                        cycleCount = temp;
                        curCycle = curr;
                    }
                    System.out.println("   Suggest removing " + curCycle);
                }
                checkForCycleSmall(head, depends.get(j), curr);
            }
        }

    }

    public void runNCommandswCheckRecSmall(Vector<String> commands, Integer N) {
        // PRE: commands contains set of commands read in by
        // readCommandsFromFile()
        // POST: executed min(N, all) commands, checking for cycles and
        // recommending fix by removing smallest cycle
        int count = 0;
        Iterator<String> i = commands.iterator();
        while (i.hasNext() && count < N) {
            String curr = i.next();
            String arr[] = curr.split(" ", 2);
            System.out.println(curr);
            if (clashFree) {
                if (arr[0].equals("DEPEND")) {
                    dependenciesCycleFreeLarge(arr[1], curr);
                }
                if (arr[0].equals("INSTALL")) {
                    runInstall(arr[1]);
                }
                if (arr[0].equals("LIST")) {
                    list();
                }
                if (arr[0].equalsIgnoreCase("REMOVE")) {
                    runRemove(arr[1]);
                }
                if (arr[0].equals("END")) {
                    break;
                }
            } else {
                if (arr[0].equals("DEPEND")) {
                    System.out.println("   Found cycle in dependencies");
                    int currentCount = countCycle(arr[1], curr);
                    if (currentCount > 0) {
                        if (currentCount <= cycleCount) {
                            cycleCount = currentCount;
                            curCycle = curr;
                        }
                    } else if (currentCount == 0) {
                        dependenciesCycleFreeLarge(arr[1], curr);
                    }
                    System.out.println("   Suggest removing " + curCycle);
                }
            }
            count++;
        }
    }

    public Vector<String> readCommandsFromFile(String fInName) throws IOException {
        // PRE: -
        // POST: returns lines from input file as vector of string
        BufferedReader fIn = new BufferedReader(new FileReader(fInName));
        String s;
        Vector<String> comList = new Vector<String>();

        while ((s = fIn.readLine()) != null) {
            comList.add(s);
        }
        fIn.close();

        return comList;
    }

    public String readSoln(String fInName, Integer N) throws IOException {
        // PRE: -
        // POST: returns N lines from input file as single string
        BufferedReader fIn = new BufferedReader(new FileReader(fInName));
        String s;
        String out = "";
        Integer i = 0;

        while (((s = fIn.readLine()) != null) && (i <= N)) {
            if ((i != N) || s.startsWith("   "))
                // responses to commands start with three spaces
                out += s + System.lineSeparator();
            if (!s.startsWith("   "))
                i += 1;
        }
        fIn.close();

        return out;
    }

    public static void main(String[] args) {

        DepInstall d = new DepInstall();
        Vector<String> inCommands = null;
        String PATH = "C:/sample-data1/";
        // change to your own path

        Integer N = d.MAXCOMS;
        // Integer N = 8;

        try {
            inCommands = d.readCommandsFromFile(PATH + "sample_P1.in");
        } catch (IOException e) {
            System.out.println("in exception: " + e);
        }

        d.runNCommands(inCommands, N);
        System.out.println();

    }
}
