//'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''‘’
//
// Parses the a nada file and prints parse tree details
//
// Created on Feb 6 , 2020
//
// Copyright 2020 Daryl Posnett All rights reserved
//
package nada.visitors;
import java.util.*;
import java.io.*;
import nada.node.*;
import nada.analysis.*;
public class SemanticAnalyzer extends DepthFirstAdapter
{
	public void defaultIn(Node node)
	{
		System.err.println("IN:" + node.getClass().getSimpleName() + ":" + node);
	}
	public void defaultOut(Node node)
	{
		System.err.println("OUT:" + node.getClass().getSimpleName() + ":" + node);
	}
}