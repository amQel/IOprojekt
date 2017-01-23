import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;


public class DPLL {

	private static int length, width=3, size;
	static int COS=0;


	public static void main(String[] args) throws Exception {
		int[] lengthp = new int[2];
	    int[][] CNF = readFile(lengthp);
	    length = lengthp[0];
	    size = lengthp[1]+1;
	    
	    Formula f = new Formula(CNF);
	    f.saveFormToRFile(size);
	    f.removeClauses(0, size);
	    long diff = new Date().getTime();
//	    f.getStatistics(length, width, lengthp[1]+1);
		System.out.println(proceedDPLL(f, new Interpolation(size)));
//		System.out.print(COS);

		System.out.println((new Date().getTime()) - diff);
		

	}
	
	public static int[][] readFile(int[] length) throws Exception {
		int[][] formula = null;
		BufferedReader br = new BufferedReader(new FileReader("src/formtest.txt"));
		try {
		    String line = br.readLine();

		    int clauses = Integer.parseInt(line.split(" ")[1]);
		    int sol = Integer.parseInt(line.split(" ")[0]);
		    formula = new int[clauses][3];
		    length[0] = clauses;
		    length[1] = sol;
		    int i=0;
		    
		    line = br.readLine();
		    
		    while (line != null) {
		    	String[] nums = line.split("\\s+");
		    	int j=0;
		    	for (String string : nums) {

		    		if(string.length()>0 && 
		    				Integer.parseInt(string)!=0){
		    			formula[i][j] = Integer.parseInt(string);
			    		j++;
		    		}
				}
		    	i++;
		    	line = br.readLine();
		    	
		    }

		} finally {
		    br.close();
		}
		return formula;
	}

	
	public static boolean proceedDPLL(Formula f, Interpolation p){
//		COS++;
//	System.out.println("SOLUTION");
//		p.printSolution();
		if(f.form.length == 0) {
			p.printSolution();
			return true;
		}
		int PandF = f.getInterpolation(p, length, width);
//		System.out.println("PandF "+ PandF);
		if(PandF != 2) return PandF==1 ? true : false;
//		System.out.println("before propagation");
//		for(int i=0; i<f.form.length; i++){
//			for(int j=0; j<f.form[i].length; j++){
//				System.out.print(f.form[i][j]+ " ");
//			}
//			System.out.println();
//		}
		Formula propagated = new Formula(f, p, length, width);

		
//		System.out.println("before get pure");
//		for(int i=0; i<propagated.form.length; i++){
//			for(int j=0; j<propagated.form[i].length; j++){
//				System.out.print(propagated.form[i][j]+ " ");
//			}
//			System.out.println();
//		}
		if(propagated.consistent==false) return false;
		propagated.getStatistics(size, p);
		Interpolation newPT = new Interpolation(p.getSolution());
		newPT.getNextX(true);

		Interpolation newPF = new Interpolation(p.getSolution());
		newPF.getNextX(false);

		if(proceedDPLL(propagated, newPT)) return true;
		if(proceedDPLL(propagated, newPF)) return true;	
		return false;
		
	}
}
