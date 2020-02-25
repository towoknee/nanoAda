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
    public void caseANumberDecl(ANumberDecl node)
    {

        String s = node.getIdentList().toString().trim(); // a,b,c
        String[] sArr = s.split(" , "); // [a, b, c]

        for(String i : sArr)
        {
            System.out.print("final ");
            System.out.print("int ");
            System.out.print(i);

            System.out.print(" = ");
            node.getSimpleExpr().apply(this);
            System.out.println(";");
        }



        inANumberDecl(node);
        if(node.getIdentList() != null)
        {
            node.getIdentList().apply(this);
        }
        if(node.getColon() != null)
        {
            node.getColon().apply(this);
        }
        if(node.getConst() != null)
        {
            node.getConst().apply(this);
        }
        if(node.getGets() != null)
        {
            node.getGets().apply(this);
        }
        if(node.getSimpleExpr() != null)
        {
            node.getSimpleExpr().apply(this);
        }
        if(node.getSemi() != null)
        {
            node.getSemi().apply(this);
        }
        outANumberDecl(node);
    }

    @Override
    public void caseAIdentList(AIdentList node)
    {
        System.out.print(node.getIdent().toString().trim());
        {
            List<PAnotherIdent> copy = new ArrayList<PAnotherIdent>(node.getAnotherIdent());
            for(PAnotherIdent e : copy)
            {
                e.apply(this);
            }
        }
    }

  @Override
    public void caseAAnotherIdent(AAnotherIdent node)
    {
        System.out.print(",");
        System.out.print(node.getIdent().toString().trim());
    }

    @Override
    public void caseASubprogramSpec(ASubprogramSpec node)
    {
        System.out.print("public static void ");
        System.out.print(node.getIdent().toString().trim());
        System.out.print("(");
        if(node.getFormalPart() != null)
        {
            node.getFormalPart().apply(this);
        }
        System.out.print(")");
    }

    @Override
    public void caseAFormalPart(AFormalPart node)
    {   
        node.getParamSpec().apply(this);
        {
            List<PAnotherParamSpec> copy = new ArrayList<PAnotherParamSpec>(node.getAnotherParamSpec());
            for(PAnotherParamSpec e : copy)
            {
                e.apply(this);
            }
        }  
    }

    @Override
    public void caseAParamSpec(AParamSpec node)
    {
        String s = node.getIdentList().toString().trim(); // a,b,c
        String[] sArr = s.split(" , "); // [a, b, c]

        for(String i : sArr)
        {
            System.out.print(node.getIdent().toString().trim()); //idk if it gives type
            System.out.print(" ");
            System.out.print(i);

            if (i != sArr[sArr.length - 1])
                System.out.print(", ");
        }
    }

    @Override
    public void caseAAnotherParamSpec(AAnotherParamSpec node)
    {
        System.out.print(",");
        node.getParamSpec().apply(this);
    }

    @Override
    public void caseAStmtSeq(AStmtSeq node)
    {

        node.getStatement().apply(this);

        {
            List<PStatement> copy = new ArrayList<PStatement>(node.getStatements());
            for(PStatement e : copy)
            {
                e.apply(this);
            }
        }
    }

    @Override
    public void caseASimpleStmtStatement(ASimpleStmtStatement node)
    {
        node.getSimpleStmt().apply(this);
    }

    @Override
    public void caseACompoundStmtStatement(ACompoundStmtStatement node)
    {
        node.getCompoundStmt().apply(this);
    }

    @Override
    public void caseANullStmtSimpleStmt(ANullStmtSimpleStmt node)
    {
        node.getNullStmt().apply(this);
    }

    @Override
    public void caseAAssignStmtSimpleStmt(AAssignStmtSimpleStmt node)
    {
        node.getAssignStmt().apply(this);
    }

    @Override
    public void caseAProcCallStmtSimpleStmt(AProcCallStmtSimpleStmt node)
    {
        node.getProcCallStmt().apply(this);
    }

    @Override
    public void caseAWriteSimpleStmt(AWriteSimpleStmt node)
    {
        node.getWriteStmt().apply(this);
    }

    @Override
    public void caseAIfCompoundCompoundStmt(AIfCompoundCompoundStmt node)
    {
        node.getIfStmt().apply(this);
    }

    @Override
    public void caseALoopCompoundCompoundStmt(ALoopCompoundCompoundStmt node)
    {
        node.getLoopStmt().apply(this);
    }

    @Override
    public void caseANullStmt(ANullStmt node)
    {
        System.out.println(";");
    }

    @Override
    public void caseAAssignStmt(AAssignStmt node)
    {
        System.out.print(node.getIdent().toString().trim()); //apply(this);
        System.out.print(" = ");
        node.getSimpleExpr().apply(this);
        System.out.println(";");
    }

    @Override
    public void caseAWriteWriteStmt(AWriteWriteStmt node)
    {
        //inAWriteWriteStmt(node);
        System.out.print("System.out.print");
        System.out.print("(");
        node.getWriteExpr().apply(this);
        System.out.print(")");
        System.out.print(";");
        //outAWriteWriteStmt(node);
    }

	@Override
    public void caseAWritelnWriteStmt(AWritelnWriteStmt node)
    {
        //inAWritelnWriteStmt(node);
        System.out.print("System.out.println");
        System.out.print("(");
        node.getWriteExpr().apply(this);
        System.out.print(")");
        System.out.println(";");
        //outAWritelnWriteStmt(node);
    }

    @Override
    public void caseAIfStmt(AIfStmt node)
    {

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
            node.getElseClause().apply(this);
        }
    }

    @Override
    public void caseAElseifClause(AElseifClause node)
    {
        System.out.print("else if");
        System.out.print("(");
        node.getRelation().apply(this);
        System.out.println("){");
        node.getStmtSeq().apply(this);
        System.out.println("");
        System.out.println("}");
        
    }

    @Override
    public void caseAElseClause(AElseClause node)
    {
        System.out.println("else{");
        node.getStmtSeq().apply(this);
        System.out.println("");
        System.out.println("}");
    }

    @Override
    public void caseALoopStmt(ALoopStmt node)
    {
        System.out.print("while");
        System.out.print("(");
        node.getRelation().apply(this);
        System.out.print(")");
        System.out.println("{");
        node.getStmtSeq().apply(this);
        System.out.println("");
        System.out.println("}");
    }

    @Override
    public void caseAProcCallStmt(AProcCallStmt node)
    {
        System.out.print(node.getIdent().toString().trim());// apply(this);
        if(node.getActualParamPart() != null)
        {
            node.getActualParamPart().apply(this);
        }
        System.out.print(";");
    }

    @Override
    public void caseAActualParamPart(AActualParamPart node)
    {

        System.out.print("(");
        node.getSimpleExpr().apply(this);
        
        {
            List<PAnotherParam> copy = new ArrayList<PAnotherParam>(node.getAnotherParam());
            for(PAnotherParam e : copy)
            {
                e.apply(this);
            }
        }
        System.out.print(")");
    }

    @Override
    public void caseAAnotherParam(AAnotherParam node)
    {
        System.out.print(",");
        node.getSimpleExpr().apply(this);
    }

    @Override
    public void caseARelation(ARelation node)
    {
        node.getSimpleExpr().apply(this);

        if(node.getRelationClause() != null)
            node.getRelationClause().apply(this);
        
    }

    @Override
    public void caseARelationClause(ARelationClause node)
    {
        node.getRelOp().apply(this);
        node.getSimpleExpr().apply(this);
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