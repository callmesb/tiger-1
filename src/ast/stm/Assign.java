package ast.stm;

public class Assign extends T {
	public String id;
	public ast.exp.T exp;
	public ast.type.T type; // type of the id
	public int row;
	public int col;
	public boolean isField;

	public Assign(String id, ast.exp.T exp,int row,int col) {
		this.id = id;
		this.exp = exp;
		this.type = null;
		this.row = row;
		this.col = col;
		this.isField = false;
	}
	public Assign(String id, ast.exp.T exp) {
		this.id = id;
		this.exp = exp;
		this.type = null;
		this.isField = false;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
	}
}
