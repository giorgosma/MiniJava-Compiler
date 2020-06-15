import java.util.*;

public class ClassDeclarationInfo {
    LinkedHashMap<String, String> varDeclarations = new LinkedHashMap<String, String>();                                    // Variable Name, type
    LinkedHashMap<String, MethodDeclarationInfo> methodDeclarations = new LinkedHashMap<String, MethodDeclarationInfo>();   // Method Name, type
    String ClassDeclarationName;
    String ClassExtendsDeclarationName;

    public ClassDeclarationInfo(String ClassDeclarationName, String ClassExtendsDeclarationName) {
        this.ClassDeclarationName = ClassDeclarationName;
        this.ClassExtendsDeclarationName = ClassExtendsDeclarationName;
    }

    public void printClassDeclarationInfo() {       // for debugging reasons just avoid it
        System.out.println();
        System.out.println("Class " + ClassDeclarationName);
        if (ClassExtendsDeclarationName != null) {
            System.out.println("Extends " + ClassExtendsDeclarationName);
        }
        for (String classname : varDeclarations.keySet()) {
            String classtype = varDeclarations.get(classname);
            System.out.println("    " + classtype + " " + classname);
        }
        for (String classname : methodDeclarations.keySet()) {
            methodDeclarations.get(classname).printMethodDeclarationInfo();
        }
    }
}