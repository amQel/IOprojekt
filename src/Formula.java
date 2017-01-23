import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class Formula {
	public int[][] form;
	public boolean consistent = true;
	public Formula(int[][] form){
		this.form = form;
	}

	
	public Formula(Formula f, Interpolation p, int length, int width) {
		int[][] fForm = f.getForm();
		ArrayList<int[]> listaForm = new ArrayList();
		for(int i=0; i<fForm.length; i++){
			List<Integer> forms = new ArrayList();
			for (int is : fForm[i]) {
				forms.add(is);
			}
			int[] formsToAdd = new int[forms.size()];
			int j=0;
			for (int k : forms) {
				formsToAdd[j] = k;
				j++;
			}
			if(formsToAdd != null) listaForm.add(formsToAdd);
		}
		int pom=0;
		this.form = new int[listaForm.size()][3];
		for (int[] is : listaForm) {
			if(is!= null){
				this.form[pom]= is;
				pom++;
			}
		}
		int[] solution = p.getSolution();
		
		for(int i=0; i<solution.length; i++){
			if(solution[i] == 1){
				int cntt = this.removeClauses(i, solution.length);
				if(cntt==0) this.consistent = false;
			} else if (solution[i] == 0){
				int cntt = this.removeClauses(-1*i, solution.length);
				if(cntt==0) this.consistent = false;
			}
		}
	}


	public int removeClauses(int i, int literals) {
//		System.out.println("REMOVE CLAUSES + " + i);
		ArrayList<int[]> lista = new ArrayList();
		
		for(int j=0; j<form.length; j++){
			int czyP=0;
			for (int js : form[j]) {
				if(js == i) czyP=1;//form[j][0] != i  && form[j][1] != i  && form[j][2] != i
			}
			if(czyP==0) {
				List<Integer> allLiterals = new ArrayList<Integer>();
				int[] literalsToAdd;
				for(int p=0;p<form[j].length;p++){
					if(form[j][p]!=(-1)*i) allLiterals.add(form[j][p]);
				}
				literalsToAdd = new int[allLiterals.size()];
				int l=0;
				for (int k : allLiterals) {
					literalsToAdd[l] = k;
					l++;
				}
				lista.add(literalsToAdd);
			}
			
			
		}
		//System.out.println(lista.size());
		int k=0;
		int[][] nForm = new int[lista.size()][3];
		for (int[] js : lista) {
			nForm[k]= js;
			k++;
		}
		form = nForm;
		int cons = 1;
		int[] potentialInconsistency = new int[literals];
		
		for(int j=0; j<form.length; j++){
			if(form[j].length == 1){
				int mnz = 1;
				if(form[j][0]<0) mnz = -1;
				if((potentialInconsistency[form[j][0]*mnz]<0 && mnz==1) || (potentialInconsistency[form[j][0]*mnz]>0 && mnz==-1) ){
				cons=0;	
				}
				if(potentialInconsistency[form[j][0]*mnz]==0)potentialInconsistency[form[j][0]*mnz] = mnz;
				
			}
//			for (int js : form[j]) {
//				System.out.print(js + " ");
//			}
//			System.out.println();
		}
//		if(cons == 0 ); System.out.print("IN CONCS " );
		return cons;
	}


	private int[][] getForm() {
		
		return this.form;
	}


	public int getInterpolation(Interpolation p, int length, int width) {
		int ans = 1;
		int current;
		for(int i=0; i<form.length; i++){
			current = p.getValue(form[i]);
//			System.out.println(current+ " "+ i);
			if(current == 0){
//				System.out.println("false formula " + i);
				return 0;

			} else {
			ans*=current;	
			}
			if(ans>=2) ans = 2;
		}
		if(ans == 1) p.printSolution();		
		return ans;
	}

	public void getStatistics(int size, Interpolation p) {
//		System.out.print("SIZE + " +size);
		int[] ansT = new int[size];
		int[] ansF = new int[size];
		int[] settingVars = new int[size];
		for (int i=0; i<form.length; i++){
			if(form[i].length == 1){
				if(form[i][0]>0) settingVars[form[i][0]] = 1;
				else settingVars[-1*form[i][0]] = -1;
			}
			for(int j=0; j<form[i].length; j++){
				if(form[i][j] >0){
					ansT[form[i][j]]++;
				} else {
					ansF[-1*form[i][j]]++;
				}
			}
		}
		
		for(int i=1; i<size; i++){
			if(settingVars[i]!=0){
				if(settingVars[i]>0) {
					p.solution[i]= 1;
					removeClauses(i, p.solution.length);
				}
				else if(settingVars[i]<0){
					p.solution[i]= 0;
					removeClauses(-1*i, p.solution.length);
				}
			}else {
				if(ansT[i]>0 && ansF[i]==0) {

					p.solution[i] = 1;
					removeClauses(i, p.solution.length);
				}
				if(ansF[i]>0 && ansT[i]==0) {

					p.solution[i] = 0;
					removeClauses(-1*i, p.solution.length);
				}
			}
			
		}
		
	}


	public void saveFormToRFile(int size)  throws Exception {
		    PrintWriter writer = new PrintWriter("src/fitnessR.txt", "UTF-8");
		   
		    writer.println("fitnessFunc <- function(x) {");
		    
		    
		writer.println("p<-0");
		for(int i=0; i<form.length; i++){
			String p = "if(";
			for(int j=0; j<form[i].length; j++){
				p+= "(x["+Math.abs(form[i][j])+"]";
				if(form[i][j]<0) p+="+1)%%2+";
				else p+= ")+";
			}
			p=p.substring(0, p.length()-1);
			p+=">0) p<- p+1";
			System.out.println(p);
			writer.println(p);
		}
		 writer.println("return (-p)");
		 writer.println("}\n");
		 writer.println(" GAproblem <- rbga.bin(size = " + (size-1) + ", popSize = 200, iters = 500, mutationChance = 0.01, elitism = T, evalFunc = fitnessFunc)");
		 writer.println("summary(GAproblem, echo=TRUE)");
		 
		writer.close();
//		((x[1]+1)%%2+(x[2]+1)%%2)%%2
		
	}

}
