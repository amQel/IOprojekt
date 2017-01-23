
public class Interpolation {
    public int[] solution = null;
	
	public int[] getSolution() {
		return solution;
	}

	public Interpolation(int i) {
		this.solution = new int[i];
		this.solution[0]=3;
		for (int j=1; j<i; j++){
			solution[j] = 2;
		}
		// TODO Auto-generated constructor stub
	}

	public Interpolation(int[] solution2) {
		int[] solut = new int[solution2.length];
		for(int i=0; i<solut.length; i++) solut[i]=solution2[i];
		this.solution = solut;
	}

	public int getValue(int[] is) {
		int ans = 0;
		int mnz = 1;
		for(int i=0; i<is.length; i++){
			if(is[i]<0) mnz=-1;
			if(solution[mnz*is[i]] == 2){
				ans+=2;
			} else 
			{
			 if(mnz>0){
					if(solution[mnz*is[i]] == 1){
						ans = 1;
						break;
					}
				} else {
					if(solution[mnz*is[i]] == 0){
						ans = 1;
						break;
					}
				}
			}
				
			mnz = 1;
		}
		if(ans>=2) ans = 2;
//		System.out.println(ans);
		return ans;
	}

	public Interpolation getNextX(boolean b) {

		for(int i=0; i<solution.length; i++){
			if(solution[i]==2) {
				if(b) solution[i]=1;
				else solution[i]=0;
				break;
			}
		}
		
		Interpolation in = new Interpolation(this.solution);
		
		return in;
	}


	public void printSolution() {
		for (int i=0; i<solution.length; i++){
			System.out.print("x" + i + "=" + solution[i] + " ");
		}
		System.out.println();
		
	}

}
