FROM openjdk:8-jdk
EXPOSE 80:80
RUN mkdir /app
COPY ../.. /app/
WORKDIR app/build/build/install/popper/bin
CMD ["./popper"]