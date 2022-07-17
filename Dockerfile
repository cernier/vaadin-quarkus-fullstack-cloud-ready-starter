FROM maven as build
WORKDIR /code
COPY . /code/
RUN mvn clean package -DskipTests -Pproduction

FROM openjdk:11-jre
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'
# Configure the JAVA_OPTIONS, you can add -XshowSettings:vm to also display the heap size.
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
EXPOSE 8080
USER 185
WORKDIR /app
# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=build --chown=185 /code/target/quarkus-app/lib/ /app/lib/
COPY --from=build --chown=185 /code/target/quarkus-app/*.jar /app/
COPY --from=build --chown=185 /code/target/quarkus-app/app/ /app/app/
COPY --from=build --chown=185 /code/target/quarkus-app/quarkus/ /app/quarkus/
ENTRYPOINT [ "java", "-jar", "/app/quarkus-run.jar" ]