#!/bin/sh
cd `dirname $0`
cp=.:${CLASSPATH}:./conf/
for loop in `ls *.jar`;do
cp=${cp}:${loop}
done
for filename in `ls ./lib/*.jar`
do
cp=${cp}:${filename}
done
cp=classes:${cp}
JMXPORT=1043
DEBUGPORT=8789

case "$1" in
	start)
	echo "Start, Please waiting..."
	JVM_OTHER_PARAM="-server -Xmx1g -Xms1g -XX:SurvivorRatio=3 -XX:PermSize=128m -Xss256k  -XX:+DisableExplicitGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection  -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseFastAccessorMethods -XX:SurvivorRatio=4 -XX:LargePageSizeInBytes=10m -XX:CompileThreshold=10000"
	
	JVM_DEBUG_PARAM="-Xdebug -Xrunjdwp:transport=dt_socket,address=${DEBUGPORT},server=y,suspend=n"
	#JVM_SELECTOR_PROVIDER_PARAM=-Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider
	
	## JMXREMOTE.PASSWORD AND JMXREMOTE.ACCESS PERMISSION MUST BE 600
	
	JVM_JMX_PARAM="-Dcom.sun.management.jmxremote.port="${JMXPORT}
	JVM_JMX_PARAM=${JVM_JMX_PARAM}" -Dcom.sun.management.jmxremote.authenticate=true -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.password.file=./jmxremote/jmxremote.password -Dcom.sun.management.jmxremote.access.file=./jmxremote/jmxremote.access"
	
	java -cp ${cp} ${JVM_OTHER_PARAM} ${JVM_JMX_PARAM} ${JVM_DEBUG_PARAM} ${JVM_SELECTOR_PROVIDER_PARAM} $2 &
	echo "OK"
	;;

	stop)
	echo "Stop, Please waiting..."
	JMX_PASSWORD_FILE=./jmxremote/jmxremote.password
	java -cp ${cp} com.drive.cool.CloseServer ${JMXPORT} ${JMX_PASSWORD_FILE} &
	echo "OK"
	;;

	*)
	echo "Usage: $0 {start|stop}"
	exit 1
	;;
esac

exit 0



