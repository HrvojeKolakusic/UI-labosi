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

public class Usirinu {

	private static Set<String> goals;
	private static String start;
	private static Map<String, String> next;
	private static List<Node> open;
	private static ArrayList<String> output;
	private static Set<String> visited;
	
	public static ArrayList<Node> expand(Node node) {
		ArrayList<Node> nextList = new ArrayList<>();
		String prijelazi = next.get(node.stanje);
		String[] split = prijelazi.split(" ");
		for (String x : split) {
			nextList.add(new Node(x.split(",")[0], node.dubina + 1, node));
		}
		return nextList;
	}
	
	public static void main(String[] args) throws IOException {
		
		Path stanjaPath = Paths.get("D:\\Faks\\UI\\lab1\\istra.txt");
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new BufferedInputStream(
								new FileInputStream(stanjaPath.toString()))));
		
		next = new HashMap<>();
		goals = new TreeSet<>();
		visited = new TreeSet<>();
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
		
		open = new ArrayList<>();
		open.add(new Node(start, 0, null));
		Node node = new Node("empty", 0, null);
		
		//main loop
		while (!open.isEmpty()) {
			node = open.get(0);
			koraci++;
			open.remove(0);
			if (goals.contains(node.stanje)) {
				break;
			}
			visited.add(node.stanje);
			
			for (Node x : expand(node)) {
				if(!visited.contains(x.stanje)) open.add(x);
			}
		}
		
		//error
		if (open.isEmpty() && !goals.contains(node.stanje)) {
			System.out.println("fail");
			br.close();
			return;
		}
		
		//output
		System.out.printf("States visited = %d\n", koraci);
		System.out.printf("Found path of length %d with total cost %d\n", node.dubina + 1, koraci);
		
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
