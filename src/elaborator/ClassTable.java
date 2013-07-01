package elaborator;

import java.util.Enumeration;

//import util.Todo;

public class ClassTable {
	// map each class name (a string), to the class bindings.
	private java.util.Hashtable<String, ClassBinding> table;
	public  java.util.Hashtable<String, java.util.Hashtable<String, Boolean>> usetable_class;	// used check
    
	public ClassTable() {
		this.table = new java.util.Hashtable<String, ClassBinding>();
		this.usetable_class = new java.util.Hashtable<String, java.util.Hashtable<String, Boolean>>();
	}
//	public ClassBinding getClassBinding(String key){
//		return table.get(key);
//	}

	// Duplication is not allowed
	//Put a class into the table
	public void put(String c, ClassBinding cb) {
		if (this.table.get(c) != null) {
			System.out.println("duplicated class: " + c);
			System.exit(1);
		}
		this.table.put(c, cb);
		this.usetable_class.put(c, cb.usetable);
	}
	public String checkUnUsed(java.util.Hashtable<String, Boolean> usetable, String class_name) {
		  String str = "";
		  Enumeration<String> en = usetable.keys();
	      while(en.hasMoreElements()) {
	    	  String id = (String)en.nextElement();
	    	  if (usetable.get(id) == false)
	    		  str += "Warning: variable '" + id +"'"+" is unsed"+" (in class("+class_name+")).\n";
	      }
	      usetable.clear();
		  return str;
	  }
	// put a field into this table
	// Duplication is not allowed
	public void put(String c, String id, ast.type.T type) {
		ClassBinding cb = this.table.get(c);
		cb.put(id, type);
		return;
	}

	// put a method into this table
	// Duplication is not allowed.
	// Also note that MiniJava does NOT allow overloading.
	public void put(String c, String id, MethodType type) {
		ClassBinding cb = this.table.get(c);
		cb.put(id, type);
		return;
	}

	// return null for non-existing class
	public ClassBinding get(String className) {
		return this.table.get(className);
	}

	// get type of some field
	// return null for non-existing field.
	public ast.type.T get(String className, String xid) {
		ClassBinding cb = this.table.get(className);
		ast.type.T type = cb.fields.get(xid);
		this.usetable_class.get(className).remove(xid);
		this.usetable_class.get(className).put(xid, true);
//		cb.usetable.remove(xid);
//		cb.usetable.put(xid, true);
		while (type == null) { // search all parent classes until found or fail
			if (cb.extendss == null){
				//this.usetable_class.get(className).remove(xid);
				//this.usetable_class.get(className).put(xid, true);
				return type;
			}

			cb = this.table.get(cb.extendss);
			type = cb.fields.get(xid);
			this.usetable_class.get(className).remove(xid);
			this.usetable_class.get(className).put(xid, true);
		}
		return type;
	}

	// get type of some method
	// return null for non-existing method
	public MethodType getm(String className, String mid) {
		ClassBinding cb = this.table.get(className);
		if(cb == null){
			return null;
		}
		MethodType type = cb.methods.get(mid);
		while (type == null) { // search all parent classes until found or fail
			if (cb.extendss == null)
				return type;

			cb = this.table.get(cb.extendss);
			type = cb.methods.get(mid);
		}
		return type;
	}

	public void dump( ) {
		 System.out.println("ClassTable:");
		  Enumeration<String> en = this.table.keys();
	      while(en.hasMoreElements()) {
	    	  String name = (String)en.nextElement();
	    	  ClassBinding cb = this.get(name);
	    	  System.out.println("class - " + name + ":");
	    	  System.out.println(cb);
	      }
	}
	@Override
	public String toString() {
		return this.table.toString();
	}
}
