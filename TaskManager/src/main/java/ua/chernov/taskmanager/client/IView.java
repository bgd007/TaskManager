package ua.chernov.taskmanager.client;

import java.awt.event.ActionListener;

public interface IView {

    String ACTION_UPDATE = "update";
    String ACTION_CLOSE  = "close";

    void addActionListener(ActionListener al);
    void removeActionListener(ActionListener al);



    void show();
    void update(Object model);
    void close();
    
}
