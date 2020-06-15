import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;

public class FillSymbolTableVisitor extends GJDepthFirst<String, String> {
    String ThisClass;
    String ThisMethod;

    static public HashMap<String, ClassDeclarationInfo> classSymbolTable = new LinkedHashMap<String, ClassDeclarationInfo>(); 

    public void getClear() {
        classSymbolTable.clear();
        ThisClass = null;
        ThisMethod = null;
    }

    public void printFillSymbolTableVisitor() {                 //// for debugging reasons just avoid it
        for (String classname : classSymbolTable.keySet()) {
            classSymbolTable.get(classname).printClassDeclarationInfo();
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
    public String visit(MainClass n, String argu) throws Exception {
        String _ret=null;
        ThisClass = n.f1.f0.toString();
        ThisMethod = n.f6.toString();
        ClassDeclarationInfo classDeclarationInfo = new ClassDeclarationInfo(ThisClass, null);
        MethodDeclarationInfo methodDeclarationInfo = new MethodDeclarationInfo(ThisMethod, "void");
        classSymbolTable.put(ThisClass, classDeclarationInfo);
        methodDeclarationInfo.varDeclarations.put(n.f11.f0.toString(),  "String[]");
        methodDeclarationInfo.parametersList.add("String[]");
        classSymbolTable.get(ThisClass).methodDeclarations.put(ThisMethod, methodDeclarationInfo);

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
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

        ThisClass = null;
        ThisMethod = null;
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
        if (classSymbolTable.containsKey(ThisClass)) {
            throw new Exception("ClassDeclaration -> class " + ThisClass + " declerated before.");
        }
        ClassDeclarationInfo classDeclarationInfo = new ClassDeclarationInfo(ThisClass, null);
        classSymbolTable.put(ThisClass, classDeclarationInfo);

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
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
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {
        String _ret=null;
        ThisClass = n.f1.f0.toString();
        if (classSymbolTable.containsKey(ThisClass)) {
            throw new Exception("ClassDeclaration -> class " + ThisClass + " declerated before.");
        }
        ClassDeclarationInfo classDeclarationInfo = new ClassDeclarationInfo(ThisClass, n.f3.f0.toString());
        classSymbolTable.put(ThisClass, classDeclarationInfo);

        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        
        ThisClass = null;        
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    public String visit(VarDeclaration n, String argu) throws Exception {
        String _ret=null;
        String Type;
        Type = n.f0.accept(this, argu);
        String Id; 
        Id = n.f1.f0.toString();
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        if (ThisClass != null) {
            if (ThisMethod != null) {
                if (classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).varDeclarations.containsKey(Id)) {
                    throw new Exception("Method " + ThisMethod + " VarDeclaration " + Type + " " + Id + " declerated before.");
                }
                else
                    classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).varDeclarations.put(Id, Type);
            }
            else {
                if (classSymbolTable.get(ThisClass).varDeclarations.containsKey(Id))
                    throw new Exception(ThisClass + " VarDecleration " + Type + " " + Id + " declerated before.");
                else
                    classSymbolTable.get(ThisClass).varDeclarations.put(Id, Type);
            }
        }
        else 
            throw new Exception();
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
        String type = n.f1.accept(this, argu);
        ThisMethod = n.f2.f0.toString();
        MethodDeclarationInfo methodDeclarationInfo = new MethodDeclarationInfo(ThisMethod, type);
        if (classSymbolTable.get(ThisClass).methodDeclarations.containsKey(ThisMethod))
            throw new Exception("MethodDeclaration -> method " + ThisMethod + " declerated before.");
        classSymbolTable.get(ThisClass).methodDeclarations.put(ThisMethod, methodDeclarationInfo);

        n.f0.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, argu);
        n.f7.accept(this, argu);
        n.f8.accept(this, argu);
        n.f9.accept(this, argu);
        n.f10.accept(this, argu);
        n.f11.accept(this, argu);
        n.f12.accept(this, argu);
        ThisMethod = null;
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    public String visit(FormalParameter n, String argu) throws Exception {
        String _ret=null;
        String Type;
        String Id;
        Type = n.f0.accept(this, argu);
        n.f1.accept(this, argu); 
        Id = n.f1.f0.toString();
        if (ThisClass != null && ThisMethod != null) {
            classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).varDeclarations.put(Id, Type);
            classSymbolTable.get(ThisClass).methodDeclarations.get(ThisMethod).parametersList.add(Type);
        }
        return _ret;
    }

    /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    public String visit(ArrayType n, String argu) throws Exception {
        String _ret=null;
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        return "int[]";
    }

    /**
    * f0 -> "boolean"
    */
    public String visit(BooleanType n, String argu) throws Exception {
        n.f0.accept(this, argu);
        return "boolean";
    }

    /**
    * f0 -> "int"
    */
    public String visit(IntegerType n, String argu) throws Exception {
        n.f0.accept(this, argu);
        return  "int";
    }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, String argu) throws Exception {
        n.f0.accept(this, argu);
        return n.f0.toString();
    }

}
