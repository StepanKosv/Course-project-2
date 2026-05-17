package com.stepankosvin.reddit_collect_pascage;
import com.fasterxml.jackson.databind.JsonNode;

public class CommentBody implements DiscussionTextElement {
    private final JsonNode body;
    private final JsonNode data;

    public CommentBody(JsonNode body) {
        this.body = body;
        this.data = body.has("data") ? body.get("data") : body;
    }

    @Override public JsonNode getBody() { return body; }
    @Override public String fullString() { return body.toString(); }

    @Override public String text()       { return safeGet("body"); }
    @Override public String html()       { return safeGet("body_html"); }
    @Override public String url()        { return safeGet("permalink"); } // По ТЗ используется permalink
    @Override public String stringId()   { return safeGet("name"); }
    @Override public String type()       { return "commentBody"; }
    @Override public String parentId()   { return safeGet("parent_id"); }
    @Override public String parentUrl()  { return null; }
    @Override public String subredditName() { return safeGet("subreddit"); }
    @Override public String subredditIdString() { return safeGet("subreddit_id"); }

    // Дополнительные поля из ТЗ
    @Override public String getUsername()  { return safeGet("author"); }
    @Override public String getUserIdStr() { return safeGet("author_fullname"); }
    public JsonNode getReplies() { return data.get("replies"); }

    private String safeGet(String field) {
        JsonNode node = data.get(field);
        return (node == null || node.isNull()) ? null : node.asText();
    }
    @Override
    public String toString() {
    	return "["+super.toString()+" "+infoString()+"]";
    }
}