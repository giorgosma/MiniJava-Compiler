import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.*;

public class OffsetVisitor extends GJDepthFirst<String, String> {
    String ThisClass;
    String ThisMethod;

    static public LinkedHashMap<String, ClassOffsetInfo> classOffsetTable = new LinkedHashMap<String, ClassOffsetInfo>();   // Classes Offset Info 
    private HashMap<String, ClassDeclarationInfo> classOffsetST = FillSymbolTableVisitor.classSymbolTable;                  //  Information from The HashMap of FillSymbolTable
    
    public void getClear() {
        classOffsetTable.clear();
        ThisClass = null;
        ThisMethod = null;
    }

    public void printOffestVisitor() {
            for (String name : classOffsetTable.keySet()) {
                classOffsetTable.get(name).printClassOffsetInfo();
            }
    }
    public boolean sameMethodInExtendedClass(String ClassName, String MethodName) {         // check if the same method has been declared in parent class
        if (classOffsetST.get(ClassName).methodDeclarations.containsKey(MethodName)) {
            return true;
        }
        else {
            if (classOffsetST.containsKey(classOffsetST.get(ClassName).ClassExtendsDeclarationName))
                return sameMethodInExtendedClass(classOffsetST.get(ClassName).ClassExtendsDeclarationName, MethodName);
            else
                return false;
        }
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
        ClassOffsetInfo classOffsetInfo = new ClassOffsetInfo(ThisClass, null);
        classOffsetTable.put(ThisClass, classOffsetInfo);

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
        String extendsClassName = n.f3.f0.toString();
        ClassOffsetInfo classOffsetInfo;
        if (classOffsetTable.containsKey(extendsClassName))     // if extended class is NOT MainClass
            classOffsetInfo = new ClassOffsetInfo(ThisClass, extendsClassName, classOffsetTable.get(extendsClassName).varDeclOffset, classOffsetTable.get(extendsClassName).methDeclOffset);
        else                                                    // if extended class is MainClass
            classOffsetInfo = new ClassOffsetInfo(ThisClass, null);
        classOffsetTable.put(ThisClass, classOffsetInfo);
        

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
        String Id = n.f1.f0.toString();
        if (ThisClass != null && ThisMethod == null) {          // if Variable is inside Class and not inside a method
            classOffsetTable.get(ThisClass).calcVariableOffset(Id, classOffsetST.get(ThisClass).varDeclarations.get(Id));
        }
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
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
        if (ThisClass != null) {                // method is from a Class and NOT from MainClass
            if (classOffsetST.containsKey(classOffsetST.get(ThisClass).ClassExtendsDeclarationName)) {
                if (!sameMethodInExtendedClass(classOffsetST.get(ThisClass).ClassExtendsDeclarationName, ThisMethod)) {     // check if there is parent class and parent has the same method
                    classOffsetTable.get(ThisClass).calcMethodOffset(ThisMethod);
                }
            }
            else
                classOffsetTable.get(ThisClass).calcMethodOffset(ThisMethod);
        }
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

}
