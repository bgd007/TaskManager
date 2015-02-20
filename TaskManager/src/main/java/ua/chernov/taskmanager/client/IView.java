package ua.chernov.taskmanager.client;

import java.awt.event.ActionListener;

public interface IView {
    // РњРµС‚РѕРґС‹ СѓРІРµРґРѕРјР»РµРЅРёСЏ РєРѕРЅС‚СЂРѕР»Р»РµСЂ Рѕ СЃРѕР±С‹С‚РёСЏС…

    String ACTION_UPDATE = "update";
    String ACTION_CLOSE  = "close";

    void addActionListener(ActionListener al);
    void removeActionListener(ActionListener al);

    // РњРµС‚РѕРґС‹ РґР»СЏ СѓРїСЂР°РІР»РµРЅРёСЏ РґР°РЅРЅС‹РјРё

//    int getAge() throws NumberFormatException;

//    void update();

    // РњРµС‚РѕРґС‹ РґР»СЏ СѓРїСЂР°РІР»РµРЅРёСЏ РІРёРґРѕРј

    void show();
    void update(Object model);
    void close();
//    void showError(String message);    
}
