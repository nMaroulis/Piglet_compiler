import syntaxtree.*;
import java.io.*;

public class Driver {


	public static void main (String [] args){
		 FileInputStream fis = null;
		
		try{
	    	 for(String s: args){

	            fis = new FileInputStream(s);  //input stream
	            SpigletParser parser = new SpigletParser(fis);   // Create Parser
	            Goal tree = parser.Goal();                       //
	           
	            methArgVisitor visitor1 = new methArgVisitor();  // 1os visitor
	            tree.accept(visitor1);	                         //
	           
	            KangaGenerator visitor2 = new KangaGenerator(s,visitor1.get_max_arg_map(), visitor1.get_stack_num_map()); //2os Visitor
	            tree.accept(visitor2);
	            
	            visitor2.close_file();
	              
	           System.out.println("SUCCESS: "+s.substring(26)); ///
	         }
	     }
	     
	     
	     
	     
	     catch(ParseException ex){
	        System.out.println(ex.getMessage());
	     }
	     catch(FileNotFoundException ex){
	        System.err.println(ex.getMessage());
	     }
	     finally{
	        try{
	        	if(fis != null) 
	        		fis.close();
	        }
	        catch(IOException ex){
	            System.err.println(ex.getMessage());
	        }
	     }    	
	}
}