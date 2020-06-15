import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;

public class TypeCheckingVisitor extends GJDepthFirst<String, String> {
    String ThisClass;
    String ThisMethod;
    
    static public HashMap<String, ClassDeclarationInfo> classST = FillSymbolTableVisitor.classSymbolTable;      // Info from FillSymbolTableVisitor
    ArrayList<String> paramsList = new ArrayList<String>();                                                     // parameters List for method calls and declaration

    public void getClear() {
        paramsList.clear();
        ThisClass = null;
        ThisMethod = null;
    }

    
    public String definedClassVariable(String variable) {                               // Variable defined as a Class Field
        if (!classST.containsKey(ThisClass))
            return null;
        if (classST.get(ThisClass).varDeclarations.containsKey(variable))
            return classST.get(ThisClass).varDeclarations.get(variable);
        else
            return null;
    }

    public String definedClassMethodVariable(String variable) {                         // Variable defined as a Class Method Field
        if (!classST.containsKey(ThisClass))
            return null;
        if (!classST.get(ThisClass).methodDeclarations.containsKey(ThisMethod))
            return null;
        if (classST.get(ThisClass).methodDeclarations.get(ThisMethod).varDeclarations.containsKey(variable)) 
            return classST.get(ThisClass).methodDeclarations.get(ThisMethod).varDeclarations.get(variable);
        else
            return null;
    }

    static public String definedExtendedClassVariable(String className, String variable) {     // Variable defined as a Parent Class Field
        if (!classST.containsKey(className))
            return null;
        else {
            if (classST.containsKey(classST.get(className).ClassExtendsDeclarationName)) {
                String ExtendsClassName = classST.get(className).ClassExtendsDeclarationName;
                if (classST.get(ExtendsClassName).varDeclarations.containsKey(variable)) 
                    return classST.get(ExtendsClassName).varDeclarations.get(variable);
                else
                    return definedExtendedClassVariable(ExtendsClassName, variable);
            }
            else 
                return null;
        }
    }

    static public String definedClassMethod(String className, String method) {                 // Method defined inside Class
        if (classST.get(className).methodDeclarations.containsKey(method))
            return classST.get(className).methodDeclarations.get(method).MethodDeclarationType;
        else
            return null;
    }

    static public String definedExtendedClassMethod(String className, String method) {         // Method defined inside Parent Class
        if (!classST.containsKey(className))
            return null;
        else {
            if (classST.containsKey(classST.get(className).ClassExtendsDeclarationName)) {
                String ExtendsClassName = classST.get(className).ClassExtendsDeclarationName;
                if (classST.get(ExtendsClassName).methodDeclarations.containsKey(method)) 
                    return classST.get(ExtendsClassName).methodDeclarations.get(method).MethodDeclarationType;
                else
                    return definedExtendedClassMethod(ExtendsClassName, method);
            }
            else 
                return null;
        }
    }

    public boolean LeftNRightTypeComparison(String left, String right) {                // Checking Two Types, and for Classes if there is extended type 
        if (left.compareTo(right) == 0)
            return true;
        else if (classST.containsKey(right)) {
            if (classST.containsKey(classST.get(right).ClassExtendsDeclarationName)) {
                String ExtendsClassName = classST.get(right).ClassExtendsDeclarationName;
                return LeftNRightTypeComparison(left, ExtendsClassName);
            }
            else
                return false;
        }
        else
            return false;
    }

    public boolean checkMethodParameters(String ClassName, String MethodName) {         // Checking one by one Method parameters
        ArrayList<String> methodsparams = classST.get(ClassName).methodDeclarations.get(MethodName).parametersList;
        if (!paramsList.equals(methodsparams)) {
            if (paramsList.size() == methodsparams.size()) {
                boolean status = true;
                for (int i = 0; i < paramsList.size(); i++) {
                    if (!LeftNRightTypeComparison(methodsparams.get(i), paramsList.get(i))) {
                        status = false;
                        break;
                    }
                    else
                        status = true;
                }
                if (status == false){
                    if (classST.containsKey(classST.get(ClassName).ClassExtendsDeclarationName)) {
                        return checkMethodParameters(classST.get(ClassName).ClassExtendsDeclarationName, MethodName);
                    }
                    else 
                        return false;
                }
                else
                    return true;
            }
            else
                return false;
        }
        else
            return true;
    }
    public void checkSameExtendedMethod(String ClassName, String MethodName) throws Exception {     // Checking if exists method with same name in Parent Class and checking its parameters and return type
        if (classST.get(ClassName).methodDeclarations.containsKey(MethodName)) {
            String exMethType = classST.get(ClassName).methodDeclarations.get(MethodName).MethodDeclarationType;
            String thMethType = classST.get(ThisClass).methodDeclarations.get(ThisMethod).MethodDeclarationType;
            if (!LeftNRightTypeComparison(exMethType, thMethType)) {
                throw new Exception("MethodDeclaration: Parent's Class " + ClassName + " Method " + MethodName + " returns  " + exMethType + " and Child Class " + ThisClass + " Method " + ThisMethod + " returns " + thMethType);
            }
            if (classST.get(ThisClass).methodDeclarations.get(ThisMethod).parametersList.size() != 0) {
                paramsList = classST.get(ThisClass).methodDeclarations.get(ThisMethod).parametersList;
                if (!checkMethodParameters(ClassName, MethodName)) {
                    
                    throw new Exception("MethodDeclaration: Parent's Class " + ClassName + " Method " + MethodName + " and Child Class " + ThisClass + " Method " + ThisMethod + " have differences in FormalParameters.");
                }
            }
        }
        if (classST.containsKey(classST.get(ClassName).ClassExtendsDeclarationName)) {
            checkSameExtendedMethod(classST.get(ClassName).ClassExtendsDeclarationName, MethodName);
        }
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
    public String visit(MainClass n, String argu) throws Exception {    // ok
        String _ret="null2";
        ThisClass = n.f1.f0.toString();
        n.f0.accept(this, argu);
        _ret = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        ThisMethod = "main";
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        n.f13.accept(this, argu);
        n.f14.accept(this, argu);
        n.f15.accept(this, argu);
        n.f16.accept(this, argu);
        n.f17.accept(this, argu);
        
        ThisMethod = null;
        ThisClass = null;
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
    public String visit(ClassDeclaration n, String argu) throws Exception {     // ok
        String _ret="null2";
        ThisClass = n.f1.f0.toString();
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        if (n.f3.present()) {
            n.f3.accept(this, argu);
        }
        if (n.f4.present()) {
            n.f4.accept(this, argu);
        }
        n.f5.accept(this, argu);
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
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {  // ok
        String _ret=null;
        ThisClass = n.f1.f0.toString();
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        if (n.f5.present()) {           // if exists call it
            n.f5.accept(this, argu);
        }
        if (n.f6.present()) {
            n.f6.accept(this, argu);
        }
        n.f7.accept(this, argu);
        ThisClass = null;
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
        String _ret=null;
        ThisMethod = n.f2.f0.toString();
        String methodType = classST.get(ThisClass).methodDeclarations.get(ThisMethod).MethodDeclarationType;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        if (n.f4.present()) {
            n.f4.accept(this, argu);
        }
        if ( classST.get(ThisClass).ClassExtendsDeclarationName != null) {                              // check for parent same method
            checkSameExtendedMethod(classST.get(ThisClass).ClassExtendsDeclarationName, ThisMethod);
        }
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        if (n.f7.present()) {
            n.f7.accept(this, argu);
        }
        if (n.f8.present()) {
            n.f8.accept(this, argu);
        }
        n.f9.accept(this, argu);
        String returnType = n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);

        if (!LeftNRightTypeComparison(methodType, returnType)) {            // checking return type
            throw new Exception("MethodDeclaration: " + ThisMethod + " return type " + returnType + " is different from method return type " + methodType + " .");
        }
        ThisMethod = null;
        return _ret;
    }

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, String argu) throws Exception {  // ok
        String _ret=null;
        String Id = n.f0.f0.toString();
        String IdType = n.f0.accept(this, argu);
        if (IdType == null) {
            throw new Exception("AssignmentStatement: Id " + Id + "not defined.");
        }
        n.f1.accept(this, argu);
        String ExType = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        if (!LeftNRightTypeComparison(IdType, ExType)) {
            throw new Exception("AssignmentStatement: Id Type: " + Id + " and Ex Type: " + ExType + " are different.");
        }
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
    public String visit(ArrayAssignmentStatement n, String argu) throws Exception { // ok
        String _ret=null;
        String Id = n.f0.toString();
        String IdType = n.f0.accept(this, argu);
        if (!IdType.equals("int[]")) {
            throw new Exception("ArrayAssignmentStatement: Id " + Id + " not defined as int[].");
        }
        n.f1.accept(this, argu);
        String exType = n.f2.accept(this, argu);
        if (!exType.equals("int")) {
            throw new Exception("ArrayAssignmentStatement: Arrays[] " + Id + " index expression not defined as int.");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        exType = n.f5.accept(this, argu);
        if (!exType.equals("int")) {
            throw new Exception("ArrayAssignmentStatement: Arrays[] " + Id + " expression not defined as int.");
        }
        n.f6.accept(this, argu);
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
    public String visit(IfStatement n, String argu) throws Exception {      // ok
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        _ret = n.f2.accept(this, argu);
        if (_ret.compareTo("boolean") != 0) {
            throw new Exception("IfStatement: expr " + _ret + "not boolean.");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public String visit(WhileStatement n, String argu) throws Exception {   // ok
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        _ret = n.f2.accept(this, argu);
        if (_ret.compareTo("boolean") != 0) {
            throw new Exception("WhileStatement: expr " + _ret + "not boolean.");
        }
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
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
        _ret = n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        if (_ret.compareTo("int") != 0) {
            throw new Exception("PrintStatement: expr " + _ret + "not int.");
        }
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
    public String visit(AndExpression n, String argu) throws Exception {    // ok
        String _ret=null;
        String left = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String right = n.f2.accept(this, argu);
        if (left.compareTo("boolean") != 0) {
            throw new Exception("AndExpression: left " + left + " not int.");
        }
        else if (right.compareTo("boolean") != 0) {
            throw new Exception("AndExpression: rihgt " + right + " not int.");
        }
        else
            return "boolean";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, String argu) throws Exception {    // ok
        String _ret=null;
        String left = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String right = n.f2.accept(this, argu);
        if (left.compareTo("int") != 0) {
            throw new Exception("CompareExpression: left " + left + "not int.");
        }
        else if (right.compareTo("int") != 0) {
            throw new Exception("CompareExpression: rihgt " + right + "not int.");
        }
        else
            return "boolean";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, String argu) throws Exception {   // ok
        String _ret=null;
        String left = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String right = n.f2.accept(this, argu);
        if (left.compareTo("int") != 0) {
            throw new Exception("PlusExpression: left " + left + "not int.");
        }
        else if (right.compareTo("int") != 0) {
            throw new Exception("PlusExpression: rihgt " + right + "not int.");
        }
        else
            return "int";    
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, String argu) throws Exception {  // ok
        String _ret=null;
        String left = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String right = n.f2.accept(this, argu);
        if (left.compareTo("int") != 0) {
            throw new Exception("MinusExpression: left " + left + "not int.");
        }
        else if (right.compareTo("int") != 0) {
            throw new Exception("MinusExpression: rihgt " + right + "not int.");
        }
        else
            return "int";    
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, String argu) throws Exception {  // ok
        String _ret=null;
        String left = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        String right = n.f2.accept(this, argu);
        if (left.compareTo("int") != 0) {
            throw new Exception("TimesExpression: left " + left + "not int.");
        }
        else if (right.compareTo("int") != 0) {
            throw new Exception("TimesExpression: rihgt " + right + "not int.");
        }
        else
            return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, String argu) throws Exception {      // ok
        String _ret=null;
        _ret = n.f0.accept(this, argu);
        if (_ret.compareTo("int[]") != 0) {
            throw new Exception("ArrayLookup: Array PrimaryExpression " + _ret + " not int[].");
        }
        n.f1.accept(this, argu);
        _ret = null;
        _ret = n.f2.accept(this, argu);
        if (_ret.compareTo("int") != 0) {
            throw new Exception("ArrayLookup: Index PrimaryExpression " + _ret + " not int.");
        }
        n.f3.accept(this, argu);
        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public String visit(ArrayLength n, String argu) throws Exception {      // ok
        String _ret=null;
        _ret = n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        if (_ret.compareTo("int[]") != 0) {
            throw new Exception("ArrayLength: PrimaryExpression type " + _ret + " not int[].");
        }
        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public String visit(MessageSend n, String argu) throws Exception {  // ok
        String _ret=null;
        String prmEx = n.f0.accept(this, argu);
        if (prmEx == null) {
            throw new Exception("MessageSend: " + ThisClass + " " + ThisMethod + " " + prmEx + " not defined.");
        }
        n.f1.accept(this, argu);
        String Id = n.f2.f0.toString();
        String IdType = n.f2.accept(this, argu);
        if (definedClassMethod(prmEx, Id) == null) {        // if method has been declared/defined inisde calling class
            _ret = definedExtendedClassMethod(prmEx, Id);   // if method has been declared/defined inside parent calling class
            if (_ret == null) {
                throw new Exception("MessageSend: " + ThisClass + " " + ThisMethod + " " + Id + " is not method of class " + prmEx + " .");
            }
        }
        else
            _ret = classST.get(prmEx).methodDeclarations.get(Id).MethodDeclarationType; 
        n.f3.accept(this, argu);
        if (n.f4.present()) {
            n.f4.accept(this, argu);
            if (!checkMethodParameters(prmEx, Id)) {        // check calling parameters
                throw new Exception("MessageSend: Class " + prmEx + " in Method " + Id + " parameters not matching.");
            }
            paramsList.clear();
        }
        n.f5.accept(this, argu);
        return _ret;
    }
    
    /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
    public String visit(ExpressionList n, String argu) throws Exception {   // ok
        String _ret=null;
        String ExType = n.f0.accept(this, argu);
        paramsList = new ArrayList<String>();
        if (ExType != null) {
            paramsList.add(ExType);
        }
        n.f1.accept(this, argu);
        return _ret;
    }
  
    /**
    * f0 -> ","
    * f1 -> Expression()
    */
    public String visit(ExpressionTerm n, String argu) throws Exception {   // ok
        String _ret=null;
        n.f0.accept(this, argu);
        String ExType = n.f1.accept(this, argu);
        if (ExType != null) {
            paramsList.add(ExType);
        }
        return _ret;
    }
  
    /**
    * f0 -> NotExpression()
    *       | PrimaryExpression()
    */
    public String visit(Clause n, String argu) throws Exception {   // ok
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
    public String visit(PrimaryExpression n, String argu) throws Exception {    // ok
        String _ret = n.f0.accept(this, argu);
        if (_ret == null) {
            throw new Exception("PrimaryExpression: " + _ret + " not defined.");
        }
        return _ret;
    }

    /**
    * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, String argu) throws Exception {   // ok 
        n.f0.accept(this, argu);
        return "int";
    }

    /**
    * f0 -> "true"
    */
    public String visit(TrueLiteral n, String argu) throws Exception {      //  ok
        n.f0.accept(this, argu);
        return "boolean";
    }

    /**
    * f0 -> "false"
    */
    public String visit(FalseLiteral n, String argu) throws Exception {     // ok
        n.f0.accept(this, argu);
        return "boolean";
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, String argu) throws Exception {   // ok
        String _ret = null;
        String Id = n.f0.toString();
        _ret = definedClassMethodVariable(Id);
        if (_ret == null) {
            _ret = definedClassVariable(Id);
            if (_ret == null) {
                _ret = definedExtendedClassVariable(ThisClass, Id);
                if (_ret == null) {
                    return null;        // if Id not defined return null 
                }
            }
        }
        n.f0.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n, String argu) throws Exception {   // ok
        n.f0.accept(this, argu);
        return ThisClass;
    }

    /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(ArrayAllocationExpression n, String argu) throws Exception {    // ok
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        _ret = n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        if(_ret.compareTo("int") != 0) {
            throw new Exception("ArrayAllocationExpression: exType " + _ret + " not int.");
        }
        return "int[]";
    }

    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public String visit(AllocationExpression n, String argu) throws Exception {     // ok
        String _ret=null;
        _ret = n.f1.f0.toString();
        if ((_ret.compareTo("null") == 0) || (classST.get(_ret) == null)) {
            throw new Exception("AllocationExpression: " + _ret + " not declared.");
        }
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        return _ret;
    }

    /**
    * f0 -> "!"
    * f1 -> Clause()
    */
    public String visit(NotExpression n, String argu) throws Exception {    // ok
        String _ret=null;
        n.f0.accept(this, argu);
        _ret = n.f1.accept(this, argu);
        if (_ret.compareTo("boolean") != 0) {
            throw new Exception("NotExpression: Clause " + _ret + " after !.");
        }
        return _ret;
    }

    /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, String argu) throws Exception {    // ok
        String _ret = null;
        n.f0.accept(this, argu);
        _ret = n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return _ret;
    }


}
