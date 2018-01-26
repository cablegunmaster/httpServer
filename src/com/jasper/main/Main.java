package com.jasper;

import com.jasper.controller.Controller;
import com.jasper.model.Model;
import com.jasper.view.View;

public class Main {

    public static void main(String[] args) {
        int portNumber = 80;

        if (args.length > 0) {
            try {
                portNumber = Integer.parseInt(args[0]);
            } catch (Exception parseException) {
                System.err.println("No valid portnumber found: returning to basic port 8081");
                portNumber = 80; //assign one if none given.
            }
        }

        Model model = new Model();
        View view = new View();
        new Controller(model, view, portNumber);
    }
}
