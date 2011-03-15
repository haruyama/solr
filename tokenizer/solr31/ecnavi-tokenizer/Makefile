VERSION=$(shell ruby -ne 'print $$1 if $$_ =~ /^  <version>(.+)</' pom.xml)
SOLR_VERSION = '3.1.0'
JAR_FOR_NAVICSERACH=ecnavi-tokenizer-for-navicsearch-$(VERSION).jar
MVN=mvn

package:
	rm -f target/$(JAR_FOR_NAVICSERACH)
	mvn clean
	mvn package
	jar cf $(JAR_FOR_NAVICSERACH) -C target/classes jp
	mv $(JAR_FOR_NAVICSERACH) target

install-lib:
	$(MVN) install:install-file -Dfile=lib/apache-solr-core-$(SOLR_VERSION).jar -DgroupId=apache.solr -DartifactId=core -Dversion=$(SOLR_VERSION) -Dpackaging=jar
	$(MVN) install:install-file -Dfile=lib/lucene-analyzers-$(SOLR_VERSION).jar  -DgroupId=apache.lucene -DartifactId=analyzers -Dversion=$(SOLR_VERSION) -Dpackaging=jar 
	$(MVN) install:install-file -Dfile=lib/lucene-core-$(SOLR_VERSION).jar  -DgroupId=apache.lucene -DartifactId=core -Dversion=$(SOLR_VERSION) -Dpackaging=jar 

archive:
	cd ..; tar --exclude=".svn" --exclude="*~" --exclude="lib" -jcvf ecnavi-tokenizer-for-solr-3.1-$(VERSION).tar.bz2 ecnavi-tokenizer-for-solr-3.1
