export JAVA_OPTS="$JAVA_OPTS -Djava.awt.headless=true -server -Xmx2G -Xms2G -XX:MaxPermSize=128m -XX:+UseGCOverheadLimit -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+CMSIncrementalMode -XX:+CMSIncrementalPacing -XX:CMSIncrementalDutyCycleMin=0 -XX:+UseParNewGC -XX:+UseStringCache -XX:+OptimizeStringConcat -XX:+UseCompressedStrings -XX:+UseTLAB -XX:+DisableExplicitGC -XX:+UseCompressedOops -XX:+CMSParallelRemarkEnabled"
export CATALINA_OUT=/dev/null

# if Java heap size is over 32GB heap, remove -XX:+UseCompressedOops
# if your machine has 2 or more cpu, enabled -XX:+CMSParallelRemarkEnabled by -XX:+UseConcMarkSweepGC
