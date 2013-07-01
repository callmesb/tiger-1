package ast.exp;

public class NewIntArray extends T {
	public T exp;
	int row;
	int col;

	public NewIntArray(T exp,int row,int col) {
		this.exp = exp;
		this.row = row;
		this.col = col;
	}
	public NewIntArray(T exp) {
		this.exp = exp;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
