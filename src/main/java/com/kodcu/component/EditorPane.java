package com.kodcu.component;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by usta on 09.04.2015.
 */
@Component
@Scope("prototype")
public class EditorPane extends AnchorPane {

    private final WebView webView;
    private final WebEngine webEngine;
    private EventHandler<WebEvent<String>> readyHandler;
    private final JSObject window;
    private final Logger logger = LoggerFactory.getLogger(EditorPane.class);

    public EditorPane() {
        this.webView = new WebView();
        this.webView.setContextMenuEnabled(false);
        this.getChildren().add(webView);
        this.webEngine = webView.getEngine();
        window = (JSObject) webEngine.executeScript("window");
        this.webEngine.setOnAlert(event -> {
            if (Objects.nonNull(readyHandler))
                readyHandler.handle(event);
        });
        initializeMargins();
    }

    private void initializeMargins() {
        AnchorPane.setBottomAnchor(this, 0D);
        AnchorPane.setTopAnchor(this, 0D);
        AnchorPane.setLeftAnchor(this, 0D);
        AnchorPane.setRightAnchor(this, 0D);
        VBox.setVgrow(this, Priority.ALWAYS);
        AnchorPane.setBottomAnchor(webView, 0D);
        AnchorPane.setTopAnchor(webView, 0D);
        AnchorPane.setLeftAnchor(webView, 0D);
        AnchorPane.setRightAnchor(webView, 0D);
        VBox.setVgrow(webView, Priority.ALWAYS);
    }

    public void load(String url) {
        if (Objects.nonNull(url))
            webEngine.load(url);
        else
            logger.error("Url is not loaded. Reason: null reference");
    }

    public void hide() {
        super.setVisible(false);
    }

    public void show() {
        super.setVisible(true);
    }

    public void setOnReady(EventHandler<WebEvent<String>> readyHandler) {
        this.readyHandler = readyHandler;
    }

    public String getLocation() {
        return webEngine.getLocation();
    }

    public void setMember(String name, Object value) {
        window.setMember(name, value);
    }

    public void call(String methodName, Object... args) {
        window.call(methodName, args);
    }

    public void whenStateSucceed(ChangeListener<Worker.State> stateChangeListener) {
        webEngine.getLoadWorker().stateProperty().addListener(stateChangeListener);
    }

    public Object getMember(String name) {
        return window.getMember(name);
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }

    public WebView getWebView() {
        return webView;
    }

    public void onClicked(EventHandler<MouseEvent> eventHandler) {
        webView.setOnMouseClicked(eventHandler);
    }

    public void dragDropped(EventHandler<DragEvent> eventHandler) {
        webView.setOnDragDropped(eventHandler);
    }

    public void confirmHandler(Callback<String, Boolean> confirmHandler) {
        webEngine.setConfirmHandler(confirmHandler);
    }

    public JSObject getWindow() {
        return window;
    }

    public String getEditorValue() {
        return (String) webEngine.executeScript("editor.getValue()");
    }

    public void switchMode(Object... args) {
        this.call("switchMode", args);
    }

    public void rerender(Object... args) {
        this.call("rerender",args);
    }

    public void focus() {
        webView.requestFocus();
    }
}
