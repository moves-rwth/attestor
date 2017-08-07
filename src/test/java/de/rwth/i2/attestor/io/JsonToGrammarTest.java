package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.GeneralNonterminal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import static org.junit.Assert.*;

public class JsonToGrammarTest {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger( "JsonToGrammarTest" );


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}


	
	@Test
	public void testConstructNonterminals() {
		Scanner scan;
		try {
			scan = new Scanner(new FileReader("src/test/resources/grammarEncodingTest.txt"));
			
			StringBuilder str = new StringBuilder();
			while (scan.hasNext())
				str.append(scan.nextLine());
			scan.close();

			JSONArray array = new JSONArray(str.toString());
			
			JsonToGrammar.parseForwardGrammar( array );
			
			Nonterminal nt = GeneralNonterminal.getNonterminal("DLList");
			assertEquals( "rank", 2, nt.getRank() );
			assertFalse( nt.isReductionTentacle( 0 ));
			assertFalse( nt.isReductionTentacle( 1 ) );			
		} catch( FileNotFoundException e ) {
			e.printStackTrace();
			fail("exception occurred.");
		}

	}
	
	@Test
	public void testConstructNonterminals2() {
		Scanner scan;
		try {
			scan = new Scanner(new FileReader("src/test/resources/SLList.txt"));
			
			StringBuilder str = new StringBuilder();
			while (scan.hasNext())
				str.append(scan.nextLine());
			scan.close();

			JSONArray array = new JSONArray(str.toString());
			
			JsonToGrammar.parseForwardGrammar( array );
			
			Nonterminal nt = GeneralNonterminal.getNonterminal("SinglyLinkedList");
			assertEquals( "rank", 2, nt.getRank() );
			assertFalse( nt.isReductionTentacle( 0 ));
			assertTrue( nt.isReductionTentacle( 1 ) );
		
			
		} catch( FileNotFoundException e ) {
			e.printStackTrace();
			fail("exception occurred.");
		}
	}
	
	@Test
	public void testConstructNonterminalsWithRedundand() {
		Scanner scan;
		try {
			scan = new Scanner(new FileReader("src/test/resources/grammarEncodingTestWithRedundand.txt"));
			
			StringBuilder str = new StringBuilder();
			while (scan.hasNext())
				str.append(scan.nextLine());
			scan.close();

			JSONArray array = new JSONArray(str.toString());
			
			JsonToGrammar.parseForwardGrammar( array );
			
			Nonterminal nt = GeneralNonterminal.getNonterminal("SinglyLinkedList");
			assertEquals( "rank", 2, nt.getRank() );
			assertFalse( nt.isReductionTentacle( 0 ));
			assertTrue( nt.isReductionTentacle( 1 ) );
		
			
		} catch( FileNotFoundException e ) {
			e.printStackTrace();
			fail("exception occurred.");
		}

	}
}
