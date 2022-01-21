FROM openjdk:8-jdk
EXPOSE 80:80
RUN mkdir /app
COPY .. /app/
WORKDIR app
CMD ["ls"]
CMD ["ls build"]
CMD ["ls build/install"]
CMD ["ls build/install/popper"]
CMD ["ls build/install/popper/bin"]
WORKDIR app/build/install/popper/bin
CMD ["./popper"]