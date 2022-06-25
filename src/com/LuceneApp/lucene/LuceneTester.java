package com.LuceneApp.lucene;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class LuceneTester {
	String indexDir = "C:\\Users\\User\\Desktop\\TReSABrowser\\LuceneApp\\INDEX";
	String dataDir = "C:\\Users\\User\\Desktop\\TReSABrowser\\LuceneApp\\DATA";
	Indexer indexer;
	Searcher searcher;
	TopDocs docs;
	private static JTextArea TA;
	public static GUI TRESA  = new GUI(); 
	public static void main(String[] args) {
		
	    TRESA.setBounds(220, 220, 1130, 800);
	    TRESA.setDefaultCloseOperation(EXIT_ON_CLOSE); 
	    
	}
	
	public void createIndex() throws IOException {
		indexer = new Indexer(indexDir);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
		long endTime = System.currentTimeMillis();
		indexer.close();
		
		TRESA.getTA().setText(numIndexed + " File(s) indexed, time taken: " + (endTime-startTime) + " ms\n");
		
	}
	
	public void search(String fieldname,String queryString,int slop,LuceneSearchType luceneSearchType, String secondQueryString,int num ) throws IOException, ParseException {
		
		searcher = new Searcher(indexDir);
		long startTime = System.currentTimeMillis();		
		docs = searcher.search(fieldname,queryString,slop,luceneSearchType,secondQueryString,num);
		long endTime = System.currentTimeMillis();
		TRESA.getTA().append(docs.totalHits +" found. Time : " + (endTime - startTime) + " ms\n");				
				
			
	}
	
	public TopDocs gettg() {
		return docs;
	}
	
	public Indexer GetIndexer() {
		return indexer;
	}
	
	public Searcher GetSearcher() {
		return searcher;
	}
	
	public 	GUI GetTRESA() {
		return TRESA;
	}
}

