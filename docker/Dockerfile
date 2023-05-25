FROM openjdk:17

RUN mkdir /home/dev

COPY ./build/metarmophic.pdf-1.0.jar /home/dev/metarmophic.pdf-1.0.jar

ENTRYPOINT ["java","-jar","/home/dev/metarmophic.pdf-1.0.jar"]
