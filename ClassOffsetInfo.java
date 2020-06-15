import java.util.*;

public class ClassOffsetInfo {
    String className;
    String extendsClassName;
    Integer methDeclOffset;                                                                 // counter for method offsets
    Integer varDeclOffset;                                                                  // counter for variable offsets
    LinkedHashMap<String, Integer> VarOffsets = new LinkedHashMap<String, Integer>();       // varName , offset
    LinkedHashMap<String, Integer> MetOffsets = new LinkedHashMap<String, Integer>();       // methName, offset


    public ClassOffsetInfo(String className, String classExtendsName) {                     // constructor for ClassDeclaration
        this.className = className;
        this.extendsClassName = classExtendsName;
        methDeclOffset = 0;
        varDeclOffset = 0;
    }
    public ClassOffsetInfo(String className, String classExtendsName, Integer varDeclOffset, Integer methDeclOffset) {  // constructor for ClassExtendedDeclaration
        this.className = className;
        this.extendsClassName = classExtendsName;
        this.methDeclOffset = methDeclOffset;
        this.varDeclOffset = varDeclOffset;
    }

    public void calcVariableOffset(String Id, String type) {        //  calculate Variable Offset
        VarOffsets.put(Id, varDeclOffset);          
        varDeclOffset += calcOffset(type);
    }

    public int calcOffset(String type) {                        // return Offset number for every type
        if (type.equals("int")) 
            return 4;
        else if (type.equals("boolean")) {
            return 1;
        }
        else
            return 8;
    }

    public void calcMethodOffset(String Id) {           //  calculate Method Offset
        MetOffsets.put(Id, methDeclOffset);
        methDeclOffset += 8;
    }

    public void printClassOffsetInfo() {
        System.out.println();
        System.out.println("-----------Class " + className + "-----------");
        System.out.println("---Variables---");
        for (String name : VarOffsets.keySet()) {
            System.out.println(className + "." + name + " : " + VarOffsets.get(name));
        } 
        System.out.println("---Methods---");
        for (String name : MetOffsets.keySet()) {
            System.out.println(className + "." + name + " : " + MetOffsets.get(name));
        }
    }
}