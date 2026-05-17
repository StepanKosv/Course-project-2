package com.stepankosvin.reddit_collect_pascage;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class ListingBody implements JsonBody {
    private final JsonNode body;
    private final JsonNode data;

    public ListingBody(JsonNode body) {
        this.body = body;
        this.data = body.has("data") ? body.get("data") : body;
    }

    @Override public JsonNode getBody() { return body; }
    @Override public String fullString() { return body.toString(); }
    
    @Override public String infoString() {
        JsonNode children = data.get("children");
        int count = (children != null && children.isArray()) ? children.size() : 0;
        return "ListingBody | elements: " + count;
    }

    /** Выдает всех детей. Использует classificate */
    public List<JsonBody> getChilds() {
        List<JsonBody> result = new ArrayList<>();
        JsonNode children = data.get("children");
        if (children != null && children.isArray()) {
            for (JsonNode child : children) {
                result.add(classificate(child));
            }
        }
        return result;
    }

    /** Классифицирует узел по полю kind */
    public static JsonBody classificate(JsonNode json) {
        String kind = json.has("kind") ? json.get("kind").asText().trim() : null;
        if (kind == null) return new JsonHolder(json);
        
        return switch (kind) {
            case "t1" -> new CommentBody(json);
            case "t3" -> new PostBody(json);
            case "Listing" -> new ListingBody(json);
            default -> new JsonHolder(json);
        };
    }

    /** Простая реализация JsonBody для неизвестных типов */
    public static class JsonHolder implements JsonBody {
        private final JsonNode body;
        public JsonHolder(JsonNode body) { this.body = body; }
        @Override public JsonNode getBody() { return body; }
        @Override public String fullString() { return body.toString(); }
        @Override public String infoString() { return "JsonHolder | unknown kind"; }
        @Override
        public String toString() {
        	return "["+super.toString()+" "+infoString()+"]";
        }
    }

    @Override
    public String toString() {
    	return "["+super.toString()+" "+infoString()+"]";
    }
}