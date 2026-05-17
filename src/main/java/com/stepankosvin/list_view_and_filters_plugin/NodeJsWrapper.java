//package com.stepankosvin.list_view_and_filters_plugin;
//
//import java.sql.Timestamp;
//import java.util.Optional;
//
//import javafx.scene.web.WebEngine;
//import model.Node;
//
//public class NodeJsWrapper {
//    private final Node node;
//    private final WebEngine webEngine;
//
//    public NodeJsWrapper(Node node, WebEngine webEngine) {
//        this.node = node;
//        this.webEngine = webEngine;
//    }
//
//    // JS сможет читать id как обычное число (Number)
//    public Long getId() {
//        return node.getId();
//    }
//
//    // Конвертируем Timestamp в ISO-строку, которую JS легко превратит в new Date()
//    public String getCreateTime() {
//        return Optional.ofNullable(node.getCreateTime())
//                .map(Timestamp::toString)
//                .orElse(null);
//    }
//
//    public String getDeleteTime() {
//        return Optional.ofNullable(node.getDeleteTime())
//                .map(Timestamp::toString)
//                .orElse(null);
//    }
//
//    public String getDisplayText() {
//        return node.getDisplayText();
//    }
//
//    public String getType() {
//        return node.getType();
//    }
//
//    /**
//     * Превращает строку jsonMeta в настоящий, валидный объект JavaScript.
//     * Пользователь сможет писать в JS: node.getJsonMeta().someProperty
//     */
//    public JSObject getJsonMeta() {
//        String json = node.getJsonMeta();
//        if (json == null || json.trim().isEmpty()) {
//            json = "{}"; // Возвращаем пустой объект, если данных нет
//        }
//        
//        // Используем встроенный в WebView парсер JSON, чтобы получить JS-объект
//        return (JSObject) webEngine.executeScript("JSON.parse('" + escapeJson(json) + "')");
//    }
//
//    // Экранирование спецсимволов для безопасного выполнения в executeScript
//    private String escapeJson(String json) {
//        return json.replace("\\", "\\\\")
//                   .replace("'", "\\'")
//                   .replace("\n", "\\n")
//                   .replace("\r", "\\r");
//    }
//}