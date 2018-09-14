package antagonisticapple;

public class MangaListDownload {

    private String mangaTitle;
    private String mangaWebAddress;

    public String getMangaTitle() {
        return mangaTitle;
    }

    public String getMangaWebAddress() {
        return mangaWebAddress;
    }

    public MangaListDownload(String mangaTitle, String mangaWebAddress) {
        this.mangaTitle = mangaTitle;
        this.mangaWebAddress = mangaWebAddress;
    }
}
