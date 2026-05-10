package reddit_collect_pascage;


public class LoadCommentCmd extends LoadPageCmd {
    public LoadCommentCmd(RedditCollectorVer2 collector, String url) {
        super(collector, url);
    }

    @Override
    public boolean isDeprecated() {
        // Нет смысла грузить, если пост уже загружен
        return isPostLoaded() || super.isDeprecated();
    }

    @Override
    public boolean isExecutable() {
        // Ждём, пока пост загрузится
        return !isPostLoaded();
    }

    private boolean isPostLoaded() {
        String addr = RedditUrlUtilities.extractAddress(url);
        if (addr == null) return false;
        String[] parts = addr.split("/");
        if (parts.length < 4) return false;
        // Формируем URL поста: r/sub/comments/id/.json
        String postUrl = "https://www.reddit.com/r/" + parts[1] + "/comments/" + parts[3] + "/.json";
        return collector.loadedPages.contains(postUrl);
    }
}