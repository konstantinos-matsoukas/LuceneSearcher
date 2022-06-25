package com.LuceneApp.lucene;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;

//import org.apache.lucene.analysis

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class Searcher{
	IndexSearcher indexSearcher;
	Directory indexDirectory;
	IndexReader indexReader;
	QueryParser queryParser;
	QueryParser queryParser1;
	QueryParser queryParser2;
	QueryParser queryParser3;
	QueryParser queryParser4;
	Query query;
	Analyzer MyAnalyzer;
	
	public Searcher(String indexDirectoryPath) throws IOException {
		List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", "be", "but", "by",
												   "for", "if", "in", "into", "is", "it",
												   "no", "not", "of", "on", "or", "such",
												   "that", "the", "their", "then", "there", "these",
												   "they", "this", "to", "was", "will", "with");
		CharArraySet stopWordsSet = new CharArraySet(stopWords, false);
		MyAnalyzer = new Analyzer() {
		      @Override
		      protected TokenStreamComponents createComponents( String fieldName ) {
		          //Create Tokens
		          TokenStreamComponents ts = new TokenStreamComponents( new StandardTokenizer() );
		          //LowerCase
		          ts = new TokenStreamComponents( ts.getSource(), new LowerCaseFilter( ts.getTokenStream() ) );
		          //Removing Stop-words
		          ts = new TokenStreamComponents( ts.getSource(), new StopFilter( ts.getTokenStream(), stopWordsSet ) );
		          //Stemming
		       
		          //ts = new TokenStreamComponents( ts.getSource(), new PorterStemFilter( ts.getTokenStream() ) );
		          return ts;
		      }
		  };
		
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		//indexSearcher.setSimilarity(new TFIDFSimilarity());
		//queryParser = new QueryParser(LuceneConstants.BODY, new StandardAnalyzer(stopWordsSet));
	}

	public TopDocs search(String fieldname,String queryString,int slop,LuceneSearchType luceneSearchType, String secondQueryString ,int num) throws IOException, ParseException {
		
		Query query = null;
		//query = queryParser.parse(searchQuery);
		
		switch (luceneSearchType){
		case SIMPLE_SEARCH:
			if(fieldname.compareTo("ALL")==0) {
				queryParser = new QueryParser(LuceneConstants.TITLE, new StandardAnalyzer());
				MultiFieldQueryParser queryParser2 = new MultiFieldQueryParser(new String[] {"title","body","people","places"},MyAnalyzer);
				
				
				query = queryParser2.parse(queryString);   
			 }
			else {
			queryParser = new QueryParser(fielddet(fieldname), MyAnalyzer);
			query = queryParser.parse(queryString);    
			 
			}
	        break;
			
	    case PHRASE_QUERY:
	    	if(fieldname.compareTo("ALL")==0) {
				queryParser = new QueryParser(LuceneConstants.TITLE, new StandardAnalyzer());
				MultiFieldQueryParser queryParser2 = new MultiFieldQueryParser(new String[] {"title","body","people","places"},MyAnalyzer);
				
				 query = new PhraseQuery(slop,(fielddet(fieldname)), queryString);
			        query = queryParser2.parse(queryString);
			 }
	    	else {
	    	queryParser = new QueryParser(fielddet(fieldname), MyAnalyzer);
	        query = new PhraseQuery(slop,(fielddet(fieldname)), queryString);
	        query = queryParser.parse(queryString);
	    	}
	        break;
	  
	    case BOOLEAN_QUERY:
	    	if(fieldname.compareTo("ALL")==0) {
				queryParser = new QueryParser(LuceneConstants.TITLE, new StandardAnalyzer());
				MultiFieldQueryParser queryParser2 = new MultiFieldQueryParser(new String[] {"title","body","people","places"},MyAnalyzer);
				
				
				query = queryParser2.parse(queryString);   
			 }
			else {
			queryParser = new QueryParser(fielddet(fieldname), MyAnalyzer);
			query = queryParser.parse(queryString);    
			 
			}               
	        break;
		
	}
		
		
		
		
		System.out.println("query: "+ query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH, Sort.RELEVANCE,true);
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}
	
	public String fielddet(String fieldname) {
		  
	  if(fieldname.compareTo("<BODY>")==0)
	      return LuceneConstants.BODY;
	  else if(fieldname.compareTo("<PLACES>")==0)
	      return LuceneConstants.PLACES;
	  else if(fieldname.compareTo("<TITLE>")==0)
	      return LuceneConstants.TITLE;
	  else
		  return LuceneConstants.PEOPLE;
	  
   }

	public IndexReader getIndex()  {
		return indexReader;
	
	}
	
	public void returnIndex() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
}

