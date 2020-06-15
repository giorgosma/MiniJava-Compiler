import java.util.*;

public class MethodDeclarationInfo {
    LinkedHashMap<String, String> varDeclarations = new LinkedHashMap<String, String>();        // Variables of the method, and parameters
    ArrayList<String> parametersList = new ArrayList<String>();                                 // List of parameters types with  insertion order
    String MethodDeclarationType;
    String MethodDeclarationName;

    public MethodDeclarationInfo(String MethodDeclarationName, String MethodDeclarationType) {
        this.MethodDeclarationType = MethodDeclarationType;
        this.MethodDeclarationName = MethodDeclarationName;
    }

    public void printMethodDeclarationInfo() {                                              // for debugging reasons just avoid it
        System.out.println();
        System.out.println("    " +  MethodDeclarationType + " " + MethodDeclarationName + "(" + printMethodDeclarationInfoParams() + ")");
        for (String classname : varDeclarations.keySet()) {
            String classtype = varDeclarations.get(classname);
            System.out.println("        " + classtype + " " + classname);
        }
    }

    public String printMethodDeclarationInfoParams() {
        String paramlist = "";
        for (int i = 0; i < parametersList.size(); i++) {
            paramlist += parametersList.get(i);
            if (parametersList.size() > i + 1)
            paramlist += ", ";
        }
        return paramlist;
    }
}