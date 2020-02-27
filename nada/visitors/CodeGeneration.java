package nada.visitors;
import java.util.*;
import java.io.*;
import nada.node.*;
import nada.analysis.*;
public class CodeGeneration extends DepthFirstAdapter
{
    String pName;
    String getFile;
    String className; 
    FileWriter myWriter;
    int whichProc;
    int outCounter; 
    String outReturn; 
    int whiteSpaceCounter;
    String whiteSpace; 

    public CodeGeneration(String progName) 
    {
        this.pName = progName;
    }

	public void defaultIn(Node node)
	{
		//System.err.println("IN:" + node.getClass().getSimpleName() + ":" + node);
	}
	public void defaultOut(Node node)
	{
		//System.err.println("OUT:" + node.getClass().getSimpleName() + ":" + node);
	}

    public String indent()
    {
        whiteSpace = "";
        for (int i = 0; i < whiteSpaceCounter; i++)
        {
            whiteSpace = whiteSpace + "\t";
        }
        return whiteSpace;
    }


    @Override
    public void caseStart(Start node)
    {
        whichProc = 0;
        outCounter = 0;
        whiteSpaceCounter = 0;
        whiteSpace = "";

        // Creating a file
        //System.out.println("pName: " + pName);
        String javaFileName = pName.replace("nada", ""); 
        javaFileName = javaFileName.replace(".ada", ".java");
        //System.out.println("java File Name: " + javaFileName);
        className = javaFileName.replace(".java", "");
        className = className.replace("/", "");

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
          myWriter = new FileWriter(getFile);
          node.getPNada().apply(this);
          node.getEOF().apply(this);

          myWriter.close();
          System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }

    }

    @Override
    public void caseANada(ANada node) 
    {
            node.getSubprogramBody().apply(this);   
    }

    @Override
    public void caseASubprogramBody(ASubprogramBody node)
    {
        try
        {
            node.getSubprogramSpec().apply(this);
            myWriter.write("{\n");
            whiteSpaceCounter++;
            node.getDeclPart().apply(this); 

            if(whichProc == 1)
            {
                myWriter.write(indent() + "public static void main(String[] args){\n");
                whiteSpaceCounter++;
                node.getStmtSeq().apply(this);
                whiteSpaceCounter--;
                myWriter.write(indent() + "}\n");
                whiteSpaceCounter--;
                myWriter.write(indent() + "}\n");
                
            }
            else
            {
                node.getStmtSeq().apply(this);
                if (outCounter == 1)
                {
                    myWriter.write(indent() + "return " + outReturn + ";\n");
                    outCounter--;
                }
                whiteSpaceCounter--;
                myWriter.write(indent() + "}\n");
                whichProc -= 1;

            }

        } 
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseADeclPart(ADeclPart node)
    {
        {
            List<PBasicDecl> copy = new ArrayList<PBasicDecl>(node.getBasicDecl());
            for(PBasicDecl e : copy)
            {
                e.apply(this);
            }
        }
    }

    @Override
    public void caseAObjDeclBasicDecl(AObjDeclBasicDecl node)
    {
        node.getObjectDecl().apply(this);
    }

    @Override
    public void caseANumDeclBasicDecl(ANumDeclBasicDecl node)
    {
        node.getNumberDecl().apply(this);
    }

    @Override
    public void caseASpbDeclBasicDecl(ASpbDeclBasicDecl node)
    {
        node.getSubprogramBody().apply(this);
    }

    @Override
    public void caseAObjectDecl(AObjectDecl node)
    {
        try
        {
            String s = node.getIdentList().toString().trim(); // a,b,c
            String[] sArr = s.split(" , "); // [a, b, c]

            for(String i : sArr)
            {
                myWriter.write(indent());
                if(whichProc == 1)
                    myWriter.write("static ");
                myWriter.write("int");
                myWriter.write(" " + i);
                myWriter.write(";\n");
            }
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseANumberDecl(ANumberDecl node)
    {
        try
        {
            String s = node.getIdentList().toString().trim(); // a,b,c
            String[] sArr = s.split(" , "); // [a, b, c]

            for(String i : sArr)
            {
                myWriter.write(indent());
                if(whichProc == 1)
                    myWriter.write("static ");
                myWriter.write("final ");
                myWriter.write("int ");
                myWriter.write(i);

                myWriter.write(" = ");
                node.getSimpleExpr().apply(this);
                myWriter.write(";\n");
            }
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAIdentList(AIdentList node)
    {
        try
        {
            myWriter.write(node.getIdent().toString().trim());
            {
                List<PAnotherIdent> copy = new ArrayList<PAnotherIdent>(node.getAnotherIdent());
                for(PAnotherIdent e : copy)
                {
                    e.apply(this);
                }
            }
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

  @Override
    public void caseAAnotherIdent(AAnotherIdent node)
    {
        try
        {
            myWriter.write(",");
            myWriter.write(node.getIdent().toString().trim());
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseASubprogramSpec(ASubprogramSpec node)
    {
        try
        {   whichProc += 1; // For functions
            if (whichProc > 1)
            {
                if (node.getFormalPart().toString().trim().contains("out"))
                    myWriter.write(indent() + "public static int " + node.getIdent().toString().trim());
                else
                    myWriter.write(indent() + "public static void " + node.getIdent().toString().trim());
                myWriter.write("(");
                if(node.getFormalPart() != null)
                {
                    node.getFormalPart().apply(this);
                }
                myWriter.write(")");

            }
            else //For main class
            {
                myWriter.write("public class ");
                myWriter.write(className);
                //myWriter.write(node.getIdent().toString().trim());
            }
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
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
        try
        {
            String s = node.getIdentList().toString().trim(); // a,b,c
            String[] sArr = s.split(" , "); // [a, b, c]
            
            if(node.getOut() != null)
            {
                outCounter++;
                outReturn = s;  
            }

            for(String i : sArr)
            {
                myWriter.write("int");
                myWriter.write(" " + i);
                
                if (i != sArr[sArr.length - 1])
                    myWriter.write(", ");
            }

        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAAnotherParamSpec(AAnotherParamSpec node)
    {
        try
        {
            myWriter.write(",");
            node.getParamSpec().apply(this);
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
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
        try
        {
            myWriter.write(";\n");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAAssignStmt(AAssignStmt node)
    {
        try
        {
            myWriter.write(indent() + node.getIdent().toString().trim()); 
            myWriter.write(" = ");
            node.getSimpleExpr().apply(this);
            myWriter.write(";\n");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAWriteWriteStmt(AWriteWriteStmt node)
    {
        try
        {
            myWriter.write(indent() + "System.out.print");
            myWriter.write("(");
            node.getWriteExpr().apply(this);
            myWriter.write(")");
            myWriter.write(";\n");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

	@Override
    public void caseAWritelnWriteStmt(AWritelnWriteStmt node)
    {
        try
        {
            myWriter.write(indent() + "System.out.println");
            myWriter.write("(");
            node.getWriteExpr().apply(this);
            myWriter.write(")");
            myWriter.write(";\n");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAIfStmt(AIfStmt node)
    {
        try
        {
            myWriter.write(indent() + "if");
            myWriter.write("(");
            node.getRelation().apply(this); 
            myWriter.write(")");
            myWriter.write("{\n");
            whiteSpaceCounter++;

            node.getStmtSeq().apply(this); 
            
            whiteSpaceCounter--;
            myWriter.write(indent() + "}\n");

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

        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAElseifClause(AElseifClause node)
    {
        try
        {
            myWriter.write(indent() + "else if");
            myWriter.write("(");
            node.getRelation().apply(this);
            myWriter.write("){\n");
            whiteSpaceCounter++;
            node.getStmtSeq().apply(this);
            whiteSpaceCounter--;
            myWriter.write(indent() + "}\n");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
    }

    @Override
    public void caseAElseClause(AElseClause node)
    {
        try
        {
            myWriter.write(indent() + "else{\n");
            whiteSpaceCounter++;
            node.getStmtSeq().apply(this);
            whiteSpaceCounter--;
            myWriter.write(indent() + "}\n");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseALoopStmt(ALoopStmt node)
    {
        try
        {
            myWriter.write(indent() + "while");
            myWriter.write("(");
            node.getRelation().apply(this);
            myWriter.write(")");
            myWriter.write("{\n");
            whiteSpaceCounter++;
            node.getStmtSeq().apply(this);
            whiteSpaceCounter--;
            myWriter.write(indent() + "}\n");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAProcCallStmt(AProcCallStmt node)
    {
        try
        {
            myWriter.write(indent() + node.getIdent().toString().trim());
            if(node.getActualParamPart() != null)
            {
                node.getActualParamPart().apply(this);
            }
            myWriter.write(";\n");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAActualParamPart(AActualParamPart node)
    {
        try
        {
            myWriter.write("(");
            node.getSimpleExpr().apply(this);
            
            {
                List<PAnotherParam> copy = new ArrayList<PAnotherParam>(node.getAnotherParam());
                for(PAnotherParam e : copy)
                {
                    e.apply(this);
                }
            }
            myWriter.write(")");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAAnotherParam(AAnotherParam node)
    {
        try
        {
            myWriter.write(",");
            node.getSimpleExpr().apply(this);
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
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
        try
        {
            myWriter.write(node.getStringLit().toString().trim());
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseASimpleExprWriteExpr(ASimpleExprWriteExpr node)
    {
        try
        {
            myWriter.write(node.getSimpleExpr().toString().trim());
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

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
        try
        {
            myWriter.write("-");
        
            node.getPrimary().apply(this);
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseANumLitPrimary(ANumLitPrimary node)
    {
        try
        {
            myWriter.write(node.getNumberLit().toString().trim());
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseANamePrimary(ANamePrimary node)
    {
        try
        {
            myWriter.write(node.getIdent().toString().trim());
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    @Override
    public void caseAExprPrimary(AExprPrimary node)
    {
        try
        {
            myWriter.write("(");
            node.getSimpleExpr().apply(this);
            myWriter.write(")");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAEqRelOp(AEqRelOp node)
    {
        try
        {
            myWriter.write("==");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseANeqRelOp(ANeqRelOp node)
    {
        try
        {
            myWriter.write("!=");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseALtRelOp(ALtRelOp node)
    {
        try
        {
            myWriter.write("<");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseALeRelOp(ALeRelOp node)
    {
        try
        {
            myWriter.write("<=");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAGtRelOp(AGtRelOp node)
    {
        try
        {
            myWriter.write(">");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
    @Override
    public void caseAGeRelOp(AGeRelOp node)
    {
        try
        {
            myWriter.write(">=");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAPlusAddOp(APlusAddOp node)
    {
        try
        {
            myWriter.write("+");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAMinusAddOp(AMinusAddOp node)
    {
        try
        {
            myWriter.write("-");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAMultMulOp(AMultMulOp node)
    {
        try
        {
            myWriter.write("*");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseADivMulOp(ADivMulOp node)
    {
        try
        {
            myWriter.write("/");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void caseAModMulOp(AModMulOp node)
    {
        try
        {
            myWriter.write("%");
        }
        catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
	
}