package ast.exp;

public class NewObject extends T {
	public String id;
	int row;
	int col;

	public NewObject(String id,int row,int col) {
		this.id = id;
		this.row = row;
		this.col = col;
	}
	public NewObject(String id) {
		this.id = id;
		
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
