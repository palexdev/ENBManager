package io.github.palexdev.enbmanager.frontend.views;

import com.sandec.mdfx.MarkdownView;
import io.github.palexdev.enbmanager.backend.events.bus.SimpleEventBus;
import io.github.palexdev.enbmanager.frontend.Globals;
import io.github.palexdev.enbmanager.frontend.views.AboutView.AboutPane;
import io.github.palexdev.enbmanager.frontend.views.base.View;
import io.github.palexdev.virtualizedfx.beans.VirtualBounds;
import io.github.palexdev.virtualizedfx.controls.VirtualScrollPane;
import io.github.palexdev.virtualizedfx.utils.VSPUtils;
import io.inverno.core.annotation.Bean;
import javafx.application.HostServices;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

@Bean
public class AboutView extends View<AboutPane> {
    //================================================================================
    // Properties
    //================================================================================
    private final HostServices services;

    //================================================================================
    // Constructors
    //================================================================================
    public AboutView(SimpleEventBus events, HostServices services) {
        super(events);
        this.services = services;
    }

    //================================================================================
    // Overridden Methods
    //================================================================================
    @Override
    protected AboutPane build() {
        return new AboutPane();
    }

    //================================================================================
    // Internal Classes
    //================================================================================
    public class AboutPane extends StackPane {
        private final VBox container;

        AboutPane() {
            container = new VBox();
            container.getStyleClass().add("box");

            VirtualScrollPane vsp = new VirtualScrollPane(container);
            vsp.setOrientation(Orientation.VERTICAL);
            vsp.contentBoundsProperty().bind(container.boundsInParentProperty().map(b -> VirtualBounds.of(
                container.getWidth(), container.getHeight(), b.getWidth(), b.getHeight()
            )));
            vsp.vValProperty().addListener((ob, o, n) -> {
                double maxScroll = Math.max(0, container.getBoundsInParent().getHeight() - container.getHeight());
                container.setTranslateY(-n.doubleValue() * maxScroll);
            });
            VSPUtils.setVSpeed(vsp, 64, 64, 64);


            MarkdownView mdView = new MarkdownView(assembleText()) {
                @Override
                public void setLink(Node node, String link, String description) {
                    try {
                        node.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                            node.requestFocus();
                            services.showDocument(link);
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            mdView.getStylesheets().clear();
            VBox.setVgrow(mdView, Priority.ALWAYS);
            container.getChildren().add(mdView);

            getStyleClass().add("about-view");
            getChildren().add(vsp);
        }

        String assembleText() {
            return Globals.ABOUT +
                "\n\n" +
                Globals.WHY +
                "\n\n" +
                Globals.FUNCTIONALITIES +
                "\n\n" +
                Globals.TOOLS +
                "\n\n" +
                Globals.SUPPORT;
        }
    }
}
