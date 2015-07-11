FROM java:8
ENV HOSTNAME 192.168.59.103
ADD target/scala-2.11/factorysimulation.jar /app/factorysimulation.jar
CMD java -jar -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=1898 -Dcom.sun.management.jmxremote.rmi.port=1898 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=${HOSTNAME} /app/factorysimulation.jar