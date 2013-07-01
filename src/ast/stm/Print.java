package ast.stm;

public class Print extends T {
	public ast.exp.T exp;
	public int row;
	public int col;

	public Print(ast.exp.T exp,int row,int col) {
		this.exp = exp;
		this.row = row;
		this.col = col;
	}
	public Print(ast.exp.T exp) {
		this.exp = exp;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
	}
}
