package com.jasper.model.connection.objectPoolTest;

class ExportingProcess {
    private long processNo;

    ExportingProcess(long processNo) {
        this.processNo = processNo;
        // do some  expensive calls / tasks here in future
        // .........
        System.out.println("Object with process no. " + processNo + " was created");
    }

    long getProcessNo() {
        return processNo;
    }
}// End of the ExportingProcess class.
