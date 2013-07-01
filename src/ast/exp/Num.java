package ast.exp;

public class Num extends T {
	public int num;
	int row ;
	int col;
	public Num(int num,int row,int col) {
		this.num = num;
		this.row = row;
		this.col = col;
	}
	public Num(int num) {
		this.num = num;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
