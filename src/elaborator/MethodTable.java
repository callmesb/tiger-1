package elaborator;

import java.util.Enumeration;

//import util.Todo;

public class MethodTable {
	private java.util.Hashtable<String, ast.type.T> table;
	private java.util.Hashtable<String, Boolean> usetable;	// used check
	public String method;
	public String classs;

//	public java.util.Hashtable<String, ast.type.T> getTable1(){
//		return table;
//	}
	public MethodTable(String method, String classs) {
		this.table = new java.util.Hashtable<String, ast.type.T>();
		this.usetable = new java.util.Hashtable<String, Boolean>();
		this.method = method;
		this.classs = classs;
	}
	
	public void put(ast.dec.Dec dec){
		if(this.table.get(dec.id) != null){
			System.out.println("duplicated parameter: " + dec.id+" (in method("+method+") of class("+classs+")).");
			//System.exit(1);
		}
		this.table.put(dec.id, dec.type);
		this.usetable.put(dec.id, false);
		
	}

	// Duplication is not allowed
	public void put(java.util.LinkedList<ast.dec.T> formals,
			java.util.LinkedList<ast.dec.T> locals,String method,String classs) {
		this.method = method;
		this.classs = classs;
		for (ast.dec.T dec : formals) {
			ast.dec.Dec decc = (ast.dec.Dec) dec;
//			if (this.table.get(decc.id) != null) {
//				System.out.println("duplicated parameter: " + decc.id);
//				System.exit(1);
//			}
//			this.table.put(decc.id, decc.type);
			put(decc);
		}

		for (ast.dec.T dec : locals) {
			ast.dec.Dec decc = (ast.dec.Dec) dec;
//			if (this.table.get(decc.id) != null) {
//				System.out.println("duplicated variable: " + decc.id);
//				System.exit(1);
//			}
//			this.table.put(decc.id, decc.type);
			put(decc);
		}

	}

	// return null for non-existing keys
	public ast.type.T get(String id) {
		if (this.usetable.get(id) != null) {
			this.usetable.remove(id);
			this.usetable.put(id, true);
		}
		return this.table.get(id);
	}
	 public String checkUnUsed(String method_name,String classname) {
		  String str = "";
		  Enumeration<String> en = this.usetable.keys();
	      while(en.hasMoreElements()) {
	    	  String id = (String)en.nextElement();
	    	  if (this.usetable.get(id) == false)
	    		  str += "Warning: variable '" + id +"'"+" is unused"+"(in method("+method_name+")"+" of class("+classname+")).\n";
	      }
	      usetable.clear();
		  return str;
	  }
	public void dump() {
		
		//new Todo();
		System.out.println("MethodTable:");
		System.out.println(this.table);
	}
	public void clear(){
		table.clear();
		
	}

	@Override
	public String toString() {
		return this.table.toString();
	}
//	public ClassTable getTable() {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
