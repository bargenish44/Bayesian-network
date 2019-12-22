import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author bar
 * This class represents the object Factor.
 */
public class Factor {
	List<String> cols;
	HashMap<List<String>, Float> rows;
	/**
	 * Empty Constructor
	 */
	public Factor() {
		cols=new ArrayList<>();
		rows=new HashMap<>();
	}
	/**
	 * Intelizion contractor.
	 * @param v
	 */
	public Factor(BayesianNetwork.Var v) {
		this.cols = new ArrayList<String>();
		for(BayesianNetwork.Var var:v.parents) {
			this.cols.add(var.getName());
		}
		this.cols.add(v.getName());
		this.rows=v.cpt;
	}
	public void applyEvidance(ArrayList<Pair> ev){
		HashMap<List<String>, Float> tempHash =new HashMap<>();
		List<List<String>> temp= new ArrayList<>(rows.keySet());
		ArrayList<Integer>index=new ArrayList<>();
		for(Pair p:ev) {
			int indx = cols.indexOf(p.getFirst());
			if(indx!=-1)
				index.add(indx);
		}
		Collections.sort(index);
		Collections.reverse(index);
		for(int i=0;i<temp.size();i++) {
			boolean ans=true;
			for(Pair p:ev) {
				int indx = cols.indexOf(p.getFirst());
				if(indx!=-1) {
					if(!temp.get(i).get(indx).equals(p.getSecond())) {
						ans=false;
					}
				}
			}
			if(ans) {
				Float flo=this.rows.get(temp.get(i));
				for(int in:index) {
					temp.get(i).remove(in);
				}
				tempHash.put(temp.get(i), flo);
			}
		}
		for(int in:index) {
			this.cols.remove(in);
		}
		this.rows=tempHash;
	}
	/**
	 * This function normalize the table of the Factor.
	 * Since we're using this function only on the end of the algorithm, 
	 * we're able to assume that there is only one factor.
	 */
	public void NormFactor() {
		float lambda = 0;
		for(Float f : this.rows.values()) {
			lambda+=f;
		}
		for(List<String>key : this.rows.keySet()) {
			this.rows.put(key, this.rows.get(key)/lambda);
		}
		Algorithms.sumOps+=rows.size()-1;
	}
	/**
	 * This function summarizes all the equivalent values into a new factor.
	 * @param v
	 * @return the new factor.
	 */
	public Factor SumVar(BayesianNetwork.Var v) {
		int index=cols.indexOf(v.getName());
		Factor f =new Factor();
		f.cols=this.cols;
		f.cols.remove(index);
		for(List<String> valus : this.rows.keySet()) {
			Float val = this.rows.get(valus);
			valus.remove(index);
			if(f.rows.containsKey(valus)) {
				f.rows.put(valus, f.rows.get(valus)+val);
				Algorithms.sumOps++;
			}
			else {
				f.rows.put(valus, val);
			}
		}
		return f;
	}
	/**
	 * This function join two factors.
	 * @See the algorithm variable elimination for more details.
	 * @param ot
	 * @param net
	 * @return the new factor.
	 */
	public Factor MultFactors(Factor ot,BayesianNetwork net) {
		List<String>newCols=new ArrayList<>();
		for(String runner : cols) 
			if(!newCols.contains(runner))
				newCols.add(runner);
		for(String runner :ot.cols) 
			if(!newCols.contains(runner))newCols.add(runner);
		ArrayList<List<String>>newVals=GetAllVals(newCols,net);
		HashMap<List<String>, Float> tempHash =new HashMap<>();
		for(List<String> list:newVals) {
			float f1=GetProb(this, newCols, list);
			float f2=GetProb(ot,newCols,list);
			tempHash.put(list, f1*f2);
			Algorithms.multOps++;
		}
		Factor f=new Factor();
		f.cols=newCols;
		f.rows=tempHash;
		return f;
	}
	/**
	 * This function find all the possible values for the given colms list.
	 * @param cols
	 * @param net
	 * @return the list of the values.
	 */
	
	private ArrayList<List<String>> GetAllVals(List<String>cols,BayesianNetwork net) {
		Queue<List<String>> newVals = new LinkedList<>();
		newVals.add(new ArrayList<String>());
		for(String col:cols) {
			List<String> colVals = net.GetVarByName(col).values;
			int size = newVals.size();
			for(int i =0;i<size;i++) {
				List<String> ls = newVals.remove();
				for(String val:colVals) {
					List<String> newLS = new ArrayList(ls);
					newLS.add(val);
					newVals.add(newLS);
				}
			}
		}
		return (new ArrayList<>(newVals));
	}
	/**
	 * This function find all the possible probability for the given column list.
	 * @param cols
	 * @param vals
	 * @param f
	 * @return the prob.
	 */
	private static float GetProb(Factor f,List<String>cols,List<String>vals) {
		List<String>fVals=new ArrayList<>();
		for(String runner: f.cols) {
			int index=cols.indexOf(runner);
			fVals.add(vals.get(index));
		}
		return f.rows.get(fVals);
	}
	/**
	 * This function checks if the factor contains a var.
	 * @param v
	 * @return boolean.
	 */
	public boolean containtsVar(BayesianNetwork.Var v) {
		return cols.contains(v.getName());
	}
	/**
	 * This function return the number of rows in the factor.
	 * @return the number.
	 */
	public int size() {
		return rows.size();
	}
	/**
	 * This function return the factor as string.
	 * @return the string of the factor.
	 */
	public String toString() {
		return cols.toString()+"\n"+rows.toString();
	}
}