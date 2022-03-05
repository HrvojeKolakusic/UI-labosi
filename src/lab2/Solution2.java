package lab2;

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
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Solution2 {
	
	static Map<String, Integer> rang;
	static List<String> sos;
	static List<String> nove;
	static List<Par> parovi;
	static List<String> klauzale;
	static String resolvent;
	static List<String> join;
	static String cilj = "";
	static String print = "";
	static int number = 1;
	static boolean verbose = false;
	
	
	
	public static void main(String[] args) throws IOException {
		
		
		String ulaz = "";
		String podzadatak = args[0];
		Path popisKlauzula = Paths.get(args[1]);
		
		if ((podzadatak.equals("resolution") || podzadatak.equals("cooking_interactive")) 
				&& (args.length == 3) && args[2].equals("verbose")) verbose = true;
		
		BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new BufferedInputStream(
								new FileInputStream(popisKlauzula.toString()))));
		
		klauzale = new ArrayList<>();
		String line;
		
		rang = new HashMap<>();
		sos = new ArrayList<>();
		nove = new ArrayList<>();
		parovi = new ArrayList<>();
		join = new ArrayList<>();
		resolvent = "";
		
		while ((line = br.readLine()) != null) {
			if (line.startsWith("#")) continue;
			line = line.toLowerCase();
			klauzale.add(line);
			cilj = line;
		}
		
		if (podzadatak.equals("cooking_interactive")) {
			if ((args.length == 3) && args[2].equals("verbose")) verbose = true;
			Scanner sc = new Scanner(System.in);
			
			while (true) {
				
				for (String x : klauzale) {
					System.out.println("> " + x);
				}
				System.out.println();
				
				System.out.println("Enter query: ");
				ulaz = sc.nextLine();
				
				if (ulaz.endsWith("+")) {
					ulaz = ulaz.split("\\+")[0].trim().toLowerCase();
					klauzale.add(ulaz);
					System.out.println("added " + ulaz);
				} else if (ulaz.endsWith("-")) {
					ulaz = ulaz.split("\\-")[0].trim().toLowerCase();
					klauzale.remove(ulaz);
					System.out.println("removed " + ulaz);
				} else if (ulaz.endsWith("?")) {
					ulaz = ulaz.split("\\?")[0].trim().toLowerCase();
					cilj = ulaz;
					funkcija();
				} else if (ulaz.equals("exit")) {
					sc.close();
					br.close();
					return;
				}
				
			}
		}
		
		if (podzadatak.equals("cooking_test")) {
			Path popisNaredbi = Paths.get(args[2]);
			if ((args.length == 4) && args[3].equals("verbose")) verbose = true;
			br.close();
			br = new BufferedReader(
					new InputStreamReader(
							new BufferedInputStream(
									new FileInputStream(popisNaredbi.toString()))));
			
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) continue;
				ulaz = line;
				
				if (ulaz.endsWith("+")) {
					ulaz = ulaz.split("\\+")[0].trim().toLowerCase();
					klauzale.add(ulaz);
				} else if (ulaz.endsWith("-")) {
					ulaz = ulaz.split("\\-")[0].trim().toLowerCase();
					klauzale.remove(ulaz);
				} else if (ulaz.endsWith("?")) {
					ulaz = ulaz.split("\\?")[0].trim().toLowerCase();
					cilj = ulaz;
					funkcija();
				}
			}
			
			br.close();
			return;
		}
		
		klauzale.remove(klauzale.size()-1);
		funkcija();
		
		br.close();
	}
	
	static void funkcija() {
		
		boolean exit = false;
		
		rang.clear();
		sos.clear();
		print = "";
		number = 1;
		nove.clear();
		
		for (String x : klauzale) {
			print += number + ". " + x + "\n";
			rang.put(x, number);
			number++;
		}
		
		String nCilj = negiraj(cilj);
		
		// popravak
		
		print += "====================\n";
		String[] split = nCilj.split(" v ");
		for (String x : split) {
			sos.add(x);
			print += number + " " + x + "\n";
			rang.put(x, number);
			number++;
		}
		print += "====================\n";
		
		// popravak
		
		
		while (true) {
			
			for (Par par : selectClauses()) {
				resolvent = resolve(par);
				if (resolvent.equals("NIL")) {
					if (verbose) {
						System.out.print(print);
						System.out.println(number + ". NIL (" + rang.get(par.prvi) + ", " + rang.get(par.drugi) + ")");
						System.out.println("====================");
					}
					System.out.println(cilj + " is true");
					exit = true;
					break;
				}
				if (!resolvent.equals("") && !rang.containsKey(resolvent)) {
					nove.add(resolvent);
					print += number + ". " + resolvent + " (" + rang.get(par.prvi) + ", " + rang.get(par.drugi) + ")\n";
					rang.put(resolvent, number);
					number++;
				}
			}
			
			if (exit) break;
			
			join.clear();
			join.addAll(klauzale);
			join.addAll(sos);
			if (join.containsAll(nove)) {
				if (verbose) System.out.println(print);
				System.out.println(cilj + " is unknown");
				break;
			}
			sos.addAll(nove);
		}
	}
	
	static String resolve(Par par) {
		boolean found = false;
		
		if (par.prvi.equals(par.drugi)) return "";
		if (par.prvi.equals("~" + par.drugi) || par.drugi.equals("~" + par.prvi)) {
			return "NIL";
		}
		
		String[] split1 = par.prvi.split(" v ");
		String[] split2 = par.drugi.split(" v ");
		
		for (int i = 0; i < split1.length; ++i) {
			split1[i] = split1[i].trim();
		}
		for (int i = 0; i < split2.length; ++i) {
			split2[i] = split2[i].trim();
		}
		
		Set<Integer> skrati1 = new TreeSet<>();
		Set<Integer> skrati2 = new TreeSet<>();
		
		for (int i = 0; i < split1.length; ++i) {
			for (int j = 0; j < split2.length; ++j) {
				if (split1[i].equals("~" + split2[j]) || split2[j].equals("~" + split1[i])) {
					skrati1.add(i);
					skrati2.add(j);
					found = true;
				}		
			}
		}
		
		if (!found) return "";
		
		String returnString = "";
		Set<String> retSet = new TreeSet<>();
		
		for (int i = 0; i < split1.length; ++i) {
			if (!skrati1.contains(i)) retSet.add(split1[i]);
		}
		for (int i = 0; i < split2.length; ++i) {
			if (!skrati2.contains(i)) retSet.add(split2[i]);
		}
		
		int brojac = 1;
		for (String x : retSet) {
			returnString += x;
			if (!(brojac == retSet.size())) returnString += " v ";
			brojac++;
		}
		
		return returnString;
				
				
	}
	
	static List<Par> selectClauses() {
		List<Par> returnList = new ArrayList<>();
		
		for (String x : sos) {
			for (String y : sos) {
				returnList.add(new Par(x, y));
			}
			for (String y : klauzale) {
				returnList.add(new Par(x, y));
			}
		}
		
		return returnList;
	}
	
	static String negiraj(String neg) {
		String[] split = neg.split( "v ");
		String retStr = "";
		for (int i = 0; i < split.length; ++i) {
			split[i] = split[i].trim();
			if (split[i].startsWith("~")) {
				split[i] = split[i].substring(1, split[i].length());
			} else {
				split[i] = "~" + split[i];
			}
			retStr += split[i];
			if (!(i == split.length - 1)) retStr += " v ";
		}
		return retStr;
	}
}
