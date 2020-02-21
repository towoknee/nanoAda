//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// nada.java : Copyright (c) 2004, 2020 Daryl Posnett. All rights reserved.
//
package nada;
import nada.*;
import nada.lexer.*;
import nada.parser.*;
import nada.node.*;
import nada.analysis.*;
import nada.visitors.*;
import java.io.*;
import java.util.*;
public class nada { 
	public static void main(String[] arguments) {
		try {
			new Parser(new Lexer(new PushbackReader(new BufferedReader(new FileReader(arguments[0])), 1024)))
			.parse()
			.apply(new DebugAdapter());
		}
		catch(ParserException e) {
			System.out.println("\nPARSER ERROR: " +e.getMessage());
		}
		catch(LexerException e) {
			System.out.println("\nLEXER ERROR: " +e.getMessage());
		}
		catch(Exception e){
			System.out.println("\nGENERAL ERROR: " +e.getMessage() + "\n\n");
			e.printStackTrace(System.out);
		}
	}
}