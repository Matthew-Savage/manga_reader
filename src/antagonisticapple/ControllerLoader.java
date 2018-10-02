package antagonisticapple;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ControllerLoader {

    @FXML
    private AnchorPane testPane;
    @FXML
    TextField preloadProgressTop;
    @FXML
    TextField preloadProgressCenter;
    @FXML
    TextField preloadProgressBottom;

    private Database database = new Database();
    private IndexMangaChapters indexMangaChapters = new IndexMangaChapters();
    private DbMoveAndCopy dbMoveAndCopy = new DbMoveAndCopy();
    private PopulateMangaCatalog populate = new PopulateMangaCatalog(this);
    private Executor executor = Executors.newSingleThreadExecutor();

    public void initialize() {

        preloadProgressCenter.setText(Values.DIR_ROOT.getValue());

//        executor.execute(this::fetchNewTitles);
//        executor.execute(this::checkForUpdates);
        executor.execute(this::launchMainApp);
//        thread.start();
    }


//    private Runnable prelaunchTasks = () -> {
//        fetchNewTitles();
//        checkForUpdates();
//        switchStage();
//    };

    public void launchMainApp() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                switchStage();
            }
        });
    }

    private void fetchNewTitles() {
        preloadProgressCenter.setText("Checking For New Manga");
        populate.findStartingPage();
    }

    private void checkForUpdates() {
        clearProgressText();
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        preloadProgressCenter.setText("Checking for New Chapters");
        ArrayList<Integer> updateCheckAddresses = new ArrayList<>();
        int selectedManga;
        int startingChapter;
        String webAddress;

        int updateTotalChapters;
        try {
            database.openDb(Values.DB_NAME_MANGA.getValue());
            ResultSet resultSet = database.filterManga("completed", "status", "Ongoing");
            if (resultSet.next()) {
                do {
                    updateCheckAddresses.add(resultSet.getInt("title_id"));
                } while (resultSet.next());
                resultSet.close();
            database.closeDb();
            for (int i = 0; i < updateCheckAddresses.size(); i++) {
                selectedManga = updateCheckAddresses.get(i);
                database.openDb(Values.DB_NAME_MANGA.getValue());
                resultSet = database.filterManga("completed", "title_id", Integer.toString(selectedManga));
                ArrayList chapterCount = indexMangaChapters.getChapterCount(resultSet.getString("web_address"));
                updateTotalChapters = resultSet.getInt("total_chapters");
                webAddress = resultSet.getString("web_address");
                startingChapter = (resultSet.getInt("last_chapter_read"));
                database.closeDb();
                System.out.println(chapterCount.size() + "  -  chapter count!");
                if (chapterCount.size() > updateTotalChapters) {
                    int sizeDifference = chapterCount.size() - updateTotalChapters;
                    System.out.println("holy shit a manga " + updateCheckAddresses.get(i) + " has " + sizeDifference + " new chapters!");
//                    chapterPages.getChapterPages(updateTotalChapters, updateWebAddress, Integer.toString(selectedManga));
                    database.openDb(Values.DB_NAME_MANGA.getValue());
                    database.modifyManga("completed", selectedManga, "total_chapters", chapterCount.size());
                    database.modifyManga("completed", selectedManga, "last_chapter_read", startingChapter);
                    database.modifyManga("completed", selectedManga, "new_chapters", 1);
                    database.moveManga("completed", "download_pending", selectedManga);
                    database.closeDb();
                    dbMoveAndCopy.copyToDownloading(selectedManga, webAddress, startingChapter);
                }
                }
            }
            clearProgressText();
            TimeUnit.MILLISECONDS.sleep(100);
            preloadProgressTop.setText("Update Check Complete");
            preloadProgressBottom.setText("Launching Reader");
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (Exception e) {
            //Not Possible
            System.out.println(e);
        }
    }

    public void clearProgressText() {
        preloadProgressTop.clear();
        preloadProgressCenter.clear();
        preloadProgressBottom.clear();
    }


    public void switchStage() {
        Stage primaryStage = (Stage) testPane.getScene().getWindow();
        primaryStage.hide();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.getIcons().add(new Image("assets/ico.png"));
        primaryStage.setTitle("Cupcaked Manga Reader");
        primaryStage.setScene(new Scene(root, 1920, 1034, Color.TRANSPARENT));  //1026
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setResizable(false);
//        primaryStage.setFullScreen(true);
//        primaryStage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.ESCAPE));
        primaryStage.show();
        root.requestFocus();


    }


}
