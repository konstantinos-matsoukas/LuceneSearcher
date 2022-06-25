package com.LuceneApp.lucene;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class GUI extends JFrame {
	
	 public JTextArea SearchResultsTA;
	 JScrollPane scroll;
	 private JTextArea NumResultsTA;
	 private JButton LoadFileButton;
	 private JButton SearchFileButton;
	 private JButton DeleteFileButton;
	 private JButton SearchButton;
	 private JButton ViewFileButton;
	 private JLabel NumResults;
	 DefaultTableModel model;
	 private JTable docTable;
	 private JComboBox searchTypesCombo;
	 private JComboBox FieldTypesCombo;
	 private String ChosenFilePath;
	 ArrayList<String> tableData = new ArrayList<String>();
	// public DefaultListModel<String> DocumentsListModel = new DefaultListModel<>();
	 public LuceneTester MyLuceneTester = new LuceneTester();
	 protected JTextField SearchBarTextField;
	 protected JTextField SearchFieldTextField;
	 private static final String searchTypes[] = { "Simple Search" ,  "Phrase Search", "Logical Boolean Search"};
	 private static final String fieldTypes[] = { "<PLACES>" ,  "<PEOPLE>", "<TITLE>", "<BODY>","ALL"};
	 File DATAfile = new File("C:\\Users\\User\\Desktop\\TReSABrowser\\LuceneApp\\DATA");
	 boolean errorExists=false;
	 ScoreDoc[] hits;

	 //LOAD FILE ACTION
	  private final ActionListener ChooseFileToLoadAction = new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e){
	        	
	            JFileChooser FileChooser=new JFileChooser();    
	            FileChooser.setMultiSelectionEnabled(true);
	            int i=FileChooser.showOpenDialog(LoadFileButton);  
	            
	            if(i==JFileChooser.APPROVE_OPTION){ 
	            	
	            	File[] ChosenFiles = FileChooser.getSelectedFiles();
	                //File ChosenFile=FileChooser.getSelectedFile();    
	            	for(File ChosenFile : ChosenFiles) {
			                ChosenFilePath=ChosenFile.getPath();  
			                SearchResultsTA.setText("Loading Data, Please wait...\n");
			                if(getFileExtension(ChosenFile).compareTo(".txt")==0) {
			                	SearchResultsTA.setText("This is a .txt file\n" + ChosenFile.getName() );
			                	try {
			                	    FileUtils.copyFileToDirectory(ChosenFile, DATAfile);
			                	    SearchResultsTA.setText(ChosenFile.getName() + " was loaded OK to DATA\n");
			                	} catch (IOException e2) {
			                	    e2.printStackTrace();
			                	}
			                }
			                else {
			                	final JPanel WarningPanel = new JPanel();
			        		    JOptionPane.showMessageDialog(WarningPanel, "This is not a .txt file!", "Warning",JOptionPane.WARNING_MESSAGE);
			                }	  
	            	} 	                
	            }    
	        }
	    };
	 
	    //DELETE FILE ACTION
		  private final ActionListener DeleteFileAction = new ActionListener(){
		        @Override
		        public void actionPerformed(ActionEvent e){
		        	
		            JFileChooser DeleteFileChooser=new JFileChooser(new File("C:\\Users\\User\\Desktop\\TReSABrowser\\LuceneApp\\DATA"));
		            DeleteFileChooser.setMultiSelectionEnabled(true);
		            int i=DeleteFileChooser.showOpenDialog(DeleteFileButton);  
		            
		            if(i==JFileChooser.APPROVE_OPTION){ 
		            File[] ChosenFiles=DeleteFileChooser.getSelectedFiles();	
		            
		            	for(File ChosenFile : ChosenFiles) {			               
			                ChosenFilePath=ChosenFile.getPath();  
			                try  
			                {               
			                	if(ChosenFile.delete())                     
			                	{  
			                		SearchResultsTA.setText(ChosenFile.getName() + " deleted"); 
			                	}  
			                	else  
			                	{  
			                		System.out.println("failed");  
			                	}  
			                }  
			                catch(Exception e3)  
			                {  
			                	e3.printStackTrace();  
			                } 
		            	}		                
		            }    
		        }
		    };
		    
		  //VIEW FILE ACTION
			  private final ActionListener ViewFileAction = new ActionListener(){
			        @Override
			        public void actionPerformed(ActionEvent e){
			        	int i=0;
			        	int column = 0;
			        	int row = docTable.getSelectedRow();
			        	String docName = docTable.getModel().getValueAt(row, column).toString();
			        	for ( i = 0; i < hits.length; i++) {
			        		int docId = hits[i].doc;
							//System.out.print("FOR"+i+"\n");
							try {
								Document d = MyLuceneTester.GetSearcher().getIndex().document(docId);
								if(d.getField("filename").stringValue().compareTo(docName)==0) {
					
									SearchResultsTA.append( "\n");
									SearchResultsTA.append( "NAME : " + d.getField("filename").stringValue() + "\n" );
									SearchResultsTA.append( "PLACE : " + d.getField("places").stringValue() + "\n" );
									SearchResultsTA.append( "PEOPLE : " + d.getField("people").stringValue() + "\n" );
									SearchResultsTA.append( "TITLE : " + d.getField("title").stringValue() + "\n" );
									SearchResultsTA.append( "BODY : " + d.getField("body").stringValue() + "\n" );
									SearchResultsTA.append( "\n");
								}
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			        	}
			            
			       
			            		                
			               
			        }
			    };
		    
		    //Search file
		    private final ActionListener ChooseFileToSearch = new ActionListener(){
		        @Override
		        public void actionPerformed(ActionEvent e){
		        	
		            JFileChooser FileChooser=new JFileChooser();    
		            FileChooser.setMultiSelectionEnabled(false);
		            int i=FileChooser.showOpenDialog(LoadFileButton);  
		            if(NumResultsTA.getText().compareTo("")!=0) {
		            	int numOfResults = Integer.parseInt(NumResultsTA.getText());
		    			String targetField = (String)FieldTypesCombo.getSelectedItem();
			            if(i==JFileChooser.APPROVE_OPTION){ 
			            	
			            	File ChosenFile = FileChooser.getSelectedFile();
			            	
			            	String querystring=ChosenFile.toString();
			            	try {
								MyLuceneTester.createIndex();
								filesearch(targetField,ChosenFile, numOfResults);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}		  	                
			            }
		            }
		            else {
		            	final JPanel WarningPanel = new JPanel();
						JOptionPane.showMessageDialog(WarningPanel, "The number of results box is empty!", "Warning",JOptionPane.WARNING_MESSAGE);
		            }
		            
		        }
		    };
	 
	 //Search 
	 private final ActionListener SearchActionListener = new ActionListener(){
	        @Override
	        public void actionPerformed(ActionEvent e){
	        	int i=0;
	        	model.setRowCount(0);
	        	if(SearchBarTextField.getText().compareTo("")!=0){
						if(NumResultsTA.getText().compareTo("")!=0) {
							int numOfResults = Integer.parseInt(NumResultsTA.getText());
							int selectedSearchIndex =searchTypesCombo.getSelectedIndex();
			    			String querystring=SearchBarTextField.getText();
			    			String targetField = (String)FieldTypesCombo.getSelectedItem();
			    			
					        	if((querystring.compareTo("")!=0)&&(targetField.compareTo("")!=0)){
					        		try {
					        			MyLuceneTester.createIndex();						    								   
						    			switch (selectedSearchIndex){
					                    case 0: simpleSearch(targetField, querystring, numOfResults); break;
					                    case 1: phraseSearch(targetField, querystring, numOfResults); break;
					                    case 2: booleanQuery(targetField, querystring, numOfResults); break;
					                    case 3: simpleSearch(targetField="ALL", querystring, numOfResults); break;
						    			}
						    			//MyLuceneTester.createIndex();
										//MyLuceneTester.search(SearchBarTextField.getText(),Integer.parseInt(NumResultsTA.getText()));
										//int maxDoc = MyLuceneTester.GetSearcher().getIndex().maxDoc();							
										int rank = 1;
										int j = 0;
										if(MyLuceneTester.gettg().totalHits.value!=0) {
											hits = MyLuceneTester.gettg().scoreDocs;
											SearchResultsTA.append("Rank   "  + "Score   " + "     Title" +"  	  Content\n" );		
											for ( i = 0; i < hits.length; i++) {
												if(j<numOfResults) {
													int docId = hits[i].doc;
													//System.out.print("FOR"+i+"\n");
													Document d = MyLuceneTester.GetSearcher().getIndex().document(docId);
													String example;
													String title = d.getField("filename").stringValue();
													tableData.add(title);
													model.addRow(new Object[]{tableData.get(i)});
													if(targetField.compareTo("ALL")==0) {
												       example=GetExample("<TITLE>",d )+ " "+GetExample("<BODY>",d )+"  "+GetExample("<PLACES>",d )+"  "+ GetExample("<PEOPLE>",d );
													}
													else {
														 example=GetExample(targetField,d );
													}
													
													String score = String.valueOf((float)hits[i].score);
													SearchResultsTA.append( "  " +  rank + "       " +  score + "     " + title + "     " + example + "\n");
													rank++;
													j++;
												}
											}
											j = 0;
											
										}
										else {
											SearchResultsTA.append("No Results!!!" );
										}
										
				    			
									} catch (IOException e1) {
										e1.printStackTrace();
									} catch (ParseException e2) {
										e2.printStackTrace();
									}
					        	}
						}
						else {
							final JPanel WarningPanel = new JPanel();
							JOptionPane.showMessageDialog(WarningPanel, "The number of results box is empty!", "Warning",JOptionPane.WARNING_MESSAGE);
						}			
				}								
				else {
					final JPanel WarningPanel = new JPanel();
					JOptionPane.showMessageDialog(WarningPanel, "Search Bar is empty!", "Warning",JOptionPane.WARNING_MESSAGE);
				}
	        }
	    };
	    
	    public String GetExample(String targetfield,Document d ) {
	    	 if(targetfield.compareTo("<BODY>")==0)
	   	      return d.getField("body").stringValue();
	   	  else if(targetfield.compareTo("<PLACES>")==0)
	   	      return d.getField("places").stringValue();
	   	  else if(targetfield.compareTo("<TITLE>")==0)
	   	      return d.getField("title").stringValue();
	   	  else
	   		  return d.getField("people").stringValue();
	    	
	    	
	    	
	    }
	    
	    
	    
	    
	    public void simpleSearch(String fieldName, String term, int numOfResults) throws IOException, ParseException{
	    	SearchResultsTA.append( "Started simple search:\n");
	    	MyLuceneTester.search(fieldName, term,0, LuceneSearchType.SIMPLE_SEARCH, "" , numOfResults);
	    
	    }
	 
	    public void booleanQuery(String fieldName, String term, int numOfResults) throws IOException, ParseException{
	    	SearchResultsTA.append( "Started Boolean search:\n");
	        if((term.contains("and"))||(term.contains("or"))){
	        	 MyLuceneTester.search(fieldName, term,0, LuceneSearchType.SIMPLE_SEARCH, "" , numOfResults);
	            
	        }
	        else {
	        	SearchResultsTA.append( "ERROR NO LOGIC OPERATOR\n");
	        	errorExists=true;
	        }
	       
	    }
	    
	    public void phraseSearch(String fieldName, String term, int numOfResults) throws IOException, ParseException{
	    	SearchResultsTA.append( "Started Phrase search:\n");
	    	String data[] = term.split(" ");
	        if(term.compareTo("")==0)
	        	SearchResultsTA.append("Invalid Input");
	        else if(data.length>1){	            
	            String secondTerm = data[0];
	            MyLuceneTester.search(fieldName, term, 5, LuceneSearchType.PHRASE_QUERY,secondTerm,numOfResults);
	          
	        }
	        else {
	        	SearchResultsTA.append("ERROR");
	        	errorExists=true;
	        }
	    }
	    
	    
	    public void filesearch(String fieldName, File file, int numOfResults) throws IOException, ParseException{
	    	SearchResultsTA.append( "Started simple search:\n");
	    	
	    	String str="";
	    	
	    	
	    	if(fieldName=="<PLACES>") {
	    		 str=GetPlace(file);
	    	}
	    	else if(fieldName=="<PEOPLE>") {
	    		 str=GetPeople(file);
	    	}
	    	else if(fieldName=="<TITLE>") {
	    		str=GetTitle(file);
	    	}
	    	else if(fieldName=="<BODY>") {
	    		str=GetBody(file);
	    	}
	    	
	    	MyLuceneTester.search(fieldName,str,0, LuceneSearchType.SIMPLE_SEARCH, "" , numOfResults);
	    	int maxDoc = MyLuceneTester.GetSearcher().getIndex().maxDoc();							
			  int rank = 1;
              int j = 0;
              hits = MyLuceneTester.gettg().scoreDocs;
              SearchResultsTA.append("Rank   "  + "Score   " + "Title\n   " );
              for (int i = 0; i < hits.length; i++) {
                  if(j<numOfResults) {
                      int docId = hits[i].doc;
                      System.out.print("FOR\n");
                      Document d = MyLuceneTester.GetSearcher().getIndex().document(docId);
                      String title = d.getField("filename").stringValue();
                      tableData.add(title);
					  model.addRow(new Object[]{tableData.get(i)});
                      String score = String.valueOf((float)hits[docId].score);
                      SearchResultsTA.append( " " +  rank + "     " +  score + "     " + title + "\n");
                      rank++;
                      j++;
                  }
              }
		
			
	    
	    
	    
	    }
	    
	    public String GetPlace(File file) {
	    	String content="";
	    	ChosenFilePath=file.getPath();  
	    	try
	        {
	            content = new String ( Files.readAllBytes( Paths.get(ChosenFilePath) ) );
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	    
	    	final Pattern pattern = Pattern.compile("<PLACES>(.+?)</PLACES>", Pattern.DOTALL);
	    	final Matcher matcher = pattern.matcher(content);
	    	matcher.find();
	    	
	    	System.out.println(matcher.group(1)); // Prints String I want to extract
	    	
	    	
	    	String str=matcher.group(1);
	    	return str;
	    }
	    
	    public String GetPeople(File file) {
	    	String content="";
	    	ChosenFilePath=file.getPath();  
	    	try
	        {
	            content = new String ( Files.readAllBytes( Paths.get(ChosenFilePath) ) );
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	    
	    	final Pattern pattern = Pattern.compile("<PEOPLE>(.+?)</PEOPLE>", Pattern.DOTALL);
	    	final Matcher matcher = pattern.matcher(content);
	    	matcher.find();
	    	
	    	System.out.println(matcher.group(1)); // Prints String I want to extract
	    	
	    	
	    	String str=matcher.group(1);
	    	return str;
	    }
	    
	    public String GetBody(File file) {
	    	String content="";
	    	ChosenFilePath=file.getPath();  
	    	try
	        {
	            content = new String ( Files.readAllBytes( Paths.get(ChosenFilePath) ) );
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	    
	    	final Pattern pattern = Pattern.compile("<BODY>(.+?)</BODY>", Pattern.DOTALL);
	    	final Matcher matcher = pattern.matcher(content);
	    	matcher.find();
	    	
	    	System.out.println(matcher.group(1)); // Prints String I want to extract
	    	
	    	
	    	String str=matcher.group(1);
	    	return str;
	    }
	    
	    
	    
	    public String GetTitle(File file) {
	    	String content="";
	    	ChosenFilePath=file.getPath();  
	    	try
	        {
	            content = new String ( Files.readAllBytes( Paths.get(ChosenFilePath) ) );
	        } 
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	    
	    	final Pattern pattern = Pattern.compile("<TITLE>(.+?)</TITLE>", Pattern.DOTALL);
	    	final Matcher matcher = pattern.matcher(content);
	    	matcher.find();
	    	
	    	System.out.println(matcher.group(1)); // Prints String I want to extract
	    	
	    	
	    	String str=matcher.group(1);
	    	return str;
	    }
	    public void showResults(String showField) throws IOException{
	    	SearchResultsTA.append(showField + "\n");
	    }
	
	GUI(){
	        
		 /* GUI Components*/
		
		 setLayout(null); 
	     
	     SearchButton=new JButton("Search"); 
		 SearchButton.setBounds(10,10, 150, 50);
		 SearchButton.setLayout(null);
		 SearchButton.addActionListener(SearchActionListener);
		 add(SearchButton);
		 
		 LoadFileButton = new JButton("Load Files"); 
		 LoadFileButton.setBounds(800,10,140, 50);
		 LoadFileButton.setLayout(null);
		 LoadFileButton.addActionListener(ChooseFileToLoadAction);
		 add(LoadFileButton);
		 
		 SearchFileButton = new JButton("Search Files"); 
		 SearchFileButton.setBounds(800,110,140, 50);
		 SearchFileButton.setLayout(null);
		 SearchFileButton.addActionListener(ChooseFileToSearch);
		 add(SearchFileButton);
		 
		 DeleteFileButton = new JButton("Delete");
		 DeleteFileButton.setBounds(960,10,140, 50);
		 DeleteFileButton.setLayout(null);
		 DeleteFileButton.addActionListener(DeleteFileAction);
		 add(DeleteFileButton);
		 
		 SearchBarTextField = new JTextField();
		 SearchBarTextField.setBounds(200,10, 580, 50);
		 SearchBarTextField.setLayout(null);
		 add(SearchBarTextField);
		 
		 
		 SearchResultsTA = new JTextArea();
		 SearchResultsTA.setBounds(10,80, 770, 670);
		 SearchResultsTA.setLayout(null);
		 add(SearchResultsTA);
	        

		 NumResults = new JLabel();
		 NumResults.setText("Number of results: ");
		 NumResults.setBounds(800,60,140, 50);
		 add(NumResults);
		 
		 NumResultsTA = new JTextArea();
		 NumResultsTA.setBounds(920,80, 20, 20);
		 NumResultsTA.setLayout(null);
		 add(NumResultsTA);
		 
		 searchTypesCombo = new JComboBox(searchTypes); 
		 searchTypesCombo.setBounds(800,170,140, 50); 
		 add(searchTypesCombo);
		 
		 FieldTypesCombo = new JComboBox(fieldTypes); 
		 FieldTypesCombo.setBounds(800,230,140, 50); 
		 add(FieldTypesCombo);
		 
		 model = new DefaultTableModel();
		 model.addColumn("MyColumnHeader");
		 docTable = new JTable(model);
		 docTable.setBounds(800, 290, 200, 460);
		 add(docTable);
		 
		 ViewFileButton = new JButton("View");
		 ViewFileButton.setBounds(1010,290,100, 50);
		 ViewFileButton.setLayout(null);
		 ViewFileButton.addActionListener(ViewFileAction);
		 add(ViewFileButton);
		 
		 setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	     setTitle("TReSA Browser");
	      
	     setVisible(true);
	    
	    
		
	}

	
	public  JTextArea getTA() {
		return SearchResultsTA;
	}
	
	public  JTextArea getNumResultsTA() {
		return NumResultsTA;
	}
	
	 private static String getFileExtension(File file) {
	        String extension = "";
	 
	        try {
	            if (file != null && file.exists()) {
	                String name = file.getName();
	                extension = name.substring(name.lastIndexOf("."));
	            }
	        } catch (Exception e) {
	            extension = "";
	        }
	 
	        return extension;
	 
	    }
	 
	
		

}
 
