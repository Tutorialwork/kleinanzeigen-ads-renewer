FROM eclipse-temurin:21-jre-ubi9-minimal

WORKDIR /var/lib/kleinanzeigen-ads-renewer

ADD target/kleinanzeigen-ads-renewer-*.jar kleinanzeigen-ads-renewer.jar

ENV JAVA_OPTIONS="-Xms128M -Xmx1024M"

CMD ["sh", "-c", "java $JAVA_OPTIONS -jar kleinanzeigen-ads-renewer.jar"]