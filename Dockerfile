FROM openjdk:17.0.1

COPY ./target/scala-2.13/stopit.jar /opt/app/app.jar
COPY ./config.conf /opt/app/config.conf

CMD ["java", "-Dconfig=/opt/app/config.conf", "-jar", "/opt/app/app.jar"]