package com.stepankosvin.reddit_collect_pascage;
/**
 * Интерфейс для текстовых элементов обсуждений (посты, комментарии).
 * Наследует JsonBody. Все методы предусматривают возможность возврата null.
 */
public interface DiscussionTextElement extends JsonBody {

    /** Текстовое содержимое (selftext / body) */
    String text();

    /** HTML представление содержимого (selftext_html / body_html) */
    String html();

    /** URL элемента */
    String url();

    /** Строковый идентификатор (соответствует полю name в JSON, например "t3_17hsxyu") */
    String stringId();

    /** Тип элемента (например, "postBody", "commentBody") */
    String type();

    /** Идентификатор родителя (может быть null для постов) */
    String parentId();

    /** URL родителя (может быть null) */
    String parentUrl();

    /** Имя сабреддита */
    String subredditName();

    /** Строковый идентификатор сабреддита (например, "t5_2qh2z") */
    String subredditIdString();
    
    String getUsername();
    String getUserIdStr();

    /**
     * Переопределение из ТЗ: возвращает строку в формате "тип | url | text".
     * Default-реализация безопасна к возможным null-значениям геттеров.
     */
    @Override
    default String infoString() {
        return String.format("%s | %s | %s", type(), url(), stringId());
    }
}