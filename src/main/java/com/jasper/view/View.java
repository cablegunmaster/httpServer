package com.jasper.view;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Jasper Lankhorst on 17-11-2016.
 */
public class View {

    private JFrame mainFrame;

    //textareas
    private JTextArea logtextArea;

    private JMenuItem stopMenuItem;
    private JMenuItem restartMenuItem;

    public View() {
        prepareGUI();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Main Server");
        mainFrame.setMinimumSize(new Dimension(200, 270));
        mainFrame.setSize(100, 100);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        }); //Exit on click.

        mainFrame.setJMenuBar(this.NorthPanel());
        mainFrame.add(this.MiddleContainer(), BorderLayout.NORTH);

        mainFrame.pack(); //resorts the area to show everything.
        mainFrame.setVisible(true);
    }

    /**
     * Prepares toolbar on top.
     */
    @Nonnull
    private JMenuBar NorthPanel() {

        //the top toolbar.
        JMenuBar menuBar = new JMenuBar();
        //the first menu item.
        JMenu menu = new JMenu("File");
        //Item in options.
        restartMenuItem = new JMenuItem("Reboot server");
        stopMenuItem = new JMenuItem("Quit server");

        menu.add(restartMenuItem);
        menu.addSeparator();
        menu.add(stopMenuItem);

        menuBar.add(menu);

        return menuBar;
    }

    @Nonnull
    public JMenuItem getRestartMenuItem() {
        return restartMenuItem;
    }

    @Nonnull
    public JMenuItem getstopMenuItem() {
        return stopMenuItem;
    }

    /**
     * Used for the GUI in the middle.
     *
     * @return JPanel containing the middle screen.
     */
    @Nonnull
    private JPanel MiddleContainer() {

        //New Panel.
        JPanel middleContainer = new JPanel();
        middleContainer.setLayout(new GridLayout(0, 2));

        //retrieve the incoming label
        JScrollPane receivingLogTextArea = setReceivingLogTextArea();
        JLabel serverLogLabel = new JLabel("Incoming Log");
        serverLogLabel.setPreferredSize(new Dimension(125, 100));

        //Retrieve the outgoing Label.
        JLabel outgoingLoglabel = new JLabel("Outgoing Log:");
        JScrollPane outgoingLogPane = setOutgoingTextArea();

        //adds the Scrollpanel to the panel.
        middleContainer.add(serverLogLabel, BorderLayout.CENTER);
        middleContainer.add(receivingLogTextArea, BorderLayout.CENTER);

        //add the logLabel and LogPanel
        middleContainer.add(outgoingLoglabel, BorderLayout.CENTER);
        middleContainer.add(outgoingLogPane, BorderLayout.CENTER);

        return middleContainer;
    }

    /**
     * Set the Log textarea combining the JScrollpane with the textarea.
     *
     * @return JScrollPane containing a new textarea 300x300 size.
     */
    @Nonnull
    public JScrollPane setOutgoingTextArea() {
        JTextArea outgoingTextArea = new JTextArea();
        outgoingTextArea.setLineWrap(true); //makes sure no
        outgoingTextArea.setWrapStyleWord(true);

        //SHOW The scroll panel on the textArea.
        JScrollPane jscrollpane = new JScrollPane(outgoingTextArea);
        jscrollpane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jscrollpane.setPreferredSize(new Dimension(300, 300));

        return jscrollpane;
    }

    /**
     * Set the Log textarea combining the JScrollpane with the textarea.
     *
     * @return JScrollPane containing a new textarea 300x300 size.
     */
    @Nonnull
    public JScrollPane setReceivingLogTextArea() {
        logtextArea = new JTextArea();
        logtextArea.setLineWrap(true); //makes sure no
        logtextArea.setWrapStyleWord(true);

        //SHOW The scroll panel on the textArea.
        JScrollPane jscrollpane = new JScrollPane(logtextArea);
        jscrollpane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jscrollpane.setPreferredSize(new Dimension(300, 300));

        return jscrollpane;
    }

    /**
     * Function: returning the mainview Log TextArea for appending a String.
     *
     * @return JTextArea for appending a String
     */
    @Nonnull
    public JTextArea getLogTextArea() {
        return logtextArea;
    }

    /**
     * Refresh the view van de maininterface.
     */
    public void refresh() {
        mainFrame.doLayout();
        mainFrame.revalidate();
    }

}
