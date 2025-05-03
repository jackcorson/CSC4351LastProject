JFLAGS=-g

Full:
	javac ${JFLAGS}  */*.java

Parse/*.class: Parse/*.java
	javac ${JFLAGS} Parse/*.java


clean:
	cp Parse/*.class ./.
	rm -f */*.class Parse/Grm.java Parse/Grm.err Parse/Grm.out
	mv *.class ./Parse/
