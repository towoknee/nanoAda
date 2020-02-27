//'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''‘’
//
// Parses the a nada file and prints parse tree details
//
// Created on Feb 6 , 2020
//
// Copyright 2020 Daryl Posnett All rights reserved
//
package nada.visitors;
import nada.*;
import java.util.*;
import java.io.*;
import nada.node.*;
import nada.analysis.*;
public class SemanticAnalyzer extends DepthFirstAdapter
{
	private SymbolTable table;

	private void initTable(){
		 table = new SymbolTable();
		 table.enterScope();
		 SymbolEntry entry = table.enterSymbol("INTEGER");
		 entry.setRole(SymbolEntry.TYPE);
	}


	private void acceptRole(SymbolEntry s, int expected, String e){
		 if (s.role != SymbolEntry.NONE && s.role != expected)
		 {
			System.out.println(e);
			System.exit(0);
		 }
	}

	private SymbolEntry enterId(String x){
		 SymbolEntry entry = null;
		 entry = table.enterSymbol(x);
		 return entry;
	}

	private SymbolEntry findId(String x){
		 SymbolEntry entry = null;
		 entry = table.findSymbol(x);
		 return entry;
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
		initTable();
		node.getPNada().apply(this);
		node.getEOF().apply(this);
		outStart(node);
	}


	@Override
	public void caseASubprogramBody(ASubprogramBody node)
	{
		inASubprogramBody(node);
		if(node.getSubprogramSpec() != null)
		{
			node.getSubprogramSpec().apply(this);
		}
		if(node.getIs() != null)
		{
			node.getIs().apply(this);
		}
		if(node.getDeclPart() != null)
		{
			node.getDeclPart().apply(this);
		}
		if(node.getBegin() != null)
		{
			node.getBegin().apply(this);
		}
		if(node.getStmtSeq() != null)
		{
			node.getStmtSeq().apply(this);
		}
		if(node.getEnd() != null)
		{
			node.getEnd().apply(this);
		}
		table.exitScope();
		if(node.getIdent() != null)
		{
			SymbolEntry entry = findId(node.getIdent().toString().trim());
			acceptRole(entry, SymbolEntry.PROC, "must be a PROC identifier"); // *****
			node.getIdent().apply(this);
		}
		if(node.getSemi() != null)
		{
			node.getSemi().apply(this);
		}
		outASubprogramBody(node);
	}

	@Override
	public void caseAObjectDecl(AObjectDecl node)
	{
		inAObjectDecl(node);
		if(node.getIdentList() != null)
		{
			node.getIdentList().apply(this);
		}
		if(node.getColon() != null)
		{
			node.getColon().apply(this);
		}
		if(node.getIdent() != null)
		{
			SymbolEntry entry = findId(node.getIdent().toString().trim());
    		acceptRole(entry, SymbolEntry.TYPE, "Must be a TYPE identifier");
			node.getIdent().apply(this);
		}
		if(node.getSemi() != null)
		{
			node.getSemi().apply(this);
		}
		outAObjectDecl(node);
	}

	@Override
	public void caseAIdentList(AIdentList node)
	{
		inAIdentList(node);
		if(node.getIdent() != null)
		{
			SymbolEntry entry = enterId(node.getIdent().toString().trim());
			entry.setRole(SymbolEntry.VAR);
			node.getIdent().apply(this);
		}
		{
			List<PAnotherIdent> copy = new ArrayList<PAnotherIdent>(node.getAnotherIdent());
			for(PAnotherIdent e : copy)
			{
				e.apply(this);
			}
		}
		outAIdentList(node);
	}

	@Override
	public void caseAAnotherIdent(AAnotherIdent node)
	{
		inAAnotherIdent(node);
		if(node.getComma() != null)
		{
			node.getComma().apply(this);
		}
		if(node.getIdent() != null)
		{
			SymbolEntry entry = enterId(node.getIdent().toString().trim());
			entry.setRole(SymbolEntry.VAR);
			node.getIdent().apply(this);
		}
		outAAnotherIdent(node);
	}




	@Override
	public void caseASubprogramSpec(ASubprogramSpec node)
	{
		inASubprogramSpec(node);
		if(node.getProc() != null)
		{
			node.getProc().apply(this);
		}
		if(node.getIdent() != null)
		{
			SymbolEntry entry = enterId(node.getIdent().toString().trim());
			entry.setRole(SymbolEntry.PROC);
			node.getIdent().apply(this);
		}
		table.enterScope();
		if(node.getFormalPart() != null)
		{
			node.getFormalPart().apply(this);
		}
		outASubprogramSpec(node);
	}

	@Override
	public void caseAParamSpec(AParamSpec node)
	{
		inAParamSpec(node);
		if(node.getIdentList() != null)
		{
			node.getIdentList().apply(this);
		}
		if(node.getColon() != null)
		{
			node.getColon().apply(this);
		}
		if(node.getOut() != null)
		{
			node.getOut().apply(this);
		}
		if(node.getIdent() != null)
		{
			SymbolEntry entry = findId(node.getIdent().toString().trim());
			acceptRole(entry, SymbolEntry.TYPE, "Must be a TYPE identifier");
			node.getIdent().apply(this);
		}
		outAParamSpec(node);
	}

	@Override
	public void caseAAssignStmt(AAssignStmt node)
	{
		inAAssignStmt(node);
		if(node.getIdent() != null)
		{
			SymbolEntry entry = findId(node.getIdent().toString().trim());
			acceptRole(entry, SymbolEntry.VAR, "Must be a VAR identifier");
			node.getIdent().apply(this);
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
		outAAssignStmt(node);
	}

	@Override
	public void caseAProcCallStmt(AProcCallStmt node)
	{
		inAProcCallStmt(node);
		if(node.getIdent() != null)
		{
			SymbolEntry entry = findId(node.getIdent().toString().trim());
			acceptRole(entry, SymbolEntry.PROC, "Must be a PROC identifier");
			node.getIdent().apply(this);
		}
		if(node.getActualParamPart() != null)
		{
			node.getActualParamPart().apply(this);
		}
		if(node.getSemi() != null)
		{
			node.getSemi().apply(this);
		}
		outAProcCallStmt(node);
	}

	@Override
	public void caseANamePrimary(ANamePrimary node)
	{
		inANamePrimary(node);
		if(node.getIdent() != null)
		{
			SymbolEntry entry = findId(node.getIdent().toString().trim());
			acceptRole(entry, SymbolEntry.VAR, "Must be a VAR identifier");
			node.getIdent().apply(this);
		}
		outANamePrimary(node);
	}




}
