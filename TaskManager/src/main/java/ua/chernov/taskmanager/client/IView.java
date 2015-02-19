package ua.chernov.taskmanager.client;

import java.awt.event.ActionListener;

public interface IView {
    // Методы уведомления контроллер о событиях

    String ACTION_UPDATE = "update";
    String ACTION_CLOSE  = "close";

    void addActionListener(ActionListener al);
    void removeActionListener(ActionListener al);

    // Методы для управления данными

//    int getAge() throws NumberFormatException;

//    void update();

    // Методы для управления видом

    void show();
    void update(Object model);
    void close();
//    void showError(String message);    
}
