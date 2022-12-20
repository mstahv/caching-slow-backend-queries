package org.example.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.example.ResultService;

import java.util.Date;

@Route
public class MainView extends VerticalLayout {


public MainView(ResultService service) {

        new Date().setDate(1);

        Button button = new Button("Run slow cached backend query",
                event -> {
                        long start = System.currentTimeMillis();
                        service.aggregateEntries(0, null, null);
                        Notification.show("Backend query took " + (System.currentTimeMillis() - start) + " ms", 10000, Notification.Position.BOTTOM_START);
                });

        add(button);

    Button modify = new Button("Updating backend (clears cache)",
            event -> {
                long start = System.currentTimeMillis();
                service.updateEntry(0, "");
                Notification.show("Updating backend took " + (System.currentTimeMillis() - start) + " ms", 10000, Notification.Position.BOTTOM_START);
            });

    add(modify);

}
}
