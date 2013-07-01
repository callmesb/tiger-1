package lexer;

import java.io.InputStream;

import util.Todo;

import lexer.Token.Kind;

public class Lexer {
	String fname; // the input file name to be compiled
	InputStream fstream; // input stream for the above file
	int linenum; // current line number
	int tmp_colnum;
	Token tmp_token; // for roll back, sometimes we just check next token but
						// don't use it
	boolean tmp_token_aviable;

	public Lexer(String fname, InputStream fstream) {
		this.fname = fname;
		this.fstream = fstream;
		this.linenum = 1;
		this.tmp_colnum = 0;
		this.tmp_token = null;
		this.tmp_token_aviable = false;
	}

	// When called, return the next token (refer to the code "Token.java")
	// from the input stream.
	// Return TOKEN_EOF when reaching the end of the input stream.
	private Token nextTokenInternal() throws Exception {
		int c = this.fstream.read();
		if (c == '\t')
			this.tmp_colnum += 4;
		else
			this.tmp_colnum += 1;
		if (-1 == c)
			// The value for "lineNum" is now "null",
			// you should modify this to an appropriate
			// line number for the "EOF" token.
			return new Token(Kind.TOKEN_EOF, this.linenum, this.tmp_colnum);

		// skip all kinds of "blanks" and commits
		while (' ' == c || '\t' == c || '\n' == c || '/' == c) {
			if (c == '/') {
				if ((c = this.fstream.read()) == '/') {
					this.tmp_colnum += 1;
					while ((c = this.fstream.read()) != '\n') {
						this.tmp_colnum += 1;
					}
					this.tmp_colnum += 1;
				} else {
					new Todo();
					return null;
				}
			}
			if (c == '\n') {
				this.linenum += 1;
				this.tmp_colnum = 0;
			}
			c = this.fstream.read();
			if (c == '\t')
				this.tmp_colnum += 4;
			else
				this.tmp_colnum += 1;
		}

		if (-1 == c)
			return new Token(Kind.TOKEN_EOF, this.linenum, this.tmp_colnum);

		switch (c) {
		case '+':
			return new Token(Kind.TOKEN_ADD, this.linenum, this.tmp_colnum);
		case '&':
			if (this.fstream.read() == '&') {
				this.tmp_colnum += 1;
				return new Token(Kind.TOKEN_AND, this.linenum,
						this.tmp_colnum - 1);
			} else {
				new Todo();
				return null;
			}
		case '=':
			return new Token(Kind.TOKEN_ASSIGN, this.linenum, this.tmp_colnum);
		case ',':
			return new Token(Kind.TOKEN_COMMER, this.linenum, this.tmp_colnum);
		case '.':
			return new Token(Kind.TOKEN_DOT, this.linenum, this.tmp_colnum);
		case '{':
			return new Token(Kind.TOKEN_LBRACE, this.linenum, this.tmp_colnum);
		case '[':
			return new Token(Kind.TOKEN_LBRACK, this.linenum, this.tmp_colnum);
		case '(':
			return new Token(Kind.TOKEN_LPAREN, this.linenum, this.tmp_colnum);
		case '<':
			return new Token(Kind.TOKEN_LT, this.linenum, this.tmp_colnum);
		case '!':
			return new Token(Kind.TOKEN_NOT, this.linenum, this.tmp_colnum);
		case '}':
			return new Token(Kind.TOKEN_RBRACE, this.linenum, this.tmp_colnum);
		case ']':
			return new Token(Kind.TOKEN_RBRACK, this.linenum, this.tmp_colnum);
		case ')':
			return new Token(Kind.TOKEN_RPAREN, this.linenum, this.tmp_colnum);
		case ';':
			return new Token(Kind.TOKEN_SEMI, this.linenum, this.tmp_colnum);
		case '-':
			return new Token(Kind.TOKEN_SUB, this.linenum, this.tmp_colnum);
		case '*':
			return new Token(Kind.TOKEN_TIMES, this.linenum, this.tmp_colnum);

		default:
			if (c >= 48 && c <= 57) {
				int n = c - 48;
				int colnum = this.tmp_colnum;
				while (true) {
					this.fstream.mark(100);
					c = this.fstream.read();
					this.tmp_colnum += 1;
					if (c >= 48 && c <= 57)
						n = n * 10 + (c - 48);
					else {
						this.fstream.reset();
						this.tmp_colnum -= 1;
						break;
					}
				}
				return new Token(Kind.TOKEN_NUM, this.linenum, colnum,
						Integer.toString(n));
			} else if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || c == 95) {
				String name = String.valueOf((char) c);
				int colnum = this.tmp_colnum;
				while (true) {
					this.fstream.mark(100);
					c = this.fstream.read();
					this.tmp_colnum += 1;
					if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122)
							|| (c >= 48 && c <= 57) || c == 95)
						name += String.valueOf((char) c);
					else {
						this.fstream.reset();
						this.tmp_colnum -= 1;
						break;
					}
				}
				if (name.equals("boolean"))
					return new Token(Kind.TOKEN_BOOLEAN, this.linenum, colnum);
				else if (name.equals("class"))
					return new Token(Kind.TOKEN_CLASS, this.linenum, colnum);
				else if (name.equals("else"))
					return new Token(Kind.TOKEN_ELSE, this.linenum, colnum);
				else if (name.equals("extends"))
					return new Token(Kind.TOKEN_EXTENDS, this.linenum, colnum);
				else if (name.equals("false"))
					return new Token(Kind.TOKEN_FALSE, this.linenum, colnum);
				else if (name.equals("if"))
					return new Token(Kind.TOKEN_IF, this.linenum, colnum);
				else if (name.equals("int"))
					return new Token(Kind.TOKEN_INT, this.linenum, colnum);
				else if (name.equals("length"))
					return new Token(Kind.TOKEN_LENGTH, this.linenum, colnum);
				else if (name.equals("main"))
					return new Token(Kind.TOKEN_MAIN, this.linenum, colnum);
				else if (name.equals("new"))
					return new Token(Kind.TOKEN_NEW, this.linenum, colnum);
				else if (name.equals("out"))
					return new Token(Kind.TOKEN_OUT, this.linenum, colnum);
				else if (name.equals("println"))
					return new Token(Kind.TOKEN_PRINTLN, this.linenum, colnum);
				else if (name.equals("public"))
					return new Token(Kind.TOKEN_PUBLIC, this.linenum, colnum);
				else if (name.equals("return"))
					return new Token(Kind.TOKEN_RETURN, this.linenum, colnum);
				else if (name.equals("static"))
					return new Token(Kind.TOKEN_STATIC, this.linenum, colnum);
				else if (name.equals("String"))
					return new Token(Kind.TOKEN_STRING, this.linenum, colnum);
				else if (name.equals("System"))
					return new Token(Kind.TOKEN_SYSTEM, this.linenum, colnum);
				else if (name.equals("this"))
					return new Token(Kind.TOKEN_THIS, this.linenum, colnum);
				else if (name.equals("true"))
					return new Token(Kind.TOKEN_TRUE, this.linenum, colnum);
				else if (name.equals("void"))
					return new Token(Kind.TOKEN_VOID, this.linenum, colnum);
				else if (name.equals("while"))
					return new Token(Kind.TOKEN_WHILE, this.linenum, colnum);
				else
					return new Token(Kind.TOKEN_ID, this.linenum, colnum, name);
			} else {
				new Todo();
				return null;
			}
		}
	}

	public Token nextToken() {
		Token t = null;

		if (this.tmp_token_aviable) {
			t = this.tmp_token;
			this.tmp_token_aviable = false;
		} else {
			try {
				t = this.nextTokenInternal();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (control.Control.lex)
			System.out.println(t.toString());
		return t;
	}

	public Token checkAToken() {
		Token t = null;

		try {
			t = this.nextTokenInternal();
			this.tmp_token = t;
			this.tmp_token_aviable = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		return t;
	}
}
