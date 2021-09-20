import syntaxtree.*;
import visitor.*;
import java.util.*;

public class methArgVisitor extends DepthFirstVisitor{

// --------------  GLOBAL VARIABLES  -------------------
	private HashMap <String,Integer> stack_num_map;
	private HashMap <String,Integer> max_arg_map;
	private String lastMeth;  // exei to onoma ths synarthshs pou vriskomaste 
	private int argCounter;		//exei ta currnet orismata
	private int maxMethArg;		// exei ton megisto ari8mo orimsatwm
	//private List <String> temp_list;	//exei to poia temps exoume synanthsei wste na mn au3anoume ama synantame to idio temp
	private int tempCounter;
	

// - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public methArgVisitor(){  // Constructor
		stack_num_map = new HashMap<String,Integer>();
		max_arg_map = new HashMap<String,Integer>();
		lastMeth = null;   // 
		tempCounter= 0;  
		argCounter = 0;
		maxMethArg =0;
	}

// ---------------  return maps  -----------------------
	public HashMap <String,Integer> get_stack_num_map(){
		return stack_num_map; 
	}
	public HashMap <String,Integer> get_max_arg_map(){
		return max_arg_map; 
	}
// - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	public void visit(Goal g){   // gia th main
		lastMeth = "MAIN";
		argCounter = maxMethArg = 0;
		
		g.f1.accept(this);
		max_arg_map.put(lastMeth,maxMethArg);
		stack_num_map.put(lastMeth,argCounter+1);//temp_list.size());
		if(g.f3.present())
			g.f3.accept(this);
	}

	public void visit(Procedure pr){  // gia kainouria synarthsh
		pr.f0.accept(this);
		lastMeth = pr.f0.f0.toString();  
		maxMethArg  = 0;
		//temp_list = new ArrayList<String>();
		pr.f4.accept(this);
	}

	
	public void visit(StmtExp se){
		se.f1.accept(this);
		se.f3.accept(this);
		max_arg_map.put(lastMeth,maxMethArg);
		stack_num_map.put(lastMeth,argCounter+1);
		
	}
	
	public void visit(Temp t){
		tempCounter++;
		if(Integer.parseInt(t.f1.f0.toString()) > argCounter)
			argCounter = Integer.parseInt(t.f1.f0.toString());
	}

	public void visit(Call c){
		tempCounter =0;
		if(c.f3.present())
			c.f3.accept(this);
		if(tempCounter > maxMethArg)
			maxMethArg = tempCounter;
	
	}


}

