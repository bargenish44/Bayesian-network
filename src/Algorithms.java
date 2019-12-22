import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * @author bar
 * Class Algorithms.
 * This class includes the bayes ball and variable elimination algorithms.
 */
public class Algorithms {
	private BayesianNetwork net;
	public static int sumOps=0;
	public static int multOps=0;
	/**
	 * enum of direction for the bayes Ball alg.
	 */
	private enum direction{
		up,down;
	}
	public Algorithms(BayesianNetwork net) {
		this.net=net;
	}
	
	/**
	 * This function implements the bayes ball algorithm.
	 * This algo checks if two nodes are conditionally independent of the start node.
	 * @param s - the querry
	 * @throws java.lang.Exception
	 * @return the result of the algorithm
	 */
	public String Bayes_ball(String s) throws Exception {	
		boolean ans=false;
		BayesianNetwork.Var source;
		BayesianNetwork.Var dest;
		direction direc=direction.up;
		ArrayList<BayesianNetwork.Var>ind=new ArrayList<>();
		if(s.charAt(s.length()-1)=='|') {
			String[]str=s.split("-|\\|");
			source=net.GetVarByName(str[0]);
			dest=net.GetVarByName(str[1]);
		}
		else {
			String[]str=s.split("-|=|\\||,");
			source=net.GetVarByName(str[0]);
			dest=net.GetVarByName(str[1]);
			for(int i=2;i<str.length;i++) {
				ind.add(net.GetVarByName(str[i]));
			}
		}
		String output="";
		ans=Bayes_Ball_Alg(null,source,dest,ind,direc);
		if(ans)output+="yes";
		else output+="no";
		return output;
	}
	/**
	 * This help function travel on the graph in DFS and checks if it possible to get from one vertex to another.
	 * @param last - the node that i came from
	 * @param source - the current node
	 * @param dest - the destion node
	 * @param ind - all the dependencies
	 * @param dir - my current direction
	 * @return the result of the algorithm
	 */
	private boolean Bayes_Ball_Alg(BayesianNetwork.Var last,BayesianNetwork.Var source,BayesianNetwork.Var dest,ArrayList<BayesianNetwork.Var>ind,direction dir) {
		if(source==dest) return false;
		if(ind.contains(source)) {
			if(dir==direction.up) {
				return true;
			}
			else {
				for(BayesianNetwork.Var parent : source.parents) {
					if(!Bayes_Ball_Alg(source,parent,dest,ind,direction.up))
						return false;
				}
				return true;
			}
		}
		else {
			if(dir==direction.down) {
				for(BayesianNetwork.Var child : source.childrens) {
					if(child!=last&&!Bayes_Ball_Alg(source,child,dest,ind,direction.down))
						return false;
				}
				return true;
			}
			else {
				for(BayesianNetwork.Var parent : source.parents) {
					if(parent!=last&&!Bayes_Ball_Alg(source,parent,dest,ind,direction.up))
						return false;
				}
				for(BayesianNetwork.Var child : source.childrens) {
					if(child!=last&&!Bayes_Ball_Alg(source,child,dest,ind,direction.down))
						return false;
				}
				return true;
			}
		}
	}
	/**
	 * This function implements the algorithm Variable elimination.
	 * @param s - the querry
	 * @throws java.lang.Exception
	 * @return the result of the query
	 */
	public String Variable_elimination(String s) throws Exception {
		sumOps=0;
		multOps=0;
		this.net=new BayesianNetwork(net);
		List<String>querry=new ArrayList<>();
		ArrayList<BayesianNetwork.Var>deletion=new ArrayList<>();
		ArrayList<Factor>myFactors=new ArrayList<>();
		ArrayList<String> wantedValue=new ArrayList<>();
		for( BayesianNetwork.Var v : net.vars) {
			myFactors.add(new Factor(v));
		}
		s=s.substring(2);
		String str[]=s.split("\\),");
		String str2[]=str[0].split("\\|");
		String str3[]=str2[1].split(",");
		wantedValue.add(str2[0].split("=")[1]);
		for (String string : str3) {
			querry.add(string);
		}
		try {
			String str4[]=str[1].split("-");
			for (String string : str4) 
				deletion.add(net.GetVarByName(string));
		}catch(Exception ArrayIndexOutOfBoundsException) {};
		ArrayList<Pair>ev=new ArrayList<>();
		for (String st:querry) {
			String[]strarr=st.split("=");
			ev.add(new Pair(strarr[0],strarr[1]));
		}
		for( Factor f : myFactors) 
			f.applyEvidance(ev);		
		for(BayesianNetwork.Var v : deletion) {
			ArrayList<Factor>mults = GetFacByVar(v, myFactors);
			Factor f1=mults.get(0);
			if(mults.size()<2) {
				myFactors.add(f1.SumVar(v));
				continue;
			}
			Factor f2=mults.get(1);
			Factor f = f1.MultFactors(f2,net);
			for(int i=2;i<mults.size();i++) {
				f=f.MultFactors(mults.get(i),net);
			}
			Factor ff = f.SumVar(v);
			myFactors.add(ff);
		}
		//mult all factors in myFactors
		Factor last = null;
		for(Factor f : myFactors) {
			if(last==null)
				last = f;
			else 
				last = last.MultFactors(f,net);
		}
		last.NormFactor();
		last.rows.get(wantedValue);
		Float f = last.rows.get(wantedValue);
		String fOutput = Float.toString(f);
		if(f!=1) {
			if(fOutput.length()>7)
				fOutput=fOutput.substring(0, 7);
			else {
				while(fOutput.length()<7)
					fOutput=fOutput.concat("0");
			}
		}
		fOutput+=","+sumOps+","+multOps;
		return fOutput;
	}
	/**
	 * This function return sorted list of factor that contains specific var.
	 * @param v - the var that we want
	 * @parama arr - list of factors
	 * @return the new list of factors
	 */
	private ArrayList<Factor> GetFacByVar(BayesianNetwork.Var v, ArrayList<Factor>arr){
		ArrayList<Factor>output=new ArrayList<>();
		for(int i = 0;i<arr.size();i++) {
			Factor f = arr.get(i); 
			if(f.containtsVar(v)) {
				output.add(f);
				arr.remove(i);
				i--;
			}
		}
		output.sort(new Comparator<Factor>() {
			@Override
			public int compare(Factor o1, Factor o2) {
				return o1.size()-o2.size();
			}
		});
		return output;
	}
}