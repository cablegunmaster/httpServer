package com.jasper.main;


import com.jasper.model.file.FileLoader;

import java.io.InputStream;

public class Main {

    public static void main(String[] args) {

        Server server = new Server(8081);

        //TODO list.
        //headers lezen werken.
        //POST werkende maken.

        //server.serve("web");
        //URL naar Mustache file parsen

        //eigen template engine.
        //feature scope JMustache (replace variabelen).

        //Mustache context, eigen mustache compiler maken?
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
        //10 Request afhandelen met 1 thread, non blocking threads?

        //only does upgrade.
        server.socket("/chat", (req, res) -> {
            //read out request.
            //send back with response?
        });

        server.get("/hello", (req, res) -> res.write("Hello World1@#$%&#@(%)@#&ëäï"));

        //these 2 calls / and index.html should it be the same?
        server.get("/index.html", (req, res) -> {
            //String[] names = req.getParameter("name"); //RFC doorlezen.
            res.write("Hello World INDEX.html");
        });

        server.get("/", (req, res) -> res.write("Hello World same as index.html This is a string."));
        server.get("/hello/test/*", (req, res) -> res.write("Hello World2!"));
        server.post("/store", (req, res) -> res.write("Hello World3"));

        server.get("/game", (reg,res) -> res.write(
                FileLoader.loadFile("header.html") +
                FileLoader.loadFile("body.html") +
                        FileLoader.loadFile("footer.html")
        ));

        server.setGUIVisible(false);

        server.start();
    }
}
