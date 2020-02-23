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
	public void defaultIn(Node node)
	{
		System.err.println("IN:" + node.getClass().getSimpleName() + ":" + node);
	}
	public void defaultOut(Node node)
	{
		System.err.println("OUT:" + node.getClass().getSimpleName() + ":" + node);
	}



	@Override
    public void caseAWritelnWriteStmt(AWritelnWriteStmt node)
    {
        inAWritelnWriteStmt(node);

        System.out.print("System.out.println");
        System.out.print("(");
        System.out.print(node.getWriteExpr().toString().trim());
        System.out.print(")");
        System.out.println(";");
        
        outAWritelnWriteStmt(node);
    }

	
}