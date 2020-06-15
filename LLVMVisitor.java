import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;



import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.*;

public class LLVMVisitor extends GJDepthFirst<String, String> {
    
    private final BufferedWriter writer;

    private HashMap<String, ClassDeclarationInfo> classSymbolTable = FillSymbolTableVisitor.classSymbolTable;   // Info from FillSymbolTable
    private LinkedHashMap<String, ClassOffsetInfo> classOffsetTable = OffsetVisitor.classOffsetTable;           // Info from OffsetVisitor

    private int ThisVariable;
    private int ThisLabel;

    String ThisClass;
    String ThisMethod;

    String VTable;      // String for starting vtable for defining methods
    String messageType; // String for ClassType in MessageSend 

    private LinkedHashMap<String, String> methodsVTable = new LinkedHashMap<String, String>();              // methodName, className
    
    private ArrayList<String> exprList = new ArrayList<String>();       // List of expressions in calling functions MessageSend

    public void getClear() {
        ThisClass = null;
        ThisMethod = null;
        VTable = null;
        methodsVTable.clear();
        exprList.clear();
    }

    private void emit(String s) {       // write to file
        try {
            writer.write(s);
            writer.newLine();
        }
        catch(IOException ex) {
            System.out.println(ex.toString());
        }
    }

    private String newVariable() {
        this.ThisVariable++;
        return "%_" + (this.ThisVariable - 1);
    }

    private String newLabel() {
        this.ThisLabel++;
        return "if" + (this.ThisLabel - 1);
    }

    public String getMethodClass(String className, String methodName) {     // get method's ClassType
        if (!classSymbolTable.containsKey(className)) {
            return null;
        }
        if (!classSymbolTable.get(className).methodDeclarations.containsKey(methodName)) {
            if (!classSymbolTable.containsKey(classSymbolTable.get(className).ClassExtendsDeclarationName)) {
                return null;
            }
            else {
                return getMethodClass(classSymbolTable.get(className).ClassExtendsDeclarationName, methodName);
            }
        }
        else {
            return className;
        }
    }

    public String getMethodParameters(String className, String methodName) {        //   get a string with converted method type parameters
        if (!classSymbolTable.containsKey(className)) {
            return null;
        }
        if (!classSymbolTable.get(className).methodDeclarations.containsKey(methodName)) {
            if (!classSymbolTable.containsKey(classSymbolTable.get(className).ClassExtendsDeclarationName)) {
                return null;
            }
            else {
                return getMethodParameters(classSymbolTable.get(className).ClassExtendsDeclarationName, methodName);
            }
        }
        else {
            String params = getParameters(classSymbolTable.get(className).methodDeclarations.get(methodName).parametersList);
            return params;
        }
    }


    public int getMethodDeclarationOffset(String className, String methodName) {    // get methods offset
        if (!classOffsetTable.containsKey(className)) {
            return -1;
        }
        if (!classOffsetTable.get(className).MetOffsets.containsKey(methodName)) {
            if (!classOffsetTable.containsKey(classOffsetTable.get(className).extendsClassName)) {
                return -1;
            }
            else {
                return getMethodDeclarationOffset(classOffsetTable.get(className).extendsClassName, methodName);
            }
        }
        else {
            return classOffsetTable.get(className).MetOffsets.get(methodName);
        }
    }

    public int getVariableDeclarationOffset(String className, String variableName) {    // get variable offset
        if (!classOffsetTable.containsKey(className)) {
            return -1;
        }
        if (!classOffsetTable.get(className).VarOffsets.containsKey(variableName)) {
            if (!classOffsetTable.containsKey(classOffsetTable.get(className).extendsClassName)) {
                return -1;
            }
            else {
                return getVariableDeclarationOffset(classOffsetTable.get(className).extendsClassName, variableName);
            }
        }
        else {
            return classOffsetTable.get(className).VarOffsets.get(variableName);
        }
    }

    public String getVariableDeclarationType(String className, String variableName) {   // get variable's type
        if (!classSymbolTable.containsKey(className)) {
            return null;
        }
        if (!classSymbolTable.get(className).varDeclarations.containsKey(variableName)) {
            if (!classSymbolTable.containsKey(classSymbolTable.get(className).ClassExtendsDeclarationName)) {
                return null;
            }
            else {
                return getVariableDeclarationType(classSymbolTable.get(className).ClassExtendsDeclarationName, variableName);
            }
        }
        else {
            messageType = classSymbolTable.get(className).varDeclarations.get(variableName);
            return convertType(classSymbolTable.get(className).varDeclarations.get(variableName));
        }
    }

    public String convertType(String type) {    // convert type to llvm type
        if (type.equals("int")) {
            return "i32";
        }
        else if (type.equals("int[]")) {
            return "i32*";
        }
        else if (type.equals("boolean")) {
            return "i1";
        }
        else {
            return "i8*";
        }
    }

    public void fillMethodsVTable(String ClassName) {   // fill up with class's methods inheritated methods, if method is overiden keep last className
        if (classSymbolTable.containsKey(classSymbolTable.get(ClassName).ClassExtendsDeclarationName)) {
            fillMethodsVTable(classSymbolTable.get(ClassName).ClassExtendsDeclarationName); // parent methods
        }
        if (classOffsetTable.get(ClassName).methDeclOffset > 0)
            for (String method: classSymbolTable.get(ClassName).methodDeclarations.keySet()) {
                methodsVTable.put(method, ClassName);                                       // this class methods
                //System.out.println("    class " + ClassName + " method " + method);
            }
    }

    public void printMethodsVTable() {
        for (String meth: methodsVTable.keySet()) {
            System.out.println("    " + methodsVTable.get(meth) + " " + meth);
        }
    }

    public void generateVTable(String ClassName) {      // make vtable for class methods
        if (!classOffsetTable.containsKey(ClassName)) {
            //System.out.println("    MainClass");
            emit("@." + ClassName + "_vtable = global [0 x i8*] []");   // MainClass
            return;
        }
        if (classOffsetTable.get(ClassName).methDeclOffset == 0) {      // EmptyClass
            emit("@." + ClassName + "_vtable = global [0 x i8*] []");
            //System.out.println("    Empty");
        }
        else {          
            fillMethodsVTable(ClassName);
            VTable = "@." + ClassName + "_vtable = global [" + methodsVTable.size() + " x i8*] [";
            for (String str: methodsVTable.keySet()){       // for each method, calc its parameters
                String returnType = convertType(classSymbolTable.get(methodsVTable.get(str)).methodDeclarations.get(str).MethodDeclarationType);
                VTable += "i8* bitcast (" + returnType;
                String params = getParameters(classSymbolTable.get(methodsVTable.get(str)).methodDeclarations.get(str).parametersList);
                VTable += params + ")* @" + methodsVTable.get(str) + "." + str + " to i8*), "; 
            }
            VTable = VTable.substring(0, VTable.length() - 2) + "]" + VTable.substring(VTable.length() - 1);
            emit(VTable.substring(0, VTable.length() - 1));
            methodsVTable.clear();
        }

    }

    private String getParameters(ArrayList <String> list) {     // calc methods params in a string
        String params = " (i8*";
        for (int i = 0; i < list.size(); i++) {
            params += ",";
            if (i >= 1)
                params += " "; 
            String type = convertType(list.get(i));
            params += type;
        }
        return params;
    }

    private String printMethodDeclarationInfoParams(ArrayList <String> list) {  // avoid, just for debugging
        if (list.size() == 0)
            return "";
        String paramlist = "";
        for (int i = 0; i < list.size(); i++) {
            paramlist += list.get(i);
            if (list.size() > i + 1)
            paramlist += ",";
        }
        return paramlist;
    }

    public String definedClassMethodVariable(String variable) {         // Variable defined as a Class Method Field
        if (!classSymbolTable.containsKey(ThisClass))
            return null;
        if (!classSymbolTable.get(ThisClass).methodDeclarations.containsKey(ThisMethod)) 
            return null;
        if (classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).varDeclarations.containsKey(variable)) 
            return classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).varDeclarations.get(variable);
        else
            return null;
    }

    public LLVMVisitor(BufferedWriter wr) {     // constructor, for making vtable's defining methods
        this.writer = wr;
        for (String classST: classSymbolTable.keySet()) {
            VTable = "";
            generateVTable(classST);
            //System.out.println("class " + classST);
            //printMethodsVTable();
            //methodsVTable.clear();
        }
        emit("\n");
        emit(   "declare i8* @calloc(i32, i32)\n" +
                "declare i32 @printf(i8*, ...)\n" +
                "declare void @exit(i32)\n" +
                "\n" +
                "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n" +
                "@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n" +
                "define void @print_int(i32 %i) {\n" +
                "\t%_str = bitcast [4 x i8]* @_cint to i8*\n" +
                "\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n" +
                "\tret void\n" +
                "}\n" +
                "\n" +
                "define void @throw_oob() {\n" +
                "\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n" +
                "\tcall i32 (i8*, ...) @printf(i8* %_str)\n" +
                "\tcall void @exit(i32 1)\n" +
                "\tret void\n" +
                "}\n" 
                );
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
    public String visit(MainClass n, String argu) throws Exception {
        String _ret=null;
        ThisClass = n.f1.f0.toString();
        ThisMethod = "main";

        emit("define i32 @" + ThisMethod + "() {");
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        emit("\t" + "ret i32 0");
        emit("}");

        ThisClass = null;
        ThisMethod = null;
        ThisVariable = 0;
        ThisLabel = 0;
        return _ret;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    public String visit(ClassDeclaration n, String argu) throws Exception {
        String _ret=null;
        ThisClass = n.f1.f0.toString();
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        ThisClass = null;
        return _ret;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {
        String _ret=null;
        ThisClass = n.f1.f0.toString();
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        ThisClass = null;
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public String visit(VarDeclaration n, String argu) throws Exception {   // ok ?
        String _ret = null;
        if (ThisMethod == null) // Class Variables
            return null;
        String type = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        String Id = n.f1.f0.toString();
        emit("\t%" + Id + " = alloca " + type);
        emit("");
        return _ret;
    }

    /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
    public String visit(MethodDeclaration n, String argu) throws Exception {
        emit("");
        String _ret=null;
        String type = n.f1.accept(this, argu);
        ThisMethod = n.f2.f0.toString();
        _ret = "define " + type + " @" + ThisClass + "." + ThisMethod;
        _ret += "(i8* %this";
        if (n.f4.present()) {   // if has parameters
            _ret += ", ";
            _ret += n.f4.accept(this, argu);
        }
        _ret += ") {";
        emit(_ret);
        _ret = "";
        ArrayList<String> paramList = classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).parametersList;
        int i = 0;
        // allocate parameters
        for (String varName : classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).varDeclarations.keySet()) {
            if (i >= paramList.size())
                break;
            String allocatype = convertType(paramList.get(i));
            emit("\t" + "%" + varName + " = " + "alloca " + allocatype);
            emit("\t" + "store " + allocatype + " %." + varName + ", " + allocatype + "*" + " %" + varName);
            i++;
        }
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        _ret = n.f10.accept(this, "load");
        emit("\t" + "ret " + type + " " + _ret);
        emit("}");
        ThisMethod = null;
        ThisVariable = 0;
        ThisLabel = 0;
        return _ret;
    }


    /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
    public String visit(FormalParameterList n, String argu) throws Exception {  // calc method parameters - fake call
        String _ret = "";
        ArrayList<String> paramList = classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).parametersList;
        int i = 0;
        for (String varName : classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).varDeclarations.keySet()) {
            if (i >= paramList.size())
                break;
            String type = convertType(paramList.get(i));
            //System.out.println(type + " " + varName);
            _ret += type + " %." + varName;
            if (i < (paramList.size() - 1))
                _ret += ", ";
            i++;
        }
        // n.f0.accept(this, argu);
        // n.f1.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    public String visit(FormalParameter n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> ( FormalParameterTerm() )*
    */
    public String visit(FormalParameterTail n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
    public String visit(FormalParameterTerm n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    public String visit(Type n, String argu) throws Exception {         // ok
        //return n.f0.accept(this, argu);
        String type = n.f0.accept(this, argu);
        if (type.startsWith("%"))
            type = "i8*";
        return type;
    }

    /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    public String visit(ArrayType n, String argu) throws Exception {    // ok
        return "i32*";
    }

    /**
    * f0 -> "boolean"
    */
    public String visit(BooleanType n, String argu) throws Exception {  // ok
        return "i1";
    }

    /**
    * f0 -> "int"
    */
    public String visit(IntegerType n, String argu) throws Exception {  // ok
        return "i32";
    }

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, String argu) throws Exception {  
        String _ret=null;
        String id = n.f0.f0.toString();
        String type = definedClassMethodVariable(id);
        if (type != null) {                             // allocated inside method
            String expr = n.f2.accept(this, "load");
            emit("\t" + "store " + convertType(type) + " " + expr + ", " + convertType(type) + "* " + "%" + id);
        }
        else {                                          // allocated as class variable
            int varOffset = getVariableDeclarationOffset(ThisClass, id);
            varOffset += 8;
            type = getVariableDeclarationType(ThisClass, id);
            String register0 = newVariable();
            String register1 = newVariable();
            String expr = n.f2.accept(this, "load");
            emit("\t" + register0 + " = getelementptr i8, i8* %this, i32 " + varOffset);
            emit("\t" + register1 + " = bitcast i8* " + register0 + " to " + type + "*");
            emit("\t" + "store " + type + " " + expr + ", " + type + "* " + register1);
        }
        emit("");
        return _ret;
    }

    /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
    public String visit(ArrayAssignmentStatement n, String argu) throws Exception {
        String _ret=null;
        String id = n.f0.accept(this, "load");
        String expr1 = n.f2.accept(this, "load");
        String expr2 = n.f5.accept(this, "load");
        
        String register0 = newVariable();
        String register1 = newVariable();
        String register3 = newVariable();
        emit("\t" + register0 + " = add i32 " + expr1 + ", 1");
        emit("\t" + register1 + " = getelementptr i32, i32* " + id + ", i32 " + register0);
        emit("\t" + "store i32 " + expr2 + ", i32* " + register1);
        return _ret;
    }

    /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
    public String visit(IfStatement n, String argu) throws Exception {
        String _ret=null;
        String label1 = newLabel();
        String label2 = newLabel();
        String label3 = newLabel();

        String registerExpr = n.f2.accept(this, "load");
        emit("\t" + "br i1 " + registerExpr + ", label %" + label1 + ", label %" + label2);
        emit("");

        emit(label1 + ":");
        n.f4.accept(this, argu);
        emit("\t" + "br label %" + label3);
        emit("");
        
        emit(label2 + ":");
        n.f6.accept(this, argu);
        emit("\t" + "br label %" + label3);
        emit("");
        
        emit(label3 + ":");
        emit("");
        return _ret;
    }

    /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public String visit(WhileStatement n, String argu) throws Exception {
        String _ret=null;
        String labelStart = newLabel();
        String labelEnd = newLabel();
        String labelNext = newLabel();

        emit("\t" + "br label %" + labelStart);
        emit(labelStart + ":");
        
        String registerExpr = n.f2.accept(this, "load");
        emit("\t" + "br i1 " + registerExpr + ", label %" + labelNext + ", label %" + labelEnd);
        
        emit(labelNext + ":");
        n.f4.accept(this, argu);
        emit("\t" + "br label %" + labelStart);

        emit(labelEnd + ":");
        emit("");
        return _ret;
    }

    /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public String visit(PrintStatement n, String argu) throws Exception {   // ok
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String register0 = n.f2.accept(this, "load");
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        emit("\t" + "call void (i32) @print_int(i32 " + register0 + ")");
        return _ret;
    }

    /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | Clause()
    */
    public String visit(Expression n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    public String visit(AndExpression n, String argu) throws Exception {    // I saw likedlist example phi and taked it
        String _ret=null;

        String registerClause1 = n.f0.accept(this, "load");
        String label0 = newLabel();
        String label1 = newLabel();
        String label2 = newLabel();
        String label3 = newLabel();

        emit("\t" + "br label %" + label0);
        emit("");

        emit(label0 + ":");
        emit("\t" + "br i1 " + registerClause1 + ", label %" + label1 + ", label %" + label3);
        emit("");

        emit(label1 + ":");
        String registerClause2 = n.f2.accept(this, "load");
        emit("\t" + "br label %" + label2);

        emit(label2 + ":");
        emit("\t" + "br label %" + label3);

        emit(label3 + ":");

        String register0 = newVariable();
        emit("\t" + register0 + " = phi i1 [0, %" + label0 + " ], [ " + registerClause2 + ", %" + label2 + " ]");
        return register0;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, String argu) throws Exception {
        String _ret=null;
        String register0 = newVariable();
        String prEx1 = n.f0.accept(this, "load");
        String prEx2 = n.f2.accept(this, "load");
        emit("\t" + register0 + " = icmp slt i32 " + prEx1 + ", " + prEx2);
        return register0;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, String argu) throws Exception {
        String _ret=null;
        String register0 = newVariable();
        String prEx1 = n.f0.accept(this, "load");
        String prEx2 = n.f2.accept(this, "load");
        emit("\t" + register0 + " = add i32 " + prEx1 + ", " + prEx2);
        return register0;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, String argu) throws Exception {
        String _ret=null;
        String register0 = newVariable();
        String prEx1 = n.f0.accept(this, "load");
        String prEx2 = n.f2.accept(this, "load");
        emit("\t" + register0 + " = sub i32 " + prEx1 + ", " + prEx2);
        return register0;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, String argu) throws Exception {
        String _ret=null;
        String register0 = newVariable();
        String prEx1 = n.f0.accept(this, "load");
        String prEx2 = n.f2.accept(this, "load");
        emit("\t" + register0 + " = mul i32 " + prEx1 + ", " + prEx2);
        return register0;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, String argu) throws Exception {
        String _ret=null;
        String prEx1 = n.f0.accept(this, "load");
        String prEx2 = n.f2.accept(this, "load");

        String register0 = newVariable();
        String register1 = newVariable();
        String register2 = newVariable();
        emit("\t" + register0 + " = add i32 " + prEx2 + ", 1");
        emit("\t" + register1 + " = getelementptr i32, i32* " + prEx1 + ", i32 " + register0);
        emit("\t" + register2 + " = load i32, i32* " + register1);
        emit("");
        return register2;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public String visit(ArrayLength n, String argu) throws Exception {      
        String _ret=null;
        String idArray = n.f0.accept(this, "load");
        String register0 = newVariable();
        String register1 = newVariable();
        emit("\t" + register0 + " = getelementptr i32, i32* " + idArray + ", i32 0");   // arra[0] = length
        emit("\t" + register1 + " = load i32, i32* " + register0);
        return register1;
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public String visit(MessageSend n, String argu) throws Exception {
        String _ret=null;
        String prmEx = n.f0.accept(this, "load");   // load expression
        
        String id = n.f2.accept(this, null);
        id = id.replaceFirst("%", "");              // take method's REAL name

        String register0 = newVariable();
        String register1 = newVariable();

        emit("\t" + register0 + " = bitcast i8* " + prmEx + " to i8***");
        emit("\t" + register1 + " = load i8**, i8*** " + register0);
        
        String prmExType = messageType;            // defined from identifier, this, allocationex
        int methodOffset = getMethodDeclarationOffset(prmExType, id);   // get method offset
        String methodType = TypeCheckingVisitor.definedClassMethod(prmExType, id);      // method inside class
        if (methodType == null) {
            methodType = TypeCheckingVisitor.definedExtendedClassMethod(prmExType, id); // method from parent class
        }
        String params = getMethodParameters(prmExType, id);                             // get string with method params
        params += ")*";
        String register2 = newVariable();
        String register3 = newVariable();
        String register4 = newVariable();

        emit("\t" + register2 + " = getelementptr i8*, i8** " + register1 + ", i32 " + methodOffset/8);
        emit("\t" + register3 + " = load i8*, i8** " + register2);
        emit("\t" + register4 + " = bitcast i8* " + register3 + " to " + convertType(methodType) + " " + params);

        n.f4.accept(this, argu);                            // generate list with expressions, if exists

        String className = getMethodClass(prmExType, id);   // get class that method has been defined
        // get list of method's parameters types
        ArrayList<String> paramList = classSymbolTable.get(className).methodDeclarations.get(id).parametersList;
        if (exprList.size() != paramList.size())            // avoid, just for debugging
            System.out.println("ERROR: DIFF exprLIST - PARAMLIST");
        String methArguments = "";
        for (int i = 0; i < exprList.size(); i++) {         // string of tuples, paramType exprValue ...
            methArguments += ", " + convertType(paramList.get(i)) + " " + exprList.get(i);
        }

        String register5 = newVariable();
        emit("\t" + register5 + " = call " + convertType(methodType) + " " + register4 + "(i8* " + prmEx + methArguments + ")");
        exprList.clear();   // clear list of calling expressions
        return register5;
    }

    /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
    public String visit(ExpressionList n, String argu) throws Exception {
        String _ret=null;
        String expr = n.f0.accept(this, "load");
        exprList.add(expr);     // add expression to a list of expressions of this call
        n.f1.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> ","
    * f1 -> Expression()
    */
    public String visit(ExpressionTerm n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        String expr = n.f1.accept(this, "load");
        exprList.add(expr);
        return _ret;
    }

    /**
    * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
    public String visit(Clause n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | BracketExpression()
    */
    public String visit(PrimaryExpression n, String argu) throws Exception {
        return n.f0.accept(this, argu);
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, String argu) throws Exception {
        String value = n.f0.toString();
        return value;
    }

    /**
    * f0 -> "true"
    */
    public String visit(TrueLiteral n, String argu) throws Exception {
        //return n.f0.accept(this, argu);
        return "1";
    }

    /**
    * f0 -> "false"
    */
    public String visit(FalseLiteral n, String argu) throws Exception {
        //return n.f0.accept(this, argu);
        return "0";
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, String argu) throws Exception {
        if (argu != null && (argu.compareTo("load") == 0)) {    // called from above expression, to load it
            String id = n.f0.toString();
            String type = definedClassMethodVariable(id);   
            if (type != null) {                                 // local method variable
                messageType = type;
                String register0 = newVariable();
                emit("\t" + register0 + " = load " + convertType(type) + ", " + convertType(type) + "* %" + id);
                return register0;
            }
            else {                                              // class Variable
                int varOffset = getVariableDeclarationOffset(ThisClass, id);    // variable's offset 
                String classType = getVariableDeclarationType(ThisClass, id);   // get variable's type,  messageType inside getVariableDeclaration
                // if (classType != null)
                //     System.out.println("RETURN FROM GET..: " + id + " " + classType + " -> " + ThisMethod + ", " + varOffset);
                // else
                //     System.out.println("RETURN FROM GET..: NULL!");
                //messageType = classType;
                varOffset += 8;
                String register0 = newVariable();
                String register1 = newVariable();
                String register2 = newVariable();
                emit("\t" + register0 + " = getelementptr i8, i8* %this, i32 " + varOffset);
                emit("\t" + register1 + " = bitcast i8* " + register0 + " to " + classType + "*");
                emit("\t" + register2 + " = load " + classType + ", " + classType + "* " + register1);
                return register2;
            }
        }
        else    // not from expression call, just return the name in llvm type
            return "%" + n.f0.toString();
    }

    /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n, String argu) throws Exception {
        messageType = ThisClass;    // ClassType for message type call
        return "%this";
    }

    /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(ArrayAllocationExpression n, String argu) throws Exception {
        String _ret=null;
        String expr = n.f3.accept(this, "load");    // load array's index into a register
        String register0 = newVariable();
        String register1 = newVariable();
        String register2 = newVariable();
        emit("\t" + register0 + " = add i32 " + expr + ", 1");
        emit("\t" + register1 + " = call i8* @calloc(i32 4, i32 " + register0 + ")");
        emit("\t" + register2 + " = bitcast i8* " + register1 + " to i32*");
        emit("\t" + "store i32 " + expr + ", i32* " + register2);
        return register2;
    }

    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public String visit(AllocationExpression n, String argu) throws Exception { 
        String _ret=null;
        n.f0.accept(this, argu);
        String id = n.f1.f0.toString();
        messageType = id.replaceFirst("%", "");                             // change the return of identifier to REAL TYPE
        int vtableNumMethods = classOffsetTable.get(id).methDeclOffset/8;   // index of method in vtable of methods
        int size = classOffsetTable.get(id).varDeclOffset;                  // variable's offset in vtable
        size += 8;
        String register0 = newVariable();
        emit("\t" + register0 + " = call i8* @calloc(i32 1, i32 " + size + ")");
        String register1 = newVariable();
        emit("\t" + register1 + " = bitcast i8* " + register0 + " to i8***");
        String register2 = newVariable();
        emit("\t" + register2 + " = getelementptr [" + vtableNumMethods + " x i8*], [" + vtableNumMethods + " x i8*]* @." + id + "_vtable, i32 0, i32 0");
        emit("\t" + "store i8** " + register2 + ", i8*** " + register1);
        
        //n.f2.accept(this, argu);
        //n.f3.accept(this, argu);
        return register0;
    }

    /**
    * f0 -> "!"
    * f1 -> Clause()
    */
    public String visit(NotExpression n, String argu) throws Exception {
        String _ret = null;
        _ret = n.f1.accept(this, argu);
        String register = newVariable();
        emit("\t" + register + " = xor i1 1, " + _ret);     // saw linked list example and taked it
        return register;
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        _ret = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }

}