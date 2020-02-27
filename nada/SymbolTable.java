import java.util.*;

public class SymbolTable extends Object{

   //private int level;
   private Stack<Map<String, SymbolEntry>> stack;
   /*private Chario chario;

   public Boolean scopeBool;
   public Boolean roleBool;
   */
   private static final SymbolEntry EMPTY_SYMBOL = new SymbolEntry("");

   public SymbolTable(){
      /*
      chario = c;
      scopeBool = s;
      roleBool = r;
      */
      reset();
   }

   public void reset(){
      //level = -1;
      stack = new Stack<Map<String, SymbolEntry>>();
   }

   public void enterScope(){
      stack.push(new HashMap<String, SymbolEntry>());
      //level++;
   }

   public void exitScope(){
      Map<String, SymbolEntry> table = stack.pop();
      //printTable(table);
      //level--;
   }

   public SymbolEntry enterSymbol(String id){
      Map<String, SymbolEntry> table = stack.peek();
      if (table.containsKey(id)){
         System.err.println("identifier already declared in this block");
         return EMPTY_SYMBOL;
      }
      else{
         SymbolEntry s = new SymbolEntry(id);
         table.put(id, s);
         return s;
      } 
   }

   public SymbolEntry findSymbol(String id){
      for (int i = stack.size() - 1; i >= 0; i--){
         Map<String, SymbolEntry> table = stack.get(i);
         SymbolEntry s = table.get(id);
         if (s != null)
             return s;
      }
      
      System.err.println("undeclared identifier");
      return EMPTY_SYMBOL;
   }
    /*     
   private void printTable(Map<String, SymbolEntry> table){
      if (roleBool || scopeBool)
      {
         chario.println("\nLevel " + level);
         chario.println("---------");
         for (SymbolEntry s : table.values())
            chario.println(s.toString());
      }
   }
   */

}