package ast.exp;

public class ArraySelect extends T {
	public T array;
	public T index;
	public int row;
	public int col;

	public ArraySelect(T array, T index,int row,int col) {
		this.array = array;
		this.index = index;
		this.row = row;
		this.col = col;
	}
	public ArraySelect(T array, T index) {
		this.array = array;
		this.index = index;
	}

	@Override
	public void accept(ast.Visitor v) {
		v.visit(this);
		return;
	}
}
