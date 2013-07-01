package parser;

import ast.stm.T;
import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

public class Parser {
	Lexer lexer;
	Token current;
	String fname;
	int flag = 0;

	public Parser(String fname, java.io.InputStream fstream) {
		this.fname = fname;
		lexer = new Lexer(fname, fstream);
		current = lexer.nextToken();
	}

	// /////////////////////////////////////////////
	// utility methods to connect the lexer
	// and the parser.

	private void advance() {
		current = lexer.nextToken();
	}

	private void eatToken(Kind kind) {
		if (kind == current.kind)
			advance();
		else {
			System.out.println(this.fname + ". Error in " + "row "+current.lineNum + ",column "
					+ current.colNum + ". Requires '"
					+ kind.toString() + "'" + " ('" + current.kind.toString()
					+ "' invalid).");
			flag = 1;
			// skip this error, go on to the end.
			advance();
		}
	}

	private void error() {
		System.out.println(this.fname + ":" + current.lineNum + ":"
				+ current.colNum + " : error : invalid '"
				+ current.kind.toString() + "'");
		// System.exit(1);
		// skip this error, go on to the end.
		advance();
		return;
	}

	// ////////////////////////////////////////////////////////////
	// below are method for parsing.

	// A bunch of parsing methods to parse expressions. The messy
	// parts are to deal with precedence and associativity.

	// ExpList -> Exp ExpRest*
	// ->
	// ExpRest -> , Exp
	private java.util.LinkedList<ast.exp.T> parseExpList() {
		java.util.LinkedList<ast.exp.T> ExpList = new java.util.LinkedList<ast.exp.T>();
		if (current.kind == Kind.TOKEN_RPAREN)
			return ExpList;
		ast.exp.T exp0 = parseExp();
		ExpList.add(exp0);
		while (current.kind == Kind.TOKEN_COMMER) {
			advance();
			ast.exp.T exp = parseExp();
			if(exp != null)
			   ExpList.add(exp);
		}
		return ExpList;
	}

	// AtomExp -> (exp)
	// -> INTEGER_LITERAL
	// -> true
	// -> false
	// -> this
	// -> id
	// -> new int [exp]
	// -> new id ()
	private ast.exp.T parseAtomExp() {
		int row = current.lineNum;
		int col = current.colNum;
		switch (current.kind) {
		case TOKEN_LPAREN:
			advance();
			col = current.colNum;
			ast.exp.T exp = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			return exp;
		case TOKEN_NUM:
			int num = Integer.parseInt(current.lexeme);
		//	System.out.println(num);
			col = current.colNum;
			advance();
			return new ast.exp.Num(num,row,col);
		case TOKEN_TRUE:
			advance();
			return new ast.exp.True();
		case TOKEN_FALSE:
			advance();
			return new ast.exp.False();
		case TOKEN_THIS:
			advance();
			return new ast.exp.This();
		case TOKEN_ID:
			String id = current.lexeme;
			col = current.colNum;
			advance();
			return new ast.exp.Id(id,row,col); // here maybe to modify
		case TOKEN_NEW: {
			advance();
			switch (current.kind) {
			case TOKEN_INT:
				advance();
				eatToken(Kind.TOKEN_LBRACK);
				col = current.colNum;
				ast.exp.T exp1 = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.exp.NewIntArray(exp1,row,col);
			case TOKEN_ID:
				col = current.colNum;
				String id1 = current.lexeme;
				advance();
				eatToken(Kind.TOKEN_LPAREN);
				eatToken(Kind.TOKEN_RPAREN);
				return new ast.exp.NewObject(id1,row,col);
			default:
				error();
				return null;
			}
		}
		default:
			error();
			return null;
		}
	}

	// NotExp -> AtomExp
	// -> AtomExp .id (expList)
	// -> AtomExp [exp]
	// -> AtomExp .length
	private ast.exp.T parseNotExp() {
		int row = current.lineNum;
		int col = current.colNum;
		ast.exp.T exp = parseAtomExp();
		while (current.kind == Kind.TOKEN_DOT
				|| current.kind == Kind.TOKEN_LBRACK) {
			if (current.kind == Kind.TOKEN_DOT) {
				advance();
				if (current.kind == Kind.TOKEN_LENGTH) {
					col = current.colNum;
					advance();
					return new ast.exp.Length(exp,row,col);
				}

				String id = current.lexeme;
				eatToken(Kind.TOKEN_ID);
				eatToken(Kind.TOKEN_LPAREN);
				col = current.colNum;
				java.util.LinkedList<ast.exp.T> agrs = parseExpList();
				eatToken(Kind.TOKEN_RPAREN);

				return new ast.exp.Call(exp, id, agrs,row,col);
			} else {
				advance();
				col = current.colNum;
				ast.exp.T index = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.exp.ArraySelect(exp, index,row,col);
			}
		}
		return exp;
	}

	// TimesExp -> ! TimesExp
	// -> NotExp
	private ast.exp.T parseTimesExp() {
		int flag = 0;
		int row = current.lineNum;
		int col = current.colNum;
		while (current.kind == Kind.TOKEN_NOT) {
			flag = 1;
			advance();
		}
		col = current.colNum;
		ast.exp.T exp = parseNotExp();
		if (1 == flag)
			return new ast.exp.Not(exp,row,col);
		else
			return exp;

	}

	// AddSubExp -> TimesExp * TimesExp
	// -> TimesExp
	private ast.exp.T parseAddSubExp() {
		int row = current.lineNum;
		int col = current.colNum;
		ast.exp.T left = parseTimesExp();	
		while (current.kind == Kind.TOKEN_TIMES) {
			advance();
			col = current.colNum;
			ast.exp.T right = parseTimesExp();
			return new ast.exp.Times(left, right,row,col);
		}
		return left;
	}

	// LtExp -> AddSubExp + AddSubExp
	// -> AddSubExp - AddSubExp
	// -> AddSubExp
	private ast.exp.T parseLtExp() {
		int row = current.lineNum;
		int col = current.colNum;
		ast.exp.T left = parseAddSubExp();
		while (current.kind == Kind.TOKEN_ADD || current.kind == Kind.TOKEN_SUB) {
			if (current.kind == Kind.TOKEN_ADD) {
				
				advance();
				col = current.colNum;
				ast.exp.T right = parseAddSubExp();
				return new ast.exp.Add(left, right,row,col);
			} else {
				advance();
				col = current.colNum;
				ast.exp.T right = parseAddSubExp();
				return new ast.exp.Sub(left, right,row,col);
			}
		}
		return left;
	}

	// AndExp -> LtExp < LtExp
	// -> LtExp
	private ast.exp.T parseAndExp() {
		int row = current.lineNum;
		int col = current.colNum;
		ast.exp.T left = parseLtExp();
		while (current.kind == Kind.TOKEN_LT) {		
			advance();
			col = current.colNum;
			ast.exp.T right = parseLtExp();
			return new ast.exp.Lt(left, right,row,col);
		}
		return left;

	}

	// Exp -> AndExp && AndExp
	// -> AndExp
	private ast.exp.T parseExp() {
		int row = current.lineNum;
		int col = current.colNum;
		ast.exp.T left = parseAndExp();
		while (current.kind == Kind.TOKEN_AND) {
			advance();
			col = current.colNum;
			ast.exp.T right = parseAndExp();
			return new ast.exp.And(left, right,row,col);
		}
		return left;

	}

	// Statement -> { Statement* }
	// -> if ( Exp ) Statement else Statement
	// -> while ( Exp ) Statement
	// -> System.out.println ( Exp ) ;
	// -> id = Exp ;
	// -> id [ Exp ]= Exp ;
	private ast.stm.T parseStatement() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a statement.
		int row = current.lineNum;
		int col = current.colNum;
		switch (current.kind) {
		case TOKEN_LBRACE:
			advance();
			// parseStatement();
			break;
		case TOKEN_IF:
			advance();
			eatToken(Kind.TOKEN_LPAREN);

			// if's condition
		//	col = current.colNum;
			ast.exp.T condition_if = parseExp();
			eatToken(Kind.TOKEN_RPAREN);

			// if's body
			ast.stm.T thenn;
			if (current.kind == Kind.TOKEN_LBRACE) {
				thenn = parseStatements();
				eatToken(Kind.TOKEN_RBRACE);
			} else
				thenn = parseStatement();

			// else body
			ast.stm.T elsee;
			eatToken(Kind.TOKEN_ELSE);
			if (current.kind == Kind.TOKEN_LBRACE) {
				elsee = parseStatements();
				eatToken(Kind.TOKEN_RBRACE);
			} else
				elsee = parseStatement();

			return new ast.stm.If(condition_if, thenn, elsee,row,col);

			// break;
		case TOKEN_WHILE:
			advance();
			eatToken(Kind.TOKEN_LPAREN);

			// while's condition
		//	col = current.colNum;
			ast.exp.T condition_while = parseExp();
			eatToken(Kind.TOKEN_RPAREN);

			// while's body
			ast.stm.T body_while;
			if (current.kind == Kind.TOKEN_LBRACE) {
				body_while = (ast.stm.Block)parseStatements();
				eatToken(Kind.TOKEN_RBRACE);
			} else
				body_while = parseStatement();

			return new ast.stm.While(condition_while, body_while,row,col);
			// break;
		case TOKEN_SYSTEM:
			advance();
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_OUT);
			eatToken(Kind.TOKEN_DOT);
			eatToken(Kind.TOKEN_PRINTLN);
			eatToken(Kind.TOKEN_LPAREN);

			// the print body
		//	col = current.colNum;
			ast.exp.T print_exp = parseExp();
			eatToken(Kind.TOKEN_RPAREN);
			eatToken(Kind.TOKEN_SEMI);

			return new ast.stm.Print(print_exp,row,col);
			// break;
		case TOKEN_ID:
			int flag = 0; // if assign or assignarray
			String id = current.lexeme;
			advance();
			ast.exp.T index = null;
			if (current.kind == Kind.TOKEN_LBRACK) {
				flag = 1;
				advance();
				index = parseExp();
				eatToken(Kind.TOKEN_RBRACK);
			}
			eatToken(Kind.TOKEN_ASSIGN);
		//	col = current.colNum;
			ast.exp.T exp = parseExp();
			eatToken(Kind.TOKEN_SEMI);
			if (0 == flag)
				return new ast.stm.Assign(id, exp,row,col);
			else
				return new ast.stm.AssignArray(id, index, exp,row,col);
			// break;
		default:
			error();
			return null;
		}
		return null;

	}

	// Statements -> Statement Statements
	// ->
	private ast.stm.Block parseStatements() {
		java.util.LinkedList<T> stms = new java.util.LinkedList<T>();// the
		int row = current.lineNum;
		int col = current.colNum;																// statements
		while (current.kind == Kind.TOKEN_LBRACE
				|| current.kind == Kind.TOKEN_IF
				|| current.kind == Kind.TOKEN_WHILE
				|| current.kind == Kind.TOKEN_SYSTEM
				|| current.kind == Kind.TOKEN_ID) {
			ast.stm.T stm = parseStatement();// a stm
			if(stm != null)
			    stms.add(stm);// add a stm
		}
		return new ast.stm.Block(stms,row,col);
	}

	// Type -> int []
	// -> boolean
	// -> int
	// -> id
	private ast.type.T parseType() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a type.
		switch (current.kind) {
		case TOKEN_INT:
			advance();
			if (current.kind == Kind.TOKEN_LBRACK) {
				advance();
				eatToken(Kind.TOKEN_RBRACK);
				return new ast.type.IntArray();
			}
			return new ast.type.Int();
			// break;
		case TOKEN_BOOLEAN:
			advance();
			return new ast.type.Boolean();
			// break;
		case TOKEN_ID:
			String id = current.lexeme;
			advance();
			return new ast.type.Class(id);
			// break;
		default:
			error();
			return null;
		}
	}

	// VarDecl -> Type id ;
	private ast.dec.T parseVarDecl() {
		// to parse the "Type" nonterminal in this method, instead of writing
		// a fresh one.
		ast.type.T type = parseType();
		String id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_SEMI);
		return new ast.dec.Dec(type, id);
	}

	// VarDecls -> VarDecl VarDecls
	// ->
	private java.util.LinkedList<ast.dec.T> parseVarDecls() {
		java.util.LinkedList<ast.dec.T> decs = new java.util.LinkedList<ast.dec.T>();
		while (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			// if ID is not a custom class
			//String id_class = current.lexeme;
			if (current.kind == Kind.TOKEN_ID
					&& lexer.checkAToken().kind != Kind.TOKEN_ID) {
				//ast.type.Class type_class = new ast.type.Class(current.lexeme);
				//ast.dec.T dec = new ast.dec.Dec(type_class, id_class);
				//decs.add(dec);
			    return decs;
			}
			ast.dec.T dec = parseVarDecl();
			decs.add(dec);
		}
		return decs;
	}

	// FormalList -> Type id FormalRest*
	// ->
	// FormalRest -> , Type id
	private java.util.LinkedList<ast.dec.T> parseFormalList() {
		java.util.LinkedList<ast.dec.T> decs = new java.util.LinkedList<ast.dec.T>();
		if (current.kind == Kind.TOKEN_INT
				|| current.kind == Kind.TOKEN_BOOLEAN
				|| current.kind == Kind.TOKEN_ID) {
			ast.type.T type = parseType();
			String id = current.lexeme;
			ast.dec.T dec = new ast.dec.Dec(type, id);
			decs.add(dec);
			eatToken(Kind.TOKEN_ID);
			while (current.kind == Kind.TOKEN_COMMER) {
				advance();
				ast.type.T type1 = parseType();
				String id1 = current.lexeme;
				ast.dec.T dec1 = new ast.dec.Dec(type1, id1);
				decs.add(dec1);
				eatToken(Kind.TOKEN_ID);
			}
		}
		return decs;
	}

	// Method -> public Type id ( FormalList )
	// { VarDecl* Statement* return Exp ;}
	private ast.method.T parseMethod() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a method.
		eatToken(Kind.TOKEN_PUBLIC);
		ast.type.T type_return = parseType();
		String id = current.lexeme;
		eatToken(Kind.TOKEN_ID);
		eatToken(Kind.TOKEN_LPAREN);
		java.util.LinkedList<ast.dec.T> formas = parseFormalList();
		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);
		java.util.LinkedList<ast.dec.T> vars = parseVarDecls();
		ast.stm.Block stms = parseStatements();
		eatToken(Kind.TOKEN_RETURN);
		ast.exp.T exp = parseExp();
		eatToken(Kind.TOKEN_SEMI);
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.method.Method(type_return, id, formas, vars, stms.stms,
				exp);
	}

	// MethodDecls -> MethodDecl MethodDecls
	// ->
	private java.util.LinkedList<ast.method.T> parseMethodDecls() {
		java.util.LinkedList<ast.method.T> methods = new java.util.LinkedList<ast.method.T>();
		while (current.kind == Kind.TOKEN_PUBLIC) {
			ast.method.T method = parseMethod();
			methods.add(method);
		}
		return methods;
	}

	// ClassDecl -> class id { VarDecl* MethodDecl* }
	// -> class id extends id { VarDecl* MethodDecl* }
	private ast.classs.T parseClassDecl() {
		eatToken(Kind.TOKEN_CLASS);

		String name_class = current.lexeme;
		// String extendss = null;
		String extends_class = null;
		eatToken(Kind.TOKEN_ID);
		if (current.kind == Kind.TOKEN_EXTENDS) {
			// extendss = "extends";
			eatToken(Kind.TOKEN_EXTENDS);
			extends_class = current.lexeme;
			eatToken(Kind.TOKEN_ID);
		}
		eatToken(Kind.TOKEN_LBRACE);

		java.util.LinkedList<ast.dec.T> varDecls = parseVarDecls();
		java.util.LinkedList<ast.method.T> methods = parseMethodDecls();
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.classs.Class(name_class, extends_class, varDecls,
				methods);
	}

	// ClassDecls -> ClassDecl ClassDecls
	// ->
	private java.util.LinkedList<ast.classs.T> parseClassDecls() {
		java.util.LinkedList<ast.classs.T> classlist = new java.util.LinkedList<ast.classs.T>();
		while (current.kind == Kind.TOKEN_CLASS) {
			ast.classs.T classs = parseClassDecl();
			classlist.add(classs);
		}
		return classlist;
	}

	// MainClass -> class id
	// {
	// public static void main ( String [] id )
	// {
	// Statement
	// }
	// }
	private ast.mainClass.MainClass parseMainClass() {
		// Lab1. Exercise 4: Fill in the missing code
		// to parse a main class as described by the
		// grammar above.
		eatToken(Kind.TOKEN_CLASS);

		String id = current.lexeme; // the class id
		eatToken(Kind.TOKEN_ID);

		eatToken(Kind.TOKEN_LBRACE);
		eatToken(Kind.TOKEN_PUBLIC);
		eatToken(Kind.TOKEN_STATIC);
		eatToken(Kind.TOKEN_VOID);
		eatToken(Kind.TOKEN_MAIN);
		eatToken(Kind.TOKEN_LPAREN);
		eatToken(Kind.TOKEN_STRING);
		eatToken(Kind.TOKEN_LBRACK);
		eatToken(Kind.TOKEN_RBRACK);

		String args = current.lexeme; // the args
		eatToken(Kind.TOKEN_ID);

		eatToken(Kind.TOKEN_RPAREN);
		eatToken(Kind.TOKEN_LBRACE);

		ast.stm.Block stms = parseStatements();// the stm

		eatToken(Kind.TOKEN_RBRACE);
		eatToken(Kind.TOKEN_RBRACE);
		return new ast.mainClass.MainClass(id, args, stms);
	}

	// Program -> MainClass ClassDecl*
	private ast.program.Program parseProgram() {

		ast.mainClass.MainClass mainClass = parseMainClass();
		java.util.LinkedList<ast.classs.T> classes = parseClassDecls();
		eatToken(Kind.TOKEN_EOF);
		return new ast.program.Program(mainClass, classes);
	}

	public ast.program.T parse() {
		// ast.program.T prog = null;
		System.out.println("Start parsing......");
		ast.program.T prog = parseProgram();
		if (0 == flag)
			System.out.println("End parsing,no parsing errors.");
		else {
			System.out.println("End parsing,errors list above.");
			return null;
		}
		return prog;
	}
}
