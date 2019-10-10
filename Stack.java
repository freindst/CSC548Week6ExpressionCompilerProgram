
public class Stack {
	private String value = null;
	private Stack previous = null;
	
	public Stack(){
		
	}

	public Stack(String value) {
		this.value = value;
	}
	
	public String Pop() {
		String rtr = this.value;
		if (this.HasPrevious()) {
			this.value = this.previous.value;
			this.previous = this.previous.previous;
		} else {
			this.value = null;
			this.previous = null;
		}
		return rtr;
	}
	
	public String Peek() {
		return this.value;
	}
	
	public void Push(String value) {		
		if (this.value == null && !this.HasPrevious()) {
			this.value = value;
		} else {
			Stack newStack = new Stack(this.value);
			newStack.previous = this.previous;
			this.value = value;
			this.previous = newStack;
		}		
	}
	
	public Boolean HasPrevious() {
		return this.previous != null;
	}

	public String Display(){
		String returnVal = "";
		Stack curr = this;
		while (curr != null && curr.value != null){
			returnVal += curr.value;
			if (curr.HasPrevious()){
				returnVal += " ";
				curr = curr.previous;
			} else{
				curr = null;
			}
		}
		return returnVal;
	}
}
