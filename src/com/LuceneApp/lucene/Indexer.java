package com.LuceneApp.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
//import org.tartarus.snowball.SnowballProgram;
import javax.swing.JTextArea;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;



public class Indexer {
	
	public String DocumentName;
	private IndexWriter writer;
	 List<Document> Documents = new ArrayList<>();
	
	public Indexer(String indexDirectoryPath) throws IOException {
		
		Path indexPath = Paths.get(indexDirectoryPath);
		if(!Files.exists(indexPath)) {
			Files.createDirectory(indexPath);
		}
		
		Directory indexDirectory = FSDirectory.open(indexPath);
		//create the indexer
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		config.setOpenMode(OpenMode.CREATE);
		writer = new IndexWriter(indexDirectory, config);
		
	}
	
	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}
	
	public Document getDocument(File file) throws IOException {
		Document document = new Document();
		//index file contents
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		// PLACES DATA.
		String PlaceDataLine = br.readLine().toString();
		PlaceDataLine = PlaceDataLine.replace("<PLACES>", "");
		PlaceDataLine = PlaceDataLine.replace("</PLACES>", "");
		Field PlaceField = new Field(LuceneConstants.PLACES, PlaceDataLine, TextField.TYPE_STORED);
		// PEOPLE DATA.
		String PeopleDataLine = br.readLine().toString();
		PeopleDataLine = PeopleDataLine.replace("<PEOPLE>", "");
		PeopleDataLine = PeopleDataLine.replace("</PEOPLE>", "");
		Field PeopleField = new Field(LuceneConstants.PEOPLE, PeopleDataLine, TextField.TYPE_STORED);
		// TITLE DATA.
		String TitleDataLine = br.readLine().toString();
		TitleDataLine = TitleDataLine.replace("<TITLE>", "");
		TitleDataLine = TitleDataLine.replace("</TITLE>", "");
		Field TitleField = new Field(LuceneConstants.TITLE, TitleDataLine, TextField.TYPE_STORED);
		// BODY DATA.
		String temp, BodyDataLine = "";
		while ((temp = br.readLine()) != null) {
			if (temp.contains("<BODY>")) {
				temp = temp.replace("<BODY>", "");
			} else if (temp.contains("</BODY>")) {
				temp = temp.replace("</BODY>", "");
			}
			BodyDataLine += temp;
		}
		Field BodyField = new Field(LuceneConstants.BODY, BodyDataLine, TextField.TYPE_STORED);
		
		//index file name
		Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), StringField.TYPE_STORED);
		//index file path
		Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), StringField.TYPE_STORED);
		document.add(PlaceField);
		document.add(PeopleField);
		document.add(TitleField);
		document.add(BodyField);
		document.add(fileNameField);
		DocumentName = fileNameField.stringValue();
		document.add(filePathField);
		Documents.add(document);
		br.close();
		return document;
	}
	
	private void indexFile(File file) throws IOException {
	    
		System.out.println("Indexing "+ file.getCanonicalPath());
		Document document = getDocument(file);
		writer.addDocument(document);
	}
	
	public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
		//get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();
		for (File file : files) {
			if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)){
				indexFile(file);
			}
		}
		return writer.numRamDocs();
	}
	
	public List<Document> GetDocuments() {
		return Documents;
	}
}