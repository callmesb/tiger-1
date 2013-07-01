package ast.stm;

public class Block extends T {
	public java.util.LinkedList<T> stms;
	public int row;
	public int col;

	public Block(java.util.LinkedList<T> stms,int row,int col) {
		this.stms = stms;
		this.row = row;
		this.col = col;
	}
	public Block(java.util.LinkedList<T> stms) {
		this.stms = stms;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
	}
}
