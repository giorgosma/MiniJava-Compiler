import syntaxtree.*;
import visitor.*;
import java.io.*;
import java.util.*;

class Main {
	public static FillSymbolTableVisitor fillSymbolTableVisitor;
	public static TypeCheckingVisitor typeCheckingVisitor;
	public static OffsetVisitor offsetVisitor;
	public static LLVMVisitor llvmVisitor;
	public static boolean fillST;
	public static boolean typeCheck;
	public static BufferedWriter wr;
	public static File file;
	public static FileWriter fw;

  public static void main (String [] args){
		if(args.length < 1){
	    System.err.println("Usage: java Driver <inputFile>");
	    System.exit(1);
		}
		FileInputStream fis = null;
		for (int i = 0;  i < args.length; i++) {
			try{
				fis = new FileInputStream(args[i]);
				System.err.println("");
				MiniJavaParser parser = new MiniJavaParser(fis);
				System.err.println("Program parsed successfully.");
				fillST = false;
				fillSymbolTableVisitor = new FillSymbolTableVisitor();		
				fillSymbolTableVisitor.getClear();

				Goal root = parser.Goal();
				root.accept(fillSymbolTableVisitor, null);								// fill symbol Table
				fillST = true;
				//fillSymbolTableVisitor.printFillSymbolTableVisitor();		// debugging reasons
				typeCheck = false;
				typeCheckingVisitor = new TypeCheckingVisitor();
				typeCheckingVisitor.getClear();
				root.accept(typeCheckingVisitor, null);										// type checking
				typeCheck = true;
				System.out.println(args[i] + " type checked successfully.");
				
				offsetVisitor = new OffsetVisitor();
				offsetVisitor.getClear();
				root.accept(offsetVisitor, null);													// calculate offset
				offsetVisitor.printOffestVisitor();

				// create a .ll file under out folder for each file that checked succesfully 
				File file = new File("output/" + args[i].substring(args[i].lastIndexOf("/") + 1, args[i].lastIndexOf(".")) + ".ll");
				if (!file.exists()) {
					file.createNewFile();
				}
				fw = new FileWriter(file);
				wr = new BufferedWriter(fw);
				llvmVisitor = new LLVMVisitor(wr);
				root.accept(llvmVisitor, null);
				llvmVisitor.getClear();
				//llvmVisitor.printMethodsVTable();
			}
			catch(ParseException ex){
				System.out.println(ex.getMessage());
			}
			catch(FileNotFoundException ex){
				System.err.println(ex.getMessage());
			}
			catch(Exception ex){
				System.err.println(ex.getMessage());
				printExceptionInfo();				// for debugging reasons
			}
			finally{
				try{
					if(fis != null) fis.close();
				}
				catch(IOException ex){
					System.err.println(ex.getMessage());
				}

				try {
					if (wr != null) {
						wr.close();
					}
				}
				catch(Exception ex) {
					System.err.println(ex.getMessage());
				}
			}
		}
	}
	
	public static void printExceptionInfo() {			// for debugging reasons just avoid it
		if (!fillST) {
			if (fillSymbolTableVisitor != null) {
				System.out.println("ExceptionInfo Fill: Class " + fillSymbolTableVisitor.ThisClass);
				if (fillSymbolTableVisitor.ThisMethod != null)
				System.out.println("ExceptionInfo Fill: Method " + fillSymbolTableVisitor.ThisMethod);
			}
		}	
		else if (!typeCheck) {
			if (typeCheckingVisitor!= null) {
				System.out.println("ExceptionInfo Check: Class " + typeCheckingVisitor.ThisClass);
				if (typeCheckingVisitor.ThisMethod != null)
				System.out.println("ExceptionInfo Check: Method " + typeCheckingVisitor.ThisMethod);
			}
		}
	}
}
