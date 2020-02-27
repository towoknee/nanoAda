/**
Overview of Testing 

Fails: 
1. test1Fail.ada - procedures that are not nested fails - basicDeclaration calls subprogramBody
2. test2Fail.ada - tests blank ada file for failure - expects procedure 
3. test3Fail.ada - trying to use an identifier that has the same name as a token
4. test4Fail.ada - typeDefinition for identifier list is not valid
5. test5Fail.ada - testing multiple parentheses in enumerationTypeDefinition
   - syntactically is wrong in our tinyADA but should work in practice 

Passes:
1. test1Pass.ada - accepts any token, regardless of whether or not its been instantiated
   - Fails in role analysis because J is undefined (in sequenceOfStatements)
   - PASS ON PART 1 BUT FAIL FOR PART 3 PARSER
2. test2Pass.ada - changed line 19 of prog2.ada to set I,J as booleans instead of integers for the matrix
   - Matrix will always update with indexes that are type boolean, which in a practical sense is wrong, but 
   - syntatically it is still correct in our parser(including roles)
3. test3Pass.ada - testing the optional end identifier
   - works syntactically but not semantically
   - could use any identifier name after an end statement for part 1
   - PASS ON PART 1 BUT FAIL FOR PART 3 PARSER
4. test4Pass.ada - testing identifierList, unused identifiers, and nested loops
5. test5Pass.ada - nested procedures work 
6. test6Pass.ada - tested sequence of statements 

**/

import java.util.*;

public class Parser extends Object{
   private Chario chario;
   private Scanner scanner;
   private Token token;
   private SymbolTable table;
   public Boolean scopeBool;
   public Boolean roleBool;

   private Set<Integer> addingOperator,
                        multiplyingOperator,
                        relationalOperator,
                        basicDeclarationHandles,
                        statementHandles,
                        leftNames,                        // Sets of roles for names (see below)
                        rightNames;

   public Parser(Chario c, Scanner s, Boolean scope, Boolean role){
      chario = c;
      scanner = s;
      scopeBool = scope;
      roleBool = role;
      initHandles();
      initTable();
      token = scanner.nextToken();
   }

   public void reset(){
      scanner.reset();
      initTable();
      token = scanner.nextToken();
   }

   private void initHandles(){
      addingOperator = new HashSet<Integer>();
      addingOperator.add(Token.PLUS);
      addingOperator.add(Token.MINUS);
      multiplyingOperator = new HashSet<Integer>();
      multiplyingOperator.add(Token.MUL);
      multiplyingOperator.add(Token.DIV);
      multiplyingOperator.add(Token.MOD);
      relationalOperator = new HashSet<Integer>();
      relationalOperator.add(Token.EQ);
      relationalOperator.add(Token.NE);
      relationalOperator.add(Token.LE);
      relationalOperator.add(Token.GE);
      relationalOperator.add(Token.LT);
      relationalOperator.add(Token.GT);
      basicDeclarationHandles = new HashSet<Integer>();
      basicDeclarationHandles.add(Token.TYPE);
      basicDeclarationHandles.add(Token.ID);
      basicDeclarationHandles.add(Token.PROC);
      statementHandles = new HashSet<Integer>();
      statementHandles.add(Token.EXIT);
      statementHandles.add(Token.ID);
      statementHandles.add(Token.IF);
      statementHandles.add(Token.LOOP);
      statementHandles.add(Token.NULL);
      statementHandles.add(Token.WHILE);
      leftNames = new HashSet<Integer>();                 // Name roles for targets of assignment statement
      leftNames.add(SymbolEntry.PARAM);
      leftNames.add(SymbolEntry.VAR);
      rightNames = new HashSet<Integer>(leftNames);       // Name roles for names in expressions
      rightNames.add(SymbolEntry.CONST);
   }

   /*
   Two new routines for role analysis.
   */

   private void acceptRole(SymbolEntry s, int expected, String errorMessage){
      if (s.role != SymbolEntry.NONE && s.role != expected && roleBool)
      {
         System.out.println("ERROR CAME FROM ACCEPT");
         chario.putError(errorMessage);
      }
   }

   private void acceptRole(SymbolEntry s, Set<Integer> expected, String errorMessage){
      if (s.role != SymbolEntry.NONE && ! (expected.contains(s.role)) && roleBool)
      {
         System.out.println("ERROR CAME FROM ACCEPT");
         chario.putError(errorMessage);
      }
   }

   private void accept(int expected, String errorMessage){
      if (token.code != expected)
         fatalError(errorMessage);
      token = scanner.nextToken();
   }

   private void fatalError(String errorMessage){
      chario.putError(errorMessage);
      throw new RuntimeException("Fatal error");
   }

   /*
   Three new routines for scope analysis.
   */

   private void initTable(){
      table = new SymbolTable(chario, scopeBool, roleBool);
      table.enterScope();
      SymbolEntry entry = table.enterSymbol("BOOLEAN");
      entry.setRole(SymbolEntry.TYPE);
      entry = table.enterSymbol("CHAR");
      entry.setRole(SymbolEntry.TYPE);
      entry = table.enterSymbol("INTEGER");
      entry.setRole(SymbolEntry.TYPE);
      entry = table.enterSymbol("TRUE");
      entry.setRole(SymbolEntry.CONST);
      entry = table.enterSymbol("FALSE");
      entry.setRole(SymbolEntry.CONST);
   }      

   private SymbolEntry enterId(){
      SymbolEntry entry = null;
      if (token.code == Token.ID)
         entry = table.enterSymbol(token.string);
      else
         fatalError("identifier expected enter");
      token = scanner.nextToken();
      return entry;
   }

   private SymbolEntry findId(){
      SymbolEntry entry = null;
      if (token.code == Token.ID)
         entry = table.findSymbol(token.string);
      else
      {
         fatalError("identifier expected find");
      }
      token = scanner.nextToken();
      return entry;
   }

   public void parse(){
      subprogramBody();
      accept(Token.EOF, "extra symbols after logical end of program");
      table.exitScope();
   }

   /*
   subprogramBody =
         subprogramSpecification "is"
         declarativePart
         "begin" sequenceOfStatements
         "end" [ <procedure>identifier ] ";"
   */
   private void subprogramBody(){
      subprogramSpecification();
      accept(Token.IS, "'is' expected");
      declarativePart();
      accept(Token.BEGIN, "'begin' expected");
      sequenceOfStatements();
      accept(Token.END, "'end' expected");
      table.exitScope();
      if (token.code == Token.ID){
         SymbolEntry entry = findId();
         acceptRole(entry, SymbolEntry.PROC, "must be a PROC identifier"); // ***** 
      }
      accept(Token.SEMI, "semicolon expected");
   }

   /*
   subprogramSpecification = "procedure" identifier [ formalPart ]
   */
   private void subprogramSpecification(){
      accept(Token.PROC, "'procedure' expected");
      SymbolEntry entry = enterId();
      entry.setRole(SymbolEntry.PROC); // *********
      table.enterScope();
      if (token.code == Token.L_PAR)
         formalPart();
   }

   /* ***********
   formalPart = "(" parameterSpecification { ";" parameterSpecification } ")"
   */

   private void formalPart(){
      accept(Token.L_PAR, "'(' expected");
      parameterSpecification();
      while (token.code == Token.SEMI){
            token = scanner.nextToken();
            parameterSpecification();
      }
      accept(Token.R_PAR, "')' expected");
   }



   /*
   parameterSpecification = identifierList ":" mode <type>identifier
   */
   private void parameterSpecification(){
      SymbolEntry list = identifierList();
      list.setRole(SymbolEntry.PARAM); // ******** 
      accept(Token.COLON, "':' expected");
      if (token.code == Token.IN)
         token = scanner.nextToken();
      if (token.code == Token.OUT)
         token = scanner.nextToken();
      SymbolEntry entry = findId();
      acceptRole(entry, SymbolEntry.TYPE, "must be a TYPE identifier"); // ********

   }

   /*
   declarativePart = { basicDeclaration }
   */
   private void declarativePart(){
      while (basicDeclarationHandles.contains(token.code))
         basicDeclaration();
   }

   /*
   basicDeclaration = objectDeclaration | numberDeclaration
                    | typeDeclaration | subprogramBody   
   */
   private void basicDeclaration(){
      switch (token.code){
         case Token.ID:
            numberOrObjectDeclaration();
            break;
         case Token.TYPE:
            typeDeclaration();
            break;
         case Token.PROC:
            subprogramBody();
            break;
         default: fatalError("error in declaration part");
      }
   }

   /*
   objectDeclaration =
         identifierList ":" typeDefinition ";"

   numberDeclaration =
         identifierList ":" "constant" ":=" <static>expression ";"
   */
   private void numberOrObjectDeclaration(){
      SymbolEntry list = identifierList(); 
      accept(Token.COLON, "':' expected");
      if (token.code == Token.CONST){
         list.setRole(SymbolEntry.CONST);
         token = scanner.nextToken();
         accept(Token.GETS, "':=' expected");
         expression();
      }
      else{
         list.setRole(SymbolEntry.VAR);
         typeDefinition();
      }
      accept(Token.SEMI, "semicolon expected");
   }

   /*
   typeDeclaration = "type" identifier "is" typeDefinition ";"
   */
   private void typeDeclaration(){
      accept(Token.TYPE, "'type' expected");
      SymbolEntry entry = enterId();
      entry.setRole(SymbolEntry.TYPE);
      accept(Token.IS, "'is' expected");
      typeDefinition();
      accept(Token.SEMI, "semicolon expected");
   }

   /* *************
   typeDefinition = enumerationTypeDefinition | arrayTypeDefinition
                  | range | <type>identifier
   */

   private void typeDefinition(){
      switch(token.code)
      {
         case Token.L_PAR:
            enumerationTypeDefinition();
            break;
         case Token.ARRAY:
            arrayTypeDefinition();
            break;
         case Token.RANGE:
            range();
            break;
         case Token.ID:
            SymbolEntry entry = findId();
            acceptRole(entry, SymbolEntry.TYPE, "must be a TYPE identifier");
            break;
         default: fatalError("error in statement");
      }
   }

   /*
   enumerationTypeDefinition = "(" identifierList ")"
   */
   private void enumerationTypeDefinition(){
      accept(Token.L_PAR, "'(' expected");
      SymbolEntry list = identifierList();
      list.setRole(SymbolEntry.CONST);
      accept(Token.R_PAR, "')' expected");
   }

   /*
   arrayTypeDefinition = "array" "(" index ")" "of" <type>identifier
   */
   private void arrayTypeDefinition(){
      accept(Token.ARRAY, "'array' expected");
      accept(Token.L_PAR, "'(' expected");
      index();

      while (token.code == Token.COMMA)
      {
         token = scanner.nextToken();
         index();
      }

      accept(Token.R_PAR, "')' expected");
      accept(Token.OF, "'of' expected");
      SymbolEntry entry = findId();
      acceptRole(entry, SymbolEntry.TYPE, "must be a TYPE identifier");
      
   }

   /*
   index = range | <type>identifier
   */
   private void index(){
      if (token.code == Token.RANGE)
         range();
      else if (token.code == Token.ID){
         SymbolEntry entry = findId();
         acceptRole(entry, SymbolEntry.TYPE, "type name expected");
      }
      else
         fatalError("error in index type");
   }

   /*
   range = "range " simpleExpression ".." simpleExpression
   */
   private void range(){
      accept(Token.RANGE, "'range' expected");
      simpleExpression();
      accept(Token.THRU, "'..' expected");
      simpleExpression();
   }

   /*
   identifier { "," identifer } *********
   */

   // identifierList = identifier { "," identifier }
   private SymbolEntry identifierList(){
      SymbolEntry list = enterId();
      while (token.code == Token.COMMA){
         token = scanner.nextToken();
         list.append(enterId());
      }
      return list;
   }

   /*
   sequenceOfStatements = statement { statement }
   */
   private void sequenceOfStatements(){
      statement();
      while (statementHandles.contains(token.code))
         statement();
   }

   /* ************
   statement = simpleStatement | compoundStatement

   simpleStatement = nullStatement | assignmentStatement
                   | procedureCallStatement | exitStatement

   compoundStatement = ifStatement | loopStatement
   */
   private void statement(){
      switch (token.code){
         case Token.ID:
            assignmentOrCallStatement();
            break;
         case Token.EXIT:
            exitStatement();
            break;
         case Token.IF:
            ifStatement();
            break;
         case Token.NULL:
            nullStatement();
            break;
         case Token.WHILE:
         case Token.LOOP:
            loopStatement();
            break;
         default: fatalError("error in statement");
      }
   }

   /* *********
   nullStatement = "null" ";"
   */
   private void nullStatement(){
      accept(Token.NULL, "'null' expected");
      accept(Token.SEMI, "';' expected");
   } 

   /* ***
   loopStatement =
         [ iterationScheme ] "loop" sequenceOfStatements "end" "loop" ";"

   iterationScheme = "while" condition
   */
   private void loopStatement(){
      // iteration scheme
      if (token.code == Token.WHILE)
      {
         token = scanner.nextToken();
         condition();
      }
      accept(Token.LOOP, "'loop' expected");
      sequenceOfStatements();
      accept(Token.END, "'end' expected");
      accept(Token.LOOP, "'loop' expected");
      accept(Token.SEMI, "';' expected");
   }

   /* ****
   ifStatement =
         "if" condition "then" sequenceOfStatements
         { "elsif" condition "then" sequenceOfStatements }
         [ "else" sequenceOfStatements ]
         "end" "if" ";"
   */

   private void ifStatement(){

      accept(Token.IF, "'if' expected");
      condition();
      //table.enterScope();
      accept(Token.THEN, "'then' expected");
      sequenceOfStatements();

      while(token.code == Token.ELSIF)
      {
         token = scanner.nextToken();
         condition();
         accept(Token.THEN, "'then' expected");
         sequenceOfStatements();
      }

      if (token.code == Token.ELSE)
      {
         token = scanner.nextToken();
         sequenceOfStatements();
      }
      accept(Token.END, "'end' expected");
      //table.exitScope();
      accept(Token.IF, "'if' expected");
      accept(Token.SEMI, "';' expected");
   }

   /* ****
   exitStatement = "exit" [ "when" condition ] ";"
   */
   private void exitStatement(){
      accept(Token.EXIT, "'exit' expected");
      if (token.code == Token.WHEN)
      {
         token = scanner.nextToken();
         condition();
      }

      accept(Token.SEMI, "';' expected");
   }

   /* ******
   assignmentStatement = <variable>name ":=" expression ";"

   procedureCallStatement = <procedure>name [ actualParameterPart ] ";"
   */
   private void assignmentOrCallStatement(){
      SymbolEntry entry = name();
      if (token.code == Token.GETS){
         acceptRole(entry, leftNames, "must be a variable name");
         token = scanner.nextToken();
         expression();
      }
      else{
         acceptRole(entry, SymbolEntry.PROC, "must be a proc name");
      }

      accept(Token.SEMI, "semicolon expected");
      
   }

   /*
   condition = <boolean>expression
   */
   private void condition(){
      expression(); 
   }

   /*
   expression = relation { "and" relation } | { "or" relation }
   */
   private void expression(){
      relation();
      if (token.code == Token.AND)
         while (token.code == Token.AND){
            token = scanner.nextToken();
            relation();
         }
      else if (token.code == Token.OR)
         while (token.code == Token.OR){
            token = scanner.nextToken();
            relation();
         }
   }

   /*
   relation = simpleExpression [ relationalOperator simpleExpression ]
   */
   private void relation(){
      simpleExpression();
      if (relationalOperator.contains(token.code)){
         token = scanner.nextToken();
         simpleExpression();
      }
   }

   /*
  simpleExpression =
         [ unaryAddingOperator ] term { binaryAddingOperator term }
   */
   private void simpleExpression(){
      if (addingOperator.contains(token.code))
         token = scanner.nextToken();
      term();
      while (addingOperator.contains(token.code)){
         token = scanner.nextToken();
         term();
      }
   }

   /*
   term = factor { multiplyingOperator factor }
   */
   private void term(){
      factor();
      while(multiplyingOperator.contains(token.code))
      {
         token = scanner.nextToken();
         factor();
      }
   }

   /*
   factor = primary [ "**" primary ] | "not" primary
   */
   private void factor(){
      if (token.code == Token.NOT)
      {
         token = scanner.nextToken();
         primary();
      }
      else
      {
         primary();
         if (token.code == Token.EXPO)
         {
            token = scanner.nextToken();
            primary();
         }
      }
   }

   /*
   primary = numericLiteral | name | "(" expression ")"
   */
   void primary(){
      switch (token.code){
         case Token.INT:
         case Token.CHAR:
            token = scanner.nextToken();
            break;
         case Token.ID:
            SymbolEntry entry = name();
            acceptRole(entry, rightNames, "must be a rightName/CONST identifier");
            break;
         case Token.L_PAR:
            token = scanner.nextToken();
            expression();
            accept(Token.R_PAR, "')' expected");
            break;
         default: fatalError("error in primary");
      }
   }

   /*
   name = identifier [ indexedComponent | selectedComponent ]
   */
   private SymbolEntry name(){
      SymbolEntry entry = findId();
      if (token.code == Token.L_PAR)
         indexedComponent();
      return entry;
   }

   /*
   indexedComponent = "(" expression  { "," expression } ")"
   */
   private void indexedComponent(){
      accept(Token.L_PAR, "'(' expected");
      expression();

      while(token.code == Token.COMMA)
      {
         token = scanner.nextToken();
         expression();
      }
      accept(Token.R_PAR, "')' expected");
   }

}
