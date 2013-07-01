package ast.exp;

public class Length extends T {
	public T array;
	public int row;
	public int col;

	public Length(T array,int row,int col) {
		this.array = array;
		this.row = row;
		this.col = col;
	}
	public Length(T array) {
		this.array = array;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
