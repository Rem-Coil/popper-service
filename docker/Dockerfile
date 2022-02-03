FROM openjdk:8-jdk
EXPOSE 80:80
RUN mkdir /app
COPY ./popper/ /app/
WORKDIR /app/bin
CMD ["./popper"]