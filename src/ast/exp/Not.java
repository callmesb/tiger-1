package ast.exp;

public class Not extends T {
	public T exp;
	public int row;
	public int col;

	public Not(T exp,int row,int col) {
		this.exp = exp;
		this.row = row;
		this.col = col;
	}
	public Not(T exp) {
		this.exp = exp;	
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
