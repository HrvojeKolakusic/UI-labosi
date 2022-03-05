package lab1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class HOC {

	private static Set<String> goals;
	private static Map<String, String> next;
	private static List<Node> open;
	private static Map<String, Double> h;
	private static List<Node> closed;
	private static List<Node> join;
	private static String cOutput;
	private static List<String> statePairs;
	private static String[] starts = {"Baderna", "Barban", "Buje", "Grožnjan", "Kanfanar", "Labin", "Lupoglav",
			"Medulin", "Motovun", "Opatija", "Pazin", "Poreč", "Pula", "Rovinj", "Umag", "Višnjan", "Vodnjan", "Žminj", "Buzet"};
	
	private static ArrayList<Node> expand(Node node) {
		ArrayList<Node> nextList = new ArrayList<>();
		String prijelazi = next.get(node.stanje);
		String[] split = prijelazi.split(" ");
		for (String x : split) {
			nextList.add(new Node(x.split(",")[0], node.cost + Double.parseDouble(x.split(",")[1]), 
					node.cost + Double.parseDouble(x.split(",")[1]) + h.get(x.split(",")[0]), node.dubina + 1, node));
		}
		
		return nextList;
	}
	
	public static void main(String[] args) throws IOException {
		
		Path stanjaPath = Paths.get("D:\\Faks\\UI\\lab1\\istra.txt");
		Path heuristicPath = Paths.get("D:\\Faks\\UI\\lab1\\istra_p_heuristic.txt");
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new BufferedInputStream(
								new FileInputStream(stanjaPath.toString()))));
		
		
		next = new HashMap<>();
		goals = new TreeSet<>();
		closed = new ArrayList<>();
		join = new ArrayList<>();
		statePairs = new ArrayList<>();
		String line;
		String[] split;
		int k = 0;
		
		//start i cilj
		while (true) {
			line = br.readLine();
			if (line.startsWith("#")) continue;
			if (k==0) {
				k++;
				continue;
			}
			if (k==1) {
				split = line.split(" ");
				for (String x : split) {
					goals.add(x);
				}
				break;
			}
		}
		
		//prijelazi
		while ((line = br.readLine()) != null) {
			if (line.startsWith("#")) continue;
			split = line.split(":");
			next.put(split[0], split[1].trim());
		}
		
		br.close();
		
		//heuristika
		br = new BufferedReader(
				new InputStreamReader(
						new BufferedInputStream(
								new FileInputStream(heuristicPath.toString()))));
		h = new HashMap<>();
		
		while ((line = br.readLine()) != null) {
			h.put(line.split(":")[0], Double.parseDouble(line.split(":")[1].trim()));
		}
		
		
		open = new ArrayList<>();
		boolean oOk = true;
		boolean cOk = true;
		System.out.println("Optimistic?");
		cOutput = "Consistent?\n";
		
		for (String z : starts) {
			
			open.clear();
			closed.clear();
			join.clear();
			open.add(new Node(z, (double)0, (double)0, 0, null));
			Node node = new Node("empty", (double)0, (double)0, 0, null);
			double cCost = 0;
			
			while (!open.isEmpty()) {
				node = open.get(0);
				
				//provjera consistent
				if (node.parent != null) {
					
					split = next.get(node.parent.stanje).split(" ");
					for (String t : split) {
						if (node.stanje.equals(t.split(",")[0])) cCost = Double.parseDouble(t.split(",")[1]);
					}
					
					if (!(h.get(node.parent.stanje) <= h.get(node.stanje) + cCost)) {
						if (!statePairs.contains(node.parent.stanje + node.stanje)) {
							statePairs.add(node.parent.stanje + node.stanje);
							cOutput = cOutput + "[ERR] h(" + node.parent.stanje + 
									") > h(" + node.stanje + ") + c: " + 
									h.get(node.parent.stanje) + " > " +
									h.get(node.stanje) + " + " + cCost + "\n";
							cOk = false;
						}
					}						
				}
				
				
				open.remove(0);
				if (goals.contains(node.stanje)) {
					break;
				}
				closed.add(node);
				boolean insert;
				
				for (Node x : expand(node)) {
					insert = true;
					join.clear();
					join.addAll(closed);
					join.addAll(open);
					
					for (int i = 0; i < join.size(); ++i) {
						if (x.stanje.equals(join.get(i).stanje)) {
							if (join.get(i).hcost < x.hcost) {
								insert = false;
								break;
							} else {
								closed.remove(join.get(i));
								open.remove(join.get(i));
								break;
							}
						}
					}
					
					if (insert) {
						if (open.isEmpty()) open.add(x);
						else {
							for (int i = 0; i < open.size(); ++i) {
								if (x.hcost < open.get(i).hcost) {
									open.add(i, x);
									break;
								}
								if ((i+1) == open.size()) {
									open.add(x);
									break;
								}
							}
						}
					}
				}
				
				
			}
			
			//error?
			if (open.isEmpty() && !goals.contains(node.stanje)) {
				System.out.println("fail");
				br.close();
				return;
			}
			
			//provjera optimistic
			if (!(node.cost >= h.get(z))) {
				System.out.printf("[ERR] h(%s) > h*: %.2f > %.2f\n", z, h.get(z), node.cost);
				oOk = false;
			}
		}
		
		//output
		if (oOk) System.out.println("Optimistic.");
		else System.out.println("Not optimistic.");
		System.out.println();
		System.out.printf(cOutput);
		if (cOk) System.out.println("Consistent.");
		else System.out.println("Not consistent.");
		
		br.close();
		return;

	}

}
