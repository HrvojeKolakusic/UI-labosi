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

public class Astar {

	private static Set<String> goals;
	private static String start;
	private static Map<String, String> next;
	private static List<Node> open;
	private static ArrayList<String> output;
	private static Set<String> visited;
	private static Map<String, Double> h;
	private static List<Node> closed;
	private static List<Node> join;
	
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
		Path heuristicPath = Paths.get("D:\\Faks\\UI\\lab1\\istra_heuristic.txt");
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new BufferedInputStream(
								new FileInputStream(stanjaPath.toString()))));
		
		
		next = new HashMap<>();
		goals = new TreeSet<>();
		visited = new TreeSet<>();
		closed = new ArrayList<>();
		join = new ArrayList<>();
		String line;
		String[] split;
		int k = 0;
		int koraci = 0;
		
		//start i cilj
		while (true) {
			line = br.readLine();
			if (line.startsWith("#")) continue;
			if (k==0) {
				start = line;
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
		open.add(new Node(start, (double)0, (double)0, 0, null));
		Node node = new Node("empty", (double)0, (double)0, 0, null);
		
		//main loop
		while (!open.isEmpty()) {
			node = open.get(0);
			koraci++;
			open.remove(0);
			if (goals.contains(node.stanje)) {
				break;
			}
			closed.add(node);
			boolean insert;
			
			//expand
			for (Node x : expand(node)) {
				insert = true;
				join.clear();
				join.addAll(closed);
				join.addAll(open);
				
				//provjeri closed U open
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
				
				//sortiran unos ako insert
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
			System.out.println(visited);
			br.close();
			return;
		}
		
		//output
		System.out.println("States visited = " + koraci);
		System.out.printf("Found path of length %d with total cost %.2f\n", node.dubina + 1, node.cost);
		
		Node curr = node;
		output = new ArrayList<>();
		while (!curr.stanje.equals(start)) {
			curr = curr.parent;
			output.add(0, curr.stanje);
		}
		
		for (String x : output) {
			System.out.println(x + " =>");
		}
		
		System.out.println(node.stanje);
		
		br.close();
		return;

	}

}
