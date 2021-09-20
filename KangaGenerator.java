import syntaxtree.*;

import java.util.*;

import visitor.*;

import java.io.*;

public class KangaGenerator extends DepthFirstVisitor{
	
	private HashMap<String,Integer> max_arg_map;
	private HashMap<String,Integer> stack_num_map;
	private BufferedWriter bw;
	private String lastMeth;
	private String lastReg;
	private boolean label_flag;
	private int argCounter;
	private boolean call_flag;
	private String stmt;
	private int sCounter;
	
	public KangaGenerator(String argu,HashMap<String, Integer> map, HashMap<String, Integer> map2){
		argu= argu.substring(0,argu.length()-4);
		max_arg_map = map;
		stack_num_map = map2;
		lastMeth = null;
		lastReg = null;
		label_flag = false;
		argCounter =0;
		call_flag = false;
		stmt = null;
		sCounter = 0;
	
	try  {
		File output = new File(argu +".kg");
	 
	// if file doesnt exists, then create it
	if (!output.exists()) {
		output.createNewFile();
	}

	FileWriter fw = new FileWriter(output.getAbsoluteFile());
	bw = new BufferedWriter(fw);
	}
	catch(IOException e) {
		e.printStackTrace();
	}
}

//--------------- File methods  ---------------------
	private void write(String s){
		try {  bw.write(s);   } 
		catch (IOException e) { e.printStackTrace(); }		
	}

	private void writeln(String s){ // write me allagh grammhs
		write(s+"\n");  
	}
	
	private void writelnt(String s){ // write me allagh grammhs
		write("\t"+s+"\n");  
	}
	
	public void close_file(){   //kleinei to arxeio afou ta exoume grapsei ola , kaleitai ap th driver
		try {
			bw.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
// - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public void visit(Goal g){
		lastMeth = "MAIN";
		writeln("MAIN [0] [" + stack_num_map.get(lastMeth) + "] ["+ max_arg_map.get(lastMeth) + "]");
		g.f1.accept(this);
		writeln("END");
		if(g.f3.present())
			g.f3.accept(this);
	}
	
	public void visit(StmtList sl){
		if(sl.f0.present())
			sl.f0.accept(this);
	}
	
	public void visit(Procedure pr){
		int i;
		lastMeth = pr.f0.f0.toString();
		String arg_num = pr.f2.f0.toString();
		writeln(lastMeth+" [" + arg_num +"] [" + stack_num_map.get(lastMeth) + "] ["+ max_arg_map.get(lastMeth) + "]"); 
		// temp load
		writeln("//  Temporarely load stack values ");
		for(i = 0;i<Integer.parseInt(arg_num);i++){
			writelnt("ALOAD v0 SPILLEDARG " + Integer.toString(i));
			writelnt("MOVE t" + Integer.toString(i) + " v0");
		}
		i=0;
		while(i<4  &&  i < Integer.parseInt(arg_num)){
			writelnt("MOVE v0 a" + Integer.toString(i));
			writelnt("ASTORE SPILLEDARG "+Integer.toString(i) + " v0");
			i++;
		}
		//Meth Body
		writeln("//  Method Body");
		pr.f4.accept(this);
		//Reload values
		writeln("//  Reload stack values");
		for(i = 0;i<Integer.parseInt(arg_num);i++){
			writelnt("MOVE v1 t" + Integer.toString(i));
			writelnt("ASTORE SPILLEDARG " + Integer.toString(i) + " v1");
			
		}
		writeln("END");
	}
	
	public void visit(StmtExp se ){
		se.f1.accept(this);
		se.f3.accept(this);
		writelnt("MOVE v0 " + lastReg); // lastReg takes value from SimpleExp
	}
	
	public void visit(Stmt st){
		label_flag = true;
		st.f0.accept(this);
		label_flag = false;
	}
	
	public void visit(NoOpStmt _){
		writelnt("NOOP");
	}
	
	public void visit(ErrorStmt _){
		writelnt("ERROR");
	}
	
	public void visit(JumpStmt js){
		writelnt("JUMP " + js.f1.f0.toString());
	}
	
	public void visit(CJumpStmt cjs){
		writelnt("ALOAD v0 SPILLEDARG " + cjs.f1.f1.f0.toString());
		writelnt("CJUMP v0 " + cjs.f2.f0.toString());
	}
	
	public void visit(PrintStmt ps){
		ps.f1.accept(this);
		writelnt("PRINT " + lastReg); // its made in SimpleExp
	}
	
	public void visit(MoveStmt ms){
		stmt = "MOVE";
		ms.f2.accept(this);
		stmt = null;
		//writeln("MOVE v1 " + lastReg); // to lastReg an einai hallocate px 8a parei olo to strig HALLOCATE ..
		writelnt("ASTORE SPILLEDARG " + ms.f1.f1.f0.toString() + " " + lastReg);
	}
	
	public void visit(HStoreStmt hss){
		writelnt("ALOAD v1 SPILLEDARG " + hss.f1.f1.f0.toString());
		writelnt("ALOAD v0 SPILLEDARG " + hss.f3.f1.f0.toString());
		writelnt("HSTORE v1 "+ hss.f2.f0.toString() + " v0");
		writelnt("ASTORE SPILLEDARG " + hss.f1.f1.f0.toString() + " v1");
	}
	
	public void visit(HLoadStmt hls){
		//writelnt("ALOAD v1 SPILLEDARG " + hls.f1.f1.f0.toString());
		writelnt("ALOAD v0 SPILLEDARG " + hls.f2.f1.f0.toString());
		writelnt("HLOAD v1 v0 " + hls.f3.f0.toString());
		writelnt("ASTORE SPILLEDARG " + hls.f1.f1.f0.toString() + " v1");
	}
	
	public void visit(Exp e){
		e.f0.accept(this);
	}
	
	public void visit(BinOp bo){
		bo.f2.accept(this);
		writelnt("ALOAD v0 SPILLEDARG " + bo.f1.f1.f0.toString());
		writelnt("MOVE v0 " + bo.f0.f0.choice.toString() + " v0 " + lastReg);
		lastReg = "v0";
	}
	
	public void visit(SimpleExp se){
		se.f0.accept(this);
	}
	
	public void visit(IntegerLiteral il){
		
		lastReg = il.f0.toString();
		
		if(stmt == "MOVE"){     //to vazw sto v1 afou h binop exei to v0
			writelnt("MOVE v1 " + lastReg);
			lastReg = "v1";
		}
	}
	
	public void visit(Temp tmp){
		
		writelnt("ALOAD v1 SPILLEDARG " + tmp.f1.f0.toString());
		lastReg = "v1";
		
		if(call_flag){  //einai apo call
			if(argCounter < 4)
				writelnt("MOVE a" + Integer.toString(argCounter) + " v1");
			else{
				writelnt("ALOAD v0 SPILLEDARG " + Integer.toString(argCounter) );
				writelnt("MOVE s" + Integer.toString(sCounter) + " v0");         //na valw timh sto sCounter
				writelnt("PASSARG " + Integer.toString(argCounter+1) + " v1");
				sCounter++;
			}
			argCounter++;
			
		}
	}
	
	public void visit(HAllocate h){
		h.f1.accept(this);
		writelnt("MOVE v1 HALLOCATE " + lastReg);
		lastReg = "v1";
	}
	
	public void visit(Label l){
		if(!label_flag)
			write(l.f0.toString());
		else{
			writelnt("MOVE v1 " + l.f0.toString());
			lastReg = "v1";
		}
	}
	
	public void visit(Call c){
		argCounter = 0;
		sCounter = 0;
		call_flag = true;               // gia na 3erei o temp ti na kanei
		if(c.f3.present())
			c.f3.accept(this);
		call_flag = false;
		c.f1.accept(this);
		writelnt("CALL " + lastReg);
		lastReg = "v0";              //afou sto v0 epιstrefetai to apotelesma ths call
		
		for(int i=0;i<sCounter;i++){
			writelnt("MOVE v1 s" + Integer.toString(i));
			writelnt("ASTORE SPILLEDARG " + Integer.toString(4+i) + " v1");
		}
	
	}
	
	// apo call se call a mn xanontai pragmata kaka pragmata pl kala pragmata
}

