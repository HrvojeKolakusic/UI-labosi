package lab1;

public class Node {

	public String stanje;
	public Node parent;
	public Double cost, hcost;
	public int dubina;
	
	public Node(String stanje, Double cost, Double hcost, int dubina, Node parent) {
		this.stanje = stanje;
		this.cost = cost;
		this.hcost = hcost;
		this.dubina = dubina;
		this.parent = parent;
	}
	
	public Node(String stanje, Double cost, int dubina, Node parent) {
		this.stanje = stanje;
		this.cost = cost;
		this.hcost = (double)0;
		this.dubina = dubina;
		this.parent = parent;
	}
	
	public Node(String stanje, int dubina, Node parent) {
		this.stanje = stanje;
		this.cost = (double)0;
		this.hcost = (double)0;
		this.dubina = dubina;
		this.parent = parent;
	}
}
