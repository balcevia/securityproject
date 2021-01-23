FROM java:8
WORKDIR .
COPY security-assembly-1.0.jar /
CMD java -jar security-assembly-1.0.jar