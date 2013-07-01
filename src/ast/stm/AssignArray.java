package ast.stm;

public class AssignArray extends T {
	public String id;
	public ast.exp.T index;
	public ast.exp.T exp;
	public int row;
	public int col;

	public AssignArray(String id, ast.exp.T index, ast.exp.T exp,int row,int col) {
		this.id = id;
		this.index = index;
		this.exp = exp;
		this.row = row;
		this.col = col;
	}
	public AssignArray(String id, ast.exp.T index, ast.exp.T exp) {
		this.id = id;
		this.index = index;
		this.exp = exp;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
	}
}
