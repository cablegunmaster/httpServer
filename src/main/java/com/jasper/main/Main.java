package com.jasper.main;



public class Main {

    public static void main(String[] args) {

        Server server = new Server(8080);

        server.get("/hello", (req, res) -> {
            res.write("Hello World1");
        });

        server.get("/index.html", (req, res) -> {
            res.write("Hello World INDEX.html");
        });

        server.get("/", (req, res) -> {
            res.write("Hello World same as index.html?");
        });

        server.get("/hello/test/*", (req,res) ->{
            res.write("Hello World2!");
        });

        server.post("/store", (req,res) ->{
            res.write("Hello World3");
        });

        server.start();
    }
}
