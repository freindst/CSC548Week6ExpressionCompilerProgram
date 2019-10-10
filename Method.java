import java.util.LinkedList;

public class Method {
	static char space = ' ';
	static char multi = '*';
	static char plus = '+';
	static char openPar = '(';
	static char closePar = ')';
	static String operators = "+-*()";
	
	private LinkedList<String> output;
	
	public Method() {
		output = new LinkedList<String>();			
	}
	
	//Shunting Yard algorithm. convert expression into output format
	public void Decompose(String expr) {
		Stack stack = new Stack();
		LinkedList<String> list = reformat(expr);
		for(String str : list) {
			if (str.length() == 1 && operators.indexOf(str) > -1) {
				switch (str.charAt(0)) {
				case '(':
					stack.Push(str);
					break;
				case '+':
				case '-':
					if (stack.Peek() != null && stack.Peek().contentEquals("*")) {
						output.add(stack.Pop());
					}
					stack.Push(str);
					break;
				case ')':
					String tmp = stack.Peek();					
					while(tmp != null && !tmp.contentEquals("(")) {
						output.add(stack.Pop());
						tmp = stack.Peek();
					}
					if (tmp.contentEquals("(")) {
						stack.Pop();
					}
					break;
				default:
					stack.Push(str);
					break;
				}
			} else {
				output.add(str);
			}
		}
		while(stack.Peek() != null ) {
			output.add(stack.Pop());
		}		
	}
	
	//split a string-form expression into a linked list. Each element is either a variable, a number or an operator
	private LinkedList<String> reformat(String expr){
		LinkedList<String> returnVal = new LinkedList<String>();		
		String curr = "";
		for(int i = 0; i < expr.length(); i++){
			char thisChar = expr.charAt(i);
			switch (thisChar){
				case ' ':
					if (curr.length() > 0){
						returnVal.add(curr);
						curr = "";
					}
					break;
				case '*':
				case '+':
				case '-':
				case '(':
				case ')':
					if (curr.length() > 0){
						returnVal.add(curr);
						curr = "";
					}
					returnVal.add("" + thisChar);
				break;
				default:
					curr += thisChar;
			}
		}
		if (!curr.isEmpty()) {
			returnVal.add(curr);
		}
		return returnVal;
	}
	
	//use shunting yard algorithm to split expressions
	public LinkedList<Operation> stacking(String expr, String variableName){
		Decompose(expr);
		LinkedList<Operation> returnVal = new LinkedList<Operation>();
		if (this.output.size() == 1) {
			Operation o = new Operation(this.output.getFirst(), "", " ", variableName);
			returnVal.add(o);
		} else {
			int varIndex = 0;
			while(this.output.size() > 1) {
				for(int i = 0; i< this.output.size(); i++ ) {
					String str = this.output.get(i);
					if (str.length() == 1 && operators.indexOf(str) > -1) {
						if (i >= 2){
							String varname = "Tmp" + ++varIndex;  
							Operation o = new Operation(this.output.get(i - 2), this.output.get(i-1), this.output.get(i), varname);
							returnVal.add(o);
							this.output.set(i, varname);
							this.output.remove( i - 1 );
							this.output.remove( i - 2 );
						}
						break;
					}
				}
			}
			if (returnVal.size() > 0) {
				returnVal.getLast().SetVarname(variableName);
			}
		}
		return returnVal;
	}
}
