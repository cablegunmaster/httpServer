package com.jasper.main;


public class Main {

    public static void main(String[] args) {

        Server server = new Server(8081);

        //headers lezen werken.

        //POST werkende maken.

        //server.serve("web");
        //URL naar Mustache file parsen

        //eigen template engine.
        //feature scope JMustache (replace variabelen).

        //context, uitdaging.
        //als externe pom includen.
        // {{#waarde}} {{value}} {{/waarde}}
        // {{waarde}} toString valueof Object.
        // {{^waarde}} {{/waarde}}

        //SECTION , als lijst tegenkomt, repeat de section.
        //lijst met componenten.
        //templatePart.
        //rendermetContext
        //render

        //Database bouwen.
        //10 Request afhandelen met 1 thread, non blocking threads.

        server.get("/hello", (req, res) -> {
            res.write("Hello World1@#$%&#@(%)@#&ëäï");
        });

        //these 2 calls / and index.html should it be the same?
        server.get("/index.html", (req, res) -> {
            String[] names = req.getParameter("name"); //RFC doorlezen.
            res.write("Hello World INDEX.html");
        });

        server.get("/", (req, res) -> {
            res.write("Hello World same as index.html?");
        });

        server.get("/hello/test/*", (req, res) -> {
            res.write("Hello World2!");
        });

        //TODO post afhandelen
        server.post("/store", (req, res) -> {
            res.write("Hello World3");
        });

        server.start();
    }
}
