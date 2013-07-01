package ast.exp;

public class Times extends T {
	public T left;
	public T right;
	public int row;
	public int col;

	public Times(T left, T right,int row,int col) {
		this.left = left;
		this.right = right;
		this.row = row;
		this.col = col;
	}
	public Times(T left, T right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
