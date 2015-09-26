# Node server stop script
# Author: Yujie Sui
# ! /bin/sh

echo "----------------------------------------"
echo "Try to stop node server ..."
echo "----------------------------------------"

export CLASSPATH=.
export CLASSPATH=${CLASSPATH}:`dirname $0`/bin/

THISDIR=`dirname $0`

for i in `find ${THISDIR}/lib -name "*.jar" `
do
  export CLASSPATH=${CLASSPATH}:$i
done
echo "Classpath = ${CLASSPATH}"

OPTS="-Xms128m -Xmx256m"

ARGS="$OPTS"

echo $ARGS

java $ARGS server.node.StopServer "-shutdown"
