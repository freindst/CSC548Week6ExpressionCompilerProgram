import java.util.LinkedList;

public class MipsCCompiler 
{
	private DataMemory theDataMemory;
	private RegisterCollection theRegisterCollection;
	private SrcReader theSrc;
	private String filename;
	private VariableList vars;
	
	public MipsCCompiler(String filename)
	{
		this.filename = filename;
		this.theSrc = new SrcReader(filename);
		this.theDataMemory = new DataMemory(200000);
		this.theRegisterCollection = new RegisterCollection(16);
		vars = new VariableList();
		String currLine = this.theSrc.getNextLine();
		while (!currLine.contentEquals("EOF")) {			
			String[] words = currLine.replaceAll(";", "").split(" ");
			if (words[0].contentEquals("int")) {
				vars.Assign(words[1], this.theRegisterCollection, this.theDataMemory);
				Pair p = vars.GetVarMemoryAddress(words[1]);
				System.out.println("Addi " + p.getValue0() + ", $zero, " + p.getValue1());
			} else {
				String[] arr = currLine.replaceAll("[; ]", "").split("=");
				String left = arr[0];
				String right = arr[1];
				LinkedList<Operation> operations = new Method().stacking(right, left);	//use Shunting Yard algorithm split expression into several simple operation
				for(Operation op : operations){
					OperationCompile(op);
				}
			}
			currLine = this.theSrc.getNextLine();
		}
	}

	//compile a simple operation to assembly
	private void OperationCompile(Operation op){
		String display = "";
		if (!vars.IsAssigned(op.GetVarname())){
			vars.Assign(op.GetVarname(), this.theRegisterCollection, this.theDataMemory);
		}
		Pair p_result = vars.GetVarMemoryAddress(op.GetVarname());
		if (op.GetOperator() == Operator.Plus){
			if (isInteger(op.GetLeftVar()) && isInteger(op.GetRightVar())){
				String regForTmp = this.theRegisterCollection.getNextAvailableRegisterName();
				display += "addi " + regForTmp + ", $zero, " + op.GetLeftVar() + "\n";
				display += "addi " + regForTmp + ", " + regForTmp + ", " + op.GetRightVar() + "\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
				this.theRegisterCollection.resetRegister(regForTmp);
			} else if (isInteger(op.GetLeftVar())){
				if (!vars.IsAssigned(op.GetRightVar())){
					vars.Assign(op.GetRightVar(), this.theRegisterCollection, this.theDataMemory);
				}
				Pair p_r = vars.GetVarMemoryAddress(op.GetRightVar());
				String regForTmp = this.theRegisterCollection.getNextAvailableRegisterName();
				display += "lw " + regForTmp + ", 0(" + p_r.getValue0() + ")\n";
				display += "addi " + regForTmp + ", " + regForTmp + ", " + op.GetLeftVar() + "\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
				this.theRegisterCollection.resetRegister(regForTmp);
			} else if (isInteger(op.GetRightVar())){
				if (!vars.IsAssigned(op.GetRightVar())){
					vars.Assign(op.GetRightVar(), this.theRegisterCollection, this.theDataMemory);
				}
				Pair p_l = vars.GetVarMemoryAddress(op.GetLeftVar());
				String regForTmp = this.theRegisterCollection.getNextAvailableRegisterName();
				display += "lw " + regForTmp + ", 0(" + p_l.getValue0() + ")\n";
				display += "addi " + regForTmp + ", " + regForTmp + ", " + op.GetRightVar() + "\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
				this.theRegisterCollection.resetRegister(regForTmp);
			} else {
				if (!vars.IsAssigned(op.GetRightVar())){
					vars.Assign(op.GetRightVar(), this.theRegisterCollection, this.theDataMemory);
				}
				Pair p_r = vars.GetVarMemoryAddress(op.GetRightVar());
				if (!vars.IsAssigned(op.GetRightVar())){
					vars.Assign(op.GetRightVar(), this.theRegisterCollection, this.theDataMemory);
				}
				Pair p_l = vars.GetVarMemoryAddress(op.GetLeftVar());
				String regForLeft = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForRight = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForTmp = this.theRegisterCollection.getNextAvailableRegisterName();
				display += "lw " + regForLeft + ", 0(" + p_l.getValue0() + ")\n";
				display += "lw " + regForRight + ", 0(" + p_r.getValue0() + ")\n";
				display += "add " + regForTmp + ", " + regForLeft + ", " + regForRight + "\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
				this.theRegisterCollection.resetRegister(regForLeft);
				this.theRegisterCollection.resetRegister(regForRight);
				this.theRegisterCollection.resetRegister(regForTmp);
			}
		} else if (op.GetOperator() == Operator.Multiply){
			if (isInteger(op.GetLeftVar()) && isInteger(op.GetRightVar())){
				String regForTmp = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForLeft = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForRight = this.theRegisterCollection.getNextAvailableRegisterName();
				display += "addi " + regForLeft + ", $zero, " + op.GetLeftVar() + "\n";
				display += "addi " + regForRight + ", $zero, " + op.GetRightVar() + "\n";
				display += "mul " + regForTmp + ", " + regForLeft + ", " + regForRight + "\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
				this.theRegisterCollection.resetRegister(regForTmp);
				this.theRegisterCollection.resetRegister(regForLeft);
				this.theRegisterCollection.resetRegister(regForRight);
			} else if (isInteger(op.GetLeftVar())){
				if (!vars.IsAssigned(op.GetRightVar())){
					vars.Assign(op.GetRightVar(), this.theRegisterCollection, this.theDataMemory);
				}
				Pair p_r = vars.GetVarMemoryAddress(op.GetRightVar());
				String regForTmp = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForLeft = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForRight = this.theRegisterCollection.getNextAvailableRegisterName();
				display += "lw " + regForRight + ", 0(" + p_r.getValue0() + ")\n";
				display += "addi " + regForLeft + ", $zero, " + op.GetLeftVar() + "\n";
				display += "mul " + regForTmp + ", " + regForLeft + ", " + regForRight + "\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
				this.theRegisterCollection.resetRegister(regForTmp);
				this.theRegisterCollection.resetRegister(regForLeft);
				this.theRegisterCollection.resetRegister(regForRight);
			} else if (isInteger(op.GetRightVar())){
				if (!vars.IsAssigned(op.GetRightVar())){
					vars.Assign(op.GetRightVar(), this.theRegisterCollection, this.theDataMemory);
				}
				Pair p_l = vars.GetVarMemoryAddress(op.GetLeftVar());
				String regForTmp = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForLeft = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForRight = this.theRegisterCollection.getNextAvailableRegisterName();
				display += "lw " + regForLeft + ", 0(" + p_l.getValue0() + ")\n";
				display += "addi " + regForRight + ", $zero, " + op.GetRightVar() + "\n";
				display += "mul " + regForTmp + ", " + regForLeft + ", " + regForRight + "\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
				this.theRegisterCollection.resetRegister(regForTmp);
				this.theRegisterCollection.resetRegister(regForLeft);
				this.theRegisterCollection.resetRegister(regForRight);
			} else {
				if (!vars.IsAssigned(op.GetRightVar())){
					vars.Assign(op.GetRightVar(), this.theRegisterCollection, this.theDataMemory);
				}
				Pair p_r = vars.GetVarMemoryAddress(op.GetRightVar());
				if (!vars.IsAssigned(op.GetRightVar())){
					vars.Assign(op.GetRightVar(), this.theRegisterCollection, this.theDataMemory);
				}
				Pair p_l = vars.GetVarMemoryAddress(op.GetLeftVar());
				String regForLeft = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForRight = this.theRegisterCollection.getNextAvailableRegisterName();
				String regForTmp = this.theRegisterCollection.getNextAvailableRegisterName();
				display += "lw " + regForLeft + ", 0(" + p_l.getValue0() + ")\n";
				display += "lw " + regForRight + ", 0(" + p_r.getValue0() + ")\n";
				display += "mul " + regForTmp + ", " + regForLeft + ", " + regForRight + "\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
				this.theRegisterCollection.resetRegister(regForLeft);
				this.theRegisterCollection.resetRegister(regForRight);
				this.theRegisterCollection.resetRegister(regForTmp);
			}
		} else if (op.GetOperator() == Operator.Assignment){
			String regForTmp = this.theRegisterCollection.getNextAvailableRegisterName();
			if (isInteger(op.GetLeftVar())){
				display += "addi " + regForTmp + ", $zero, " + op.GetLeftVar() + "\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
			} else {
				if (!vars.IsAssigned(op.GetRightVar())){
					vars.Assign(op.GetRightVar(), this.theRegisterCollection, this.theDataMemory);
				}
				Pair p_l = vars.GetVarMemoryAddress(op.GetLeftVar());
				display += "lw " + regForTmp + ", 0(" + p_l.getValue0() + ")\n";
				display += "sw " + regForTmp + ", 0{" + p_result.getValue0() + ")\n";
			}
			this.theRegisterCollection.resetRegister(regForTmp);
		}
		System.out.print(display);
	}

	private static Boolean isInteger(String input){
		String digits = "1234567890";
		if (input.isBlank()){
			return false;
		}
		for(int i = 0; i < input.length(); i++){
			if (digits.indexOf(input.charAt(i)) == -1){
				return false;
			}
		}
		return true;
	}
}
