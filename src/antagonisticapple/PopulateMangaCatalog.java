package antagonisticapple;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PopulateMangaCatalog {

    private static String type = "newest";
    private static String cat = "all";
    private static String state = "all";
    private static String bookBlockParent = ".list-truyen-item-wrap";
    private static String bookBlockChild = ".leftCol";
    private int currentDownload = 0;
    private int highestMangaId;
    private int totalNewTitles;
    private String highestMangaTitle;
    private List<MangaListDownload> firstPass = new ArrayList<>();
    private List<MangaListDownload> secondPass = new ArrayList<>();
    private Database database = new Database();
    private ControllerLoader controller;

    public PopulateMangaCatalog(ControllerLoader controller) {
        this.controller = controller;
    }

    public void findStartingPage() {
        int startingPage = 1;
        boolean matchFound = false;

        database.openDb(Values.DB_NAME_MANGA.getValue());
        ResultSet resultSet = database.retrieveBookmark("newest_manga");
        try {
            if (resultSet.next()) {
                highestMangaId = resultSet.getInt("title_id");
                highestMangaTitle = resultSet.getString("title");
                database.closeDb();
                do {
                    try {
                        Document mangaParent = Jsoup.connect("http://manganelo.com/manga_list?type=" + type + "&category=" + cat + "&state=" + state + "&page=" + startingPage).get();

                        for (Element list : mangaParent.select(bookBlockParent)) {
                            String title = list.select("h3 a").text().replace("'", "");
                            if (title.equals(highestMangaTitle)) {
                                System.out.println("fount a match! Page " + startingPage);
                                matchFound = true;
                            }
                        }
                        if (!matchFound) {
                            startingPage++;
                        }
                    } catch (Exception e) {
                        System.out.println("this is the exception to check if website is down! inserting a break should fix it, or a stop or a kill or whatever it is");
                        e.printStackTrace();
                    }
                } while (!matchFound);
                if (matchFound) {
                    buildMangaList(startingPage);
                    locateLastAddedManga();
                }
                System.out.println("last manga downloaded was " + highestMangaTitle + "  with ID " + highestMangaId);
            } else if (!resultSet.next()) {
                database.closeDb();
                // need a process here to locate the final page of mangelo and start from there, as in the user has NO manga at all.
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void buildMangaList(int startingPage) {
        int secondPassPosition = 0;
        Document mangaParent = null;
        do {
            try {
                mangaParent = Jsoup.connect("http://manganelo.com/manga_list?type=" + type + "&category=" + cat + "&state=" + state + "&page=" + startingPage).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Element list : mangaParent.select(bookBlockParent)) {
                firstPass.add(new MangaListDownload(list.select("h3 a").text().replace("'", ""), list.select("h3 a").first().attr("abs:href")));
            }

            for (int i = firstPass.size(); i > 0; i--) {
                MangaListDownload list = firstPass.get(i - 1);
                secondPass.add(secondPassPosition, new MangaListDownload(list.getMangaTitle(), list.getMangaWebAddress()));
                secondPassPosition++;
            }
            firstPass.clear();
            startingPage--;
        } while (startingPage > 0);
    }

    private void locateLastAddedManga() {
        for (int i = 0; i < secondPass.size(); i++) {
            MangaListDownload list = secondPass.get(i);
//            System.out.println(list.getMangaTitle());  <- this is somehow correct?
            if (list.getMangaTitle().equals(highestMangaTitle)) {
                System.out.println("found manga on list, getting to work");
                updateMangaCatalog(i + 1);
            }
        }
    }

    private void updateMangaCatalog(int firstNewTitle) {
        totalNewTitles = secondPass.size() - firstNewTitle;
        if (totalNewTitles == 1) {
            controller.clearProgressText();
            controller.preloadProgressCenter.setText(totalNewTitles + " New Manga Found");
        } else {
            controller.clearProgressText();
            controller.preloadProgressCenter.setText(totalNewTitles + " New Manga Found!");
        }
        System.out.println(totalNewTitles + " new mangas to read!");
        for (int i = firstNewTitle; i < secondPass.size(); i++) {
            MangaListDownload list = secondPass.get(i);
            fetchMangaInfo(list.getMangaWebAddress(), list.getMangaTitle());
            System.out.println(list.getMangaTitle());
        }
    }

    private void fetchMangaInfo(String webAddress, String title) {
        try {
            Document mangaChild = Jsoup.connect(webAddress).get();
            database.openDb(Values.DB_NAME_MANGA.getValue());

            for (Element listInner : mangaChild.select(bookBlockChild)) {
                String authors = listInner.select("li:contains(Author(s))").text().replaceAll(".*: ", "").replace("'", "");
                String status = listInner.select("li:contains(Status)").text().replaceAll(".*: ", "");
                String genres = listInner.select("li:contains(Genres)").text().replaceAll(".*: ", "");
                String summary = listInner.select("div#noidungm").text().replaceAll(".*: ", "").replace("'", "");
                String thumbUrl = listInner.select(".manga-info-pic img").first().attr("abs:src");
                highestMangaId++;
                currentDownload ++;

                if (status.contains("Completed") && status.length() > 9) {
                    status = status.substring(0, 9);
                } else if (status.contains("Ongoing") && status.length() > 7) {
                    status = status.substring(0, 7);
                }

//                String titleNumber = String.format("%05d", highestMangaId);  <- looks like not needed?
                downloadThumbnail(thumbUrl, highestMangaId);

                database.addNewManga("available_manga", highestMangaId, title, authors, status, summary, webAddress, genres, 0, 0, 0, 0, 0, 0);
                database.updateNewestManga("newest_manga", highestMangaId, title);
                controller.clearProgressText();
                controller.preloadProgressCenter.setText(currentDownload + " of " + totalNewTitles + " Added");
            }
            database.closeDb();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadThumbnail(String thumbAddress, int titleNumber) {
        // Download image needs to be its own class
        try {
            InputStream page = new URL(thumbAddress.replace("http:", "https:")).openStream();
            Path to = Paths.get(Values.DIR_ROOT.getValue() + File.separator + Values.DIR_THUMBS.getValue());
            Files.createDirectories(to);
            Files.copy(page, Paths.get(Values.DIR_ROOT.getValue() + File.separator + Values.DIR_THUMBS.getValue() + File.separator + titleNumber + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//    public void findLastPage() {
//        int highestMangaId = -1;
//        String highestMangaTitle = null;
//        int startingPage = 1;
//        boolean matchFound = false;
//        database.openDb(Values.DB_NAME_MANGA.getValue());
//        ResultSet resultSet = database.retrieveBookmark("newest_manga");
//        try {
//            highestMangaId = resultSet.getInt("title_id");
//            highestMangaTitle = resultSet.getString("title");
//
//            System.out.println("last manga downloaded was " + highestMangaTitle + "  with ID " + highestMangaId);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        database.closeDb();
//
//
//        do {
//            try {
//                Document mangaParent = Jsoup.connect("http://manganelo.com/manga_list?type=" + type + "&category=" + cat + "&state=" + state + "&page=" + startingPage).get();
//
//                for (Element list : mangaParent.select(bookBlockParent)) {
//                    title = list.select("h3 a").text().replace("'", "");
//                    titleUrl = list.select("h3 a").first().attr("abs:href");
//
//
//                    if (matchFound) {
//                        database.openDb(Values.DB_NAME_MANGA.getValue());
//                        Document mangaChild = Jsoup.connect(titleUrl).get();
//
//                        for (Element listInner : mangaChild.select(bookBlockChild)) {
//                            authors = listInner.select("li:contains(Author(s))").text().replaceAll(".*: ", "").replace("'", "");
//                            status = listInner.select("li:contains(Status)").text().replaceAll(".*: ", "");
//                            genres = listInner.select("li:contains(Genres)").text().replaceAll(".*: ", "");
//                            summary = listInner.select("div#noidungm").text().replaceAll(".*: ", "").replace("'", "");
//                            thumbUrl = listInner.select(".manga-info-pic img").first().attr("abs:src");
//
//                            highestMangaId++;
//
//                            mangaId = String.format("%05d", highestMangaId);
//
//                            //grab url to manga cover image, rename, and save
//
//                            System.out.println(thumbUrl);
//
//
//                            InputStream page = new URL(thumbUrl.replace("http:", "https:")).openStream();
//                            Path to = Paths.get(Values.DIR_ROOT.getValue() + File.separator + Values.DIR_THUMBS.getValue());
//                            ;
//                            Files.createDirectories(to);
//                            Files.copy(page, Paths.get(Values.DIR_ROOT.getValue() + File.separator + Values.DIR_THUMBS.getValue() + File.separator + highestMangaId + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
//
//                            if (status.contains("Completed") && status.length() > 9) {
//                                status = status.substring(0, 9);
//                            } else if (status.contains("Ongoing") && status.length() > 7) {
//                                status = status.substring(0, 7);
//                            }
//
////                            database.addNewManga("available_manga", highestMangaId, title, authors, status, summary, titleUrl, genres, 0, 0, 0);
////                            database.updateNewestManga("newest_manga", highestMangaId, title);
//                            System.out.println(title + " added!");
//                        }
//
//
//                    }
//                    if (!matchFound) {
//                        if (title.equals(highestMangaTitle)) {
//                            System.out.println("fount a match! Page " + startingPage);
//                            matchFound = true;
//                        }
//                    }
//                }
//                if (matchFound && startingPage >= 1) {
//                    startingPage--;
//                } else if (!matchFound) {
//                    startingPage++;
//                }
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//        }
//        while (startingPage >= 1);
//
////        database.openDb(Values.DB_NAME_MANGA.getValue());
//        database.closeDb();
//    }
//}


//    public void dbPopulate(int pageNum) throws Exception {
//
//        do {
//            Document mangaParent = Jsoup.connect("http://manganelo.com/manga_list?type=" + type + "&category=" + cat + "&state=" + state + "&page=" + pageNum).get();
//            database.openDb(Values.DB_NAME_MANGA.getValue());
//
//            //loops through all the manga titles on the search results page
//            for (Element list : mangaParent.select(bookBlockParent)) {
//                title = list.select("h3 a").text().replace("'","");
//                titleUrl = list.select("h3 a").first().attr("abs:href");
//
//                //pulls the url we just snagged from the homepage and feeds it into our next loop
//                Document mangaChild = Jsoup.connect(titleUrl).get();
//
//                //page for specific manga title, fills in the blanks we cant get from search results
//                for (Element listInner : mangaChild.select(bookBlockChild)) {
//                    authors = listInner.select("li:contains(Author(s))").text().replaceAll(".*: ", "").replace("'","");
//                    status = listInner.select("li:contains(Status)").text().replaceAll(".*: ", "");
//                    genres = listInner.select("li:contains(Genres)").text().replaceAll(".*: ", "");
//                    summary = listInner.select("div#noidungm").text().replaceAll(".*: ", "").replace("'","");
//                    thumbUrl = listInner.select(".manga-info-pic img").first().attr("abs:src");
//
//                    mangaIdRaw++;
//
//                    mangaId = String.format("%05d", mangaIdRaw);
//
//                    //grab url to manga cover image, rename, and save
//                    File mangaThumb = new File("C:\\MangaReader\\thumbs\\" + mangaIdRaw + ".jpg");
//                    URL mangaThumbUrl = new URL(thumbUrl);
//
//                    FileUtils.copyURLToFile(mangaThumbUrl, mangaThumb);
//
//                    if (status.contains("Completed") && status.length() > 9) {
//                        status = status.substring(0,9);
//                    } else if (status.contains("Ongoing") && status.length() > 7) {
//                        status = status.substring(0,7);
//                    }
//
//                    database.addNewManga("available_manga",mangaIdRaw,title,authors,status,summary,titleUrl,genres, 0, 0, 0);
//                }
//            }
//            pageNum--;
//        } while (pageNum > 0);
//
//        System.out.println("we fuckinbg did it? question mark?    " + mangaIdRaw );
//        database.closeDb();
//    }
//}


//                //once page is ripped, turn the page to 1 page lower. Back to front boys.
//                if (nextPage != 1) {
//                    nextPage++;
//                } else if (nextPage == 1){
//                    pageNum--;
//                    nextPage = 0;
//                }