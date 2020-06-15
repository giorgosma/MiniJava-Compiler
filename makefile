all: compile

compile:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac Main.java FillSymbolTableVisitor.java TypeCheckingVisitor.java OffsetVisitor.java LLVMVisitor.java

clean:
	rm -f *.class *~
