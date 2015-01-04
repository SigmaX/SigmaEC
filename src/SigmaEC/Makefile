JAVAC=javac
JAR=jar
#JARFILE=${HOME}/Java/sigmaec/SigmaEC.jar
JARFILE=${HOME}/Java/sigmaec/SigmaEC.jar
SRCS=../SigmaEC/*.java ../SigmaEC/*/*.java ../SigmaEC/*/*/*.java
CLASSPATH=${R_HOME}/library/rJava/jri/JRI.jar
#;${R_HOME}/library/rJava/jri/JRIEngine.jar;${R_HOME}/library/rJava/jri/REngine.jar

${JARFILE}: ${SRCS}
	${JAVAC} -cp ${CLASSPATH} ${SRCS}
#	${JAR} cf ${JARFILE} *.class */*.class */*/*.class
#	cd ..; ${JAR} cf ${JARFILE} */*.class */*/*.class */*/*/*.class
	cd ..; ${JAR} cf ${JARFILE} `find . -name "*.class"`

clean:
	rm -f ${JARFILE} `find . -name "*.class"` `find . -name "*~"`

