# Node server start script
# Author: Yujie Sui
# ! /bin/sh

echo "----------------------------------------"
echo "Starting node server ..."
echo "----------------------------------------"

export CLASSPATH=.
export CLASSPATH=${CLASSPATH}:`dirname $0`/bin/

THISDIR=`dirname $0`

for i in `find ${THISDIR}/lib -name "*.jar" `
do
  export CLASSPATH=${CLASSPATH}:$i
done
echo "Classpath = ${CLASSPATH}"

OPTS="-server -Xms128m -Xmx1024m -Djava.library.path=./lib -Dlog4j.configurationFile=log4j2.xml"

DEBUG="-verbose:gc -XX:+PrintTenuringDistribution"

#ARGS="$OPTS $DEBUG"
ARGS="$OPTS"

echo $ARGS

java $ARGS server.node.BootStrap "$1"
