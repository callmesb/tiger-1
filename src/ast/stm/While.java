package ast.stm;

public class While extends T {
	public ast.exp.T condition;
	public T body;
	public int row;
	public int col;

	public While(ast.exp.T condition, T body,int row,int col) {
		this.condition = condition;
		this.body = body;
		this.row = row;
		this.col = col;
	}
	public While(ast.exp.T condition, T body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
	}
}
