package antagonisticapple;

public class MangaListView {
    private String mangaNumber;
    private boolean newChapters;
    private boolean favorite;

    public String getMangaNumber() {
        return mangaNumber;
    }

    public boolean isNewChapters() {
        return newChapters;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setNewChapters(boolean newChapters) {
        this.newChapters = newChapters;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public MangaListView(String mangaNumber, boolean newChapters, boolean favorite) {
        this.mangaNumber = mangaNumber;
        this.newChapters = newChapters;
        this.favorite = favorite;
    }
}
