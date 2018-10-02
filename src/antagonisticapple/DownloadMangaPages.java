package antagonisticapple;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class DownloadMangaPages {

    private Database database = new Database();
    private int startingChapter;
    private String mangaId;

    public void getChapterPages(int startingChapterNumber, String webAddress, String mangaIdNumber) throws Exception{
        startingChapter = startingChapterNumber;
        mangaId = mangaIdNumber;

        IndexMangaChapters chapters = new IndexMangaChapters();
        ArrayList<String> chapterLinks = chapters.getChapterCount(webAddress);
        System.out.println("actual downloader info: " + mangaId + " " + startingChapterNumber + " " + chapterLinks.size());
        int totalChapters = chapterLinks.size();
        int loopCount = totalChapters - startingChapter;
        int image = 0;

        System.out.println("total chapters" + totalChapters);
        System.out.println("loop count" + loopCount);

//        for (int i = startingChapter; i > totalChapters - startingChapter; i--)

        if (loopCount > 0) {
            System.out.println("loopcount greater than zero!!");
        } else {
            System.out.println("loopcount NOT greater than zero??!");
        }

        while (loopCount > 0){
            Document chapter = Jsoup.connect(chapterLinks.get(loopCount - 1)).get();
            for (Element pages : chapter.select(".vung-doc .img_content")) {
//                File chapterFolder = new File("C:\\MangaReader\\" + mangaID + "\\" + folder + "\\" + image + ".png");
//                URL page = new URL(pages.select("img").first().attr("abs:src"));
//                FileUtils.copyURLToFile(page,chapterFolder);
                InputStream page = new URL(pages.select("img").first().attr("abs:src")).openStream();
                Path to = Paths.get(Values.DIR_ROOT.getValue() + File.separator + Values.DIR_MANGA.getValue() + File.separator + mangaId + File.separator + Integer.toString(startingChapter));
                System.out.println(to);
                Files.createDirectories(to);
                Files.copy(page, Paths.get(Values.DIR_ROOT.getValue() + File.separator + Values.DIR_MANGA.getValue() + File.separator + mangaId + File.separator + Integer.toString(startingChapter) + File.separator + String.format("%03d", image) + ".png"), StandardCopyOption.REPLACE_EXISTING);
                image++;
            }
            image = 0;
            startingChapter++;
            updateLastChapDownloaded();
            System.out.println(startingChapter + " starting chapter");
            System.out.println(startingChapterNumber+1);
//            controller.modifyThread.execute(this::updateLastChapDownloaded);
            if (startingChapter == (startingChapterNumber +1)) {
                copyToCurrentlyReading();
            }
//            database.modifyManga("downloading",Integer.parseInt(mangaID),"last_chapter_downloaded",startingChapter);
            loopCount--;
        }

        if (loopCount == 0) {
            removeFromDownloadQueue();
        }
    }

    private void updateLastChapDownloaded() {
        database.openDb(Values.DB_NAME_DOWNLOADING.getValue());
        database.modifyManga("downloading",Integer.parseInt(mangaId),"last_chapter_downloaded",startingChapter);
        database.closeDb();
    }

    private void copyToCurrentlyReading() {
        database.openDb(Values.DB_NAME_MANGA.getValue());
        database.moveManga("download_pending", "currently_reading", Integer.parseInt(mangaId));
        database.closeDb();
    }

    private void removeFromDownloadQueue() {
        database.openDb(Values.DB_NAME_DOWNLOADING.getValue());
        database.downloadQueueRemove("downloading",Integer.parseInt(mangaId));
        database.closeDb();
    }

    //    speed test!!
////    try (InputStream in = new URL(photoUrl).openStream()) {
////        Files.copy(in, Paths.get(absoluteDestination + photoId + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
////    }

}