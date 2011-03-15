VERSION=$(shell ruby -ne 'print $$1 if $$_ =~ /^  <version>(.+)</' pom.xml)
JAR_FOR_NAVICSERACH=ecnavi-tokenizer-for-navicsearch-$(VERSION).jar
MVN=mvn

package:
	rm -f target/$(JAR_FOR_NAVICSERACH)
	mvn clean
	mvn package
	jar cf $(JAR_FOR_NAVICSERACH) -C target/classes jp
	mv $(JAR_FOR_NAVICSERACH) target

install-lib:
	$(MVN) install:install-file -Dfile=lib/apache-solr-core-1.4.0.jar -DgroupId=apache.solr -DartifactId=core -Dversion=1.4.0 -Dpackaging=jar
	$(MVN) install:install-file -Dfile=lib/lucene-analyzers-2.9.1.jar  -DgroupId=apache.lucene -DartifactId=analyzers -Dversion=2.9.1 -Dpackaging=jar 
	$(MVN) install:install-file -Dfile=lib/lucene-core-2.9.1.jar  -DgroupId=apache.lucene -DartifactId=core -Dversion=2.9.1 -Dpackaging=jar 

archive:
	cd ..; tar --exclude=".svn" --exclude="*~" --exclude="lib" -jcvf ecnavi-tokenizer-for-solr-1.4-$(VERSION).tar.bz2 ecnavi-tokenizer-for-solr-1.4
