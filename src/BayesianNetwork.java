import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @author bar
 * This class represents a bayesian network.
 * net made of list of vars.
 */
public class BayesianNetwork {
	ArrayList<Var>vars=new ArrayList<>();
	public BayesianNetwork(String s) {
		String[] check = s.split(" ");
		String[] temp = check[1].split(",");
		for (String string : temp) {
			vars.add(new Var(string));
		}
	}
	public BayesianNetwork(BayesianNetwork ot) {
		for(Var v:ot.vars) {
			vars.add(new Var(v));
		}
	}
	/**
	 * This function return the index of the var or -1 if the var not in the net.
	 * @param s
	 * @return the var index
	 */	
	public int GetByName(String s) {
		for (int i=0;i<vars.size();i++) {
			if(vars.get(i).name.equals(s))
				return i;
		}
		return -1;
	}
	/**
	 * This function return the var or null if the var not in the net.
	 * @param s
	 * @return the var
	 */	
	public Var GetVarByName(String s) {
		for (int i=0;i<vars.size();i++) {
			if(vars.get(i).name.equals(s))
				return vars.get(i);
		}
		return null;
	}
	/**
	 * This function return the network as string.
	 * @return the string of the net.
	 */
	public String toString() {
		String s="";
		for (Var var : vars) {
			s+=var.toString()+"\n";
		}
		return s;
	}
	/**
	 * @author bar
	 * This class represents a var.
	 * var made of name,list of possible values,list of parents,list of childrens and map of cpt.
	 */
	public class Var{
		private String name;
		ArrayList<String>values=new ArrayList<>();
		ArrayList<Var>parents=new ArrayList<>();
		ArrayList<Var>childrens=new ArrayList<>();
		HashMap<List<String>,Float> cpt=new HashMap<List<String>,Float>();

		public Var(String name) {
			this.name=name;
		}
		public Var(Var ot) {
			this.name=ot.name;
			for(String s : ot.values) {
				values.add(new String(s));
			}
			for(Var var : ot.parents) {
				parents.add(var);
			}
			for(Var var : ot.childrens) {
				childrens.add(var);
			}
			cpt=copy(ot.cpt);
		}
		public String toString() {
			String s="";
			s="name is : "+name+"\n";
			s+="values is : ";
			for (String str : values) {
				s+=str+",";
			}
			s+="\nparents is : ";
			for (Var str : parents) {
				s+=str.name+",";
			}
			s+="\nchilds is : ";
			for (Var str : childrens) {
				s+=str.name+",";
			}
			s+="\nCpt is : ";
			s+=cpt.toString();
			return s;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * deep copy of the cpts.
		 * @return the new cpt.
		 */
		private HashMap<List<String>,Float>copy(HashMap<List<String>,Float> original){
			HashMap<List<String>,Float>copy=new HashMap<>();
			for(Map.Entry<List<String>,Float>entry: original.entrySet()) {
				copy.put(new ArrayList<>(entry.getKey()), entry.getValue());
			}
			return copy;
		}
	}
}
