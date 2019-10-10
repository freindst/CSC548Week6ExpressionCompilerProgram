//definition of binary operation
public class Operation {
	private String leftVar;
	private String rightVar;
	private Operator operator;
	private String Varname;
	
	public Operation(String left, String right, String op, String name) {
		this.leftVar = left;
		this.rightVar = right;
		switch (op) {
		case "+":
			this.operator = Operator.Plus;
			break;
		case "-":
			this.operator = Operator.Minus;
			break;
		case "*":
			this.operator = Operator.Multiply;
			break;
		case " ":
			this.operator = Operator.Assignment;
			break;
		}
		this.Varname = name;
	}

	public String GetLeftVar(){
		return this.leftVar;
	}

	public String GetRightVar(){
		return this.rightVar;
	}

	public Operator GetOperator(){
		return this.operator;
	}

	public String GetVarname(){
		return this.Varname;
	}
	
	public void SetVarname(String varname) {
		this.Varname = varname;
	}
	
	public Boolean CheckByName(String varname) {
		return this.Varname.contentEquals(varname);
	}
	
	public String Display() {
		String str = Varname + " = " + leftVar;
		if (this.operator == Operator.Plus) {
			str += " + ";
		} else if (this.operator == Operator.Minus) {
			str += " - ";
		} else if (this.operator == Operator.Multiply) {
			str += " * ";
		}
		str += rightVar;
		return str;
	}
}
