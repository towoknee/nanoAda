//'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''‘’
//
//

package nada.visitors;
import java.util.*;
import java.io.*;
import nada.node.*;
import nada.analysis.*;
public class CodeGeneration extends DepthFirstAdapter
{
    String pName;
    static String getFile;

    public CodeGeneration(String progName) 
    {
        this.pName = progName;
    }

	public void defaultIn(Node node)
	{
		System.err.println("IN:" + node.getClass().getSimpleName() + ":" + node);
	}
	public void defaultOut(Node node)
	{
		System.err.println("OUT:" + node.getClass().getSimpleName() + ":" + node);
	}


    @Override
    public void caseStart(Start node)
    {
        inStart(node);
        // Creating a file
        String javaFileName = pName.replace("nada", "java"); 
        try
        {
            getFile = "./nada/" + javaFileName;
            File myFile = new File(getFile);
            if(myFile.createNewFile())
                System.out.println("File Created: " + myFile.getName());
            else
                System.out.println("Already exists");
        }
        catch(IOException e)
        {
            System.out.println("Error occurred");
            e.printStackTrace();
        }
        
        // Writing to file
        try 
        {
          FileWriter myWriter = new FileWriter(getFile);
          myWriter.write("Files in Java might be tricky, but it is fun enough!\n");
          myWriter.write("Did it append?");
          System.out.println("Testing Write");
          myWriter.close();
          System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }

        node.getPNada().apply(this);
        node.getEOF().apply(this);
        outStart(node);
    }

    @Override
    public void caseAWriteWriteStmt(AWriteWriteStmt node)
    {
        inAWriteWriteStmt(node);
        System.out.print("System.out.print");
        System.out.print("(");
        node.getWriteExpr().apply(this);
        System.out.print(")");
        System.out.print(";");
        outAWriteWriteStmt(node);
    }

	@Override
    public void caseAWritelnWriteStmt(AWritelnWriteStmt node)
    {
        inAWritelnWriteStmt(node);
        System.out.print("System.out.println");
        System.out.print("(");
        node.getWriteExpr().apply(this);
        System.out.print(")");
        System.out.println(";");
        outAWritelnWriteStmt(node);
    }

    @Override
    public void caseAIfStmt(AIfStmt node)
    {
        inAIfStmt(node);

        System.out.print("if");
        System.out.print("(");
        node.getRelation().apply(this); // toString().trim();  /// not sure
        System.out.print(")");
        System.out.println("{");

        node.getStmtSeq().apply(this); // toString().trim(); // not sure
        
        System.out.println("}");

        {
            List<PElseifClause> copy = new ArrayList<PElseifClause>(node.getElseifClause());
            for(PElseifClause e : copy)
            {
                e.apply(this);
            }
        }

        if(node.getElseClause() != null)
        {
            System.out.print("else");
            System.out.print("{");
            node.getElseClause().toString().trim();
            System.out.print("}");
        }



        outAIfStmt(node);
    }

    @Override
    public void caseAStringLitWriteExpr(AStringLitWriteExpr node)
    {
        System.out.print(node.getStringLit().toString().trim());
    }

    @Override
    public void caseASimpleExprWriteExpr(ASimpleExprWriteExpr node)
    {
        System.out.print(node.getSimpleExpr().toString().trim());
    }


    // consider the use of left and right 
    @Override
    public void caseASimpleExpr(ASimpleExpr node)
    {
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        node.getAddOp().apply(this);
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
    }


    @Override
    public void caseATermSimpleExpr(ATermSimpleExpr node)
    {
        node.getTerm().apply(this);
    }

    // consider the use of left and right?? 
    @Override
    public void caseATerm(ATerm node)
    {
        if(node.getLeft() != null)
        {
            node.getLeft().apply(this);
        }
        node.getMulOp().apply(this);
        if(node.getRight() != null)
        {
            node.getRight().apply(this);
        }
    }

    @Override
    public void caseAFactorTerm(AFactorTerm node)
    {
        node.getFactor().apply(this);
    }

    @Override
    public void caseAFactorFactor(AFactorFactor node)
    {
        node.getPrimary().apply(this);
    }

    @Override
    public void caseANegPrimFactor(ANegPrimFactor node)
    {
        System.out.print("-");
        node.getPrimary().apply(this);
    }

    @Override
    public void caseANumLitPrimary(ANumLitPrimary node)
    {

        System.out.print(node.getNumberLit().toString().trim());//apply(this);
    }

    @Override
    public void caseANamePrimary(ANamePrimary node)
    {
        System.out.print(node.getIdent().toString().trim());//apply(this); // not sure

    }

    @Override
    public void caseAExprPrimary(AExprPrimary node)
    {
        System.out.print("(");
        node.getSimpleExpr().apply(this);
        System.out.print(")");
    }

    @Override
    public void caseAEqRelOp(AEqRelOp node)
    {
        System.out.print("==");
    }

    @Override
    public void caseANeqRelOp(ANeqRelOp node)
    {
        System.out.print("!=");
    }

    @Override
    public void caseALtRelOp(ALtRelOp node)
    {
        System.out.print("<");
    }

    @Override
    public void caseALeRelOp(ALeRelOp node)
    {
        System.out.print("<=");
    }

    @Override
    public void caseAGtRelOp(AGtRelOp node)
    {
        System.out.print(">");
    }
    
    @Override
    public void caseAGeRelOp(AGeRelOp node)
    {
        System.out.print(">=");
    }

    @Override
    public void caseAPlusAddOp(APlusAddOp node)
    {
        System.out.print("+");
    }

    @Override
    public void caseAMinusAddOp(AMinusAddOp node)
    {
        System.out.print("-");
    }

    @Override
    public void caseAMultMulOp(AMultMulOp node)
    {
        System.out.print("*");
    }

    @Override
    public void caseADivMulOp(ADivMulOp node)
    {
        System.out.print("/");
    }

    @Override
    public void caseAModMulOp(AModMulOp node)
    {
        System.out.print("%");
    }
	
}