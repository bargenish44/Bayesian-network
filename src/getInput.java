import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;;
/**
 * @author bar
 * This class read the input text.
 */
public class getInput {

	BayesianNetwork net;
	public getInput(String s) {this.url=s;}
	private String url;
	private String[] input;
	
	public void readFileAsString()throws Exception 
	{
		String data = ""; 
		data = new String(Files.readAllBytes(Paths.get(this.url)));
		input=data.split("(\\n\\r|\\n|\\r){3}");
		CreateNet( input);
	}
	/**
	 * This function read the text and convert him into a bayesian network
	 * it also runs the algorithms and save the results.
	 * @param result
	 * @throws Exeption
	 */
	private void CreateNet(String result[]) throws Exception {
		net = new BayesianNetwork(result[0]);
		ArrayList<String>InverseCpt=new ArrayList<>();
		ArrayList<String>InverseCpt2=new ArrayList<>();
		float InverseCptValue=1;
		for (int i=1;i<result.length-1;i++) {	//read all the vars
			String[] lines = result[i].split("(\\n\\r|\\n|\\r){2}");
			String []VarName=lines[0].split(" ");
			int val =net.GetByName(VarName[1]);
			String words[]=lines[1].split(" |,");
			for(int j=1;j<words.length;j++) 		// set values
				net.vars.get(val).values.add(words[j]);
			words=lines[2].split(" |,");
			for(int j=1;j<words.length;j++) 	//set parents
				if(!words[j].equals("none")) {
					net.vars.get(val).parents.add(net.GetVarByName(words[j]));
					if(!net.GetVarByName(words[j]).childrens.contains(net.vars.get(val)))
						net.GetVarByName(words[j]).childrens.add(net.vars.get(val));
				}
			for(int check=4;check<lines.length;check++) {	//set cpt's
				words=lines[check].split(",|=");
				words=Arrays.stream(words).filter(x -> !x.isEmpty()).toArray(String[]::new);
				int vPSize=net.vars.get(val).parents.size();
				int ind=0;
				boolean ans=true;
				InverseCpt.addAll(net.vars.get(val).values);
				for(int k=0;k<net.vars.get(val).values.size()-1;k++) {//create the cpt's
					String[]cpts=new String[vPSize+1];
					for(ind=0;ind<vPSize;ind++) {
						cpts[ind]=words[ind];
						if(ans) {
							InverseCpt2.add(cpts[ind]);
						}
					}
					ans=false;
					cpts[ind]=words[ind+k*2];
					float ctf=Float.parseFloat(words[ind+k*2+1]);
					InverseCptValue-=ctf;
					List<String>l=new ArrayList<String>();
					for(int kl=0;kl<cpts.length;kl++) {
						l.add(cpts[kl]);
					}
					net.vars.get(val).cpt.put(l, ctf);
					for(int index=0;index<InverseCpt.size();index++) {
						for(int kl=0;kl<cpts.length;kl++) {
							if(InverseCpt.size()>0) {
								if(InverseCpt.get(index).equals(cpts[kl])) { 
									InverseCpt.remove(index);
									break;
								}
							}
						}
					}
				}
				List<String>l=new ArrayList<String>();
				l.addAll(InverseCpt2);
				l.addAll(InverseCpt);
				net.vars.get(val).cpt.put(l, InverseCptValue);
				InverseCpt2.clear();
				InverseCptValue=1;
				InverseCpt.clear();
			}
		}
		String[] r = result[result.length-1].split("\n");
		String ans="";
		for (int i=1;i<r.length;i++) {
			Algorithms alg = new Algorithms(net);
			r[i] = r[i].replaceAll("([\\n\\r]+\\s*)*$", "");
			if(r[i].startsWith("P(")) {
				ans+=alg.Variable_elimination(r[i])+"\n";
			}
			else {
				ans+=alg.Bayes_ball(r[i])+"\n";
			}

		}
		WriteToFile(ans);
	}
	/**
	 * This function export a new file with the results.
	 * @param output
	 * @throws Exeption
	 */
	public void WriteToFile(String output) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));
		writer.write(output);
		writer.close();
	}
}