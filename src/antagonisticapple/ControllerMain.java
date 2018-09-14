package antagonisticapple;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControllerMain {

    @FXML
    private ImageView imageView;
    @FXML
    private ImageView thumbImage;
    @FXML
    private ImageView favoriteOverlay;
    @FXML
    private ImageView newChaptersOverlay;
    @FXML
    private ImageView mangaThumb;
    @FXML
    private AnchorPane main;
    @FXML
    private AnchorPane testPane;
    @FXML
    private Pane launchPane;
    @FXML
    private Pane catalogPane;
    @FXML
    private Pane thumbPane;
    @FXML
    private Pane favoriteOverlayPane;
    @FXML
    private Pane newChaptersOverlayPane;
    @FXML
    private Pane popupPane;
    @FXML
    private Pane innerPopup;
    @FXML
    private Pane innerPopupNumbox;
    @FXML
    private Pane genrePane;
    @FXML
    private Pane pageNumTextPane;
    @FXML
    private Pane sidebarPane;
    @FXML
    private ScrollPane scrollImagePane;
    @FXML
    private BorderPane readMangaPane;
    @FXML
    private CheckBox ascendDescend;
    @FXML
    private CheckBox genres;
    @FXML
    private CheckBox completeIncomplete;
    @FXML
    private RadioButton haveNotRead;
    @FXML
    private RadioButton haveRead;
    @FXML
    private TextField mangaTitle;
    @FXML
    private TextField mangaGenres;
    @FXML
    private TextField mangaAuthors;
    @FXML
    private TextField lastChapterRead;
    @FXML
    private TextField searchBox;
    @FXML
    private TextField currentPageNumberDisplay;
    @FXML
    private TextField lastPageNumberDisplay;
    @FXML
    private TextField enterPageNumber;
    @FXML
    private TextField sidebarChapterNumber;
    @FXML
    private TextField sidebarPageNumber;
    @FXML
    private TextArea mangaSummary;
    @FXML
    private ChoiceBox<String> currentActivity;
    @FXML
    private Button pageFirst;
    @FXML
    private Button pagePrevious;
    @FXML
    private Button pageNext;
    @FXML
    private Button pageLast;
    @FXML
    private Button appMinimize;
    @FXML
    private Button appClose;
    @FXML
    private Button startReading;
    @FXML
    private Button ignoreManga;
    @FXML
    private Button getInnerPopup;
    @FXML
    private Button ignoreMangaShort;
    @FXML
    private Button favorite;
    @FXML
    private Button readManga;
    @FXML
    private Button unignore;
    @FXML
    private Button sidebarPaneSizeDecrease;
    @FXML
    private Button sidebarPaneSizeIncrease;

    private Database database = new Database();
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private ArrayList<ImageView> favoriteOverlayViews = new ArrayList<>();
    private ArrayList<ImageView> newChaptersOverlayViews = new ArrayList<>();
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private ArrayList<String> genreStrings = new ArrayList<>();
    private ArrayList<MangaListView> mangaId = new ArrayList<>();
    private ArrayList<Integer> updateCheckAddresses = new ArrayList<>();
    private ArrayList<File> mangaPages = new ArrayList<>();
    private File thumbsPath = new File(Values.DIR_ROOT.getValue() + File.separator+ Values.DIR_THUMBS.getValue());
    private StringJoiner joiner = new StringJoiner(" AND ");
    private String webAddress;
    private String source;
    private String dest;
    private int startingChapter;
    private int pageNumber = 0;
    private int selectedManga = -1;
    private int lastChapterReadNumber;
    private int totalChaptersNumber;
    private int currentPageNumber;
    static Executor modifyThread = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService downloadThread = Executors.newScheduledThreadPool(1);
    private IndexMangaChapters indexMangaChapters = new IndexMangaChapters();
    private DownloadMangaPages downloadMangaPages = new DownloadMangaPages();
    private int thumbNumber;

    public void initialize() {
        GenreMap.createGenreString();

        for (int i = 0; i < 39; i++) {
            genreStrings.add(i, "");
        }
        for (Node thumbs : thumbPane.getChildren()) {
            imageViews.add((ImageView) thumbs);
        }
        for (Node favorites : favoriteOverlayPane.getChildren()) {
            favoriteOverlayViews.add((ImageView) favorites);
        }
        for (Node newChapters : newChaptersOverlayPane.getChildren()) {
            newChaptersOverlayViews.add((ImageView) newChapters);
        }
        for (Node genres : genrePane.getChildren()) {
            checkBoxes.add((CheckBox) genres);
        }

        currentActivity.setItems(FXCollections.observableArrayList("Available Manga", "My Library", "Finished Reading", "Not Interested"));

        lastChapterRead.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                forceNumbersOnly(oldValue, newValue, lastChapterRead);
            }
        });

        enterPageNumber.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                forceNumbersOnly(oldValue, newValue, enterPageNumber);
            }
        });

        main.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                imageView.setFitWidth(newValue.doubleValue());
                scrollImagePane.setMinWidth(newValue.doubleValue());
            }
        });

        main.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scrollImagePane.setMinHeight(newValue.doubleValue());
            }
        });
        downloadThread.scheduleWithFixedDelay(downloadManga,3,10,TimeUnit.SECONDS);
    }

    public void appClose() {
        Stage stage = (Stage) appClose.getScene().getWindow();
        stage.close();
    }

    public void appMinimize() {
        Stage stage = (Stage) appMinimize.getScene().getWindow();
        stage.setIconified(true);
    }

    private void fetchManga(String pathToManga) {
        mangaPages.clear();
        File file = new File(pathToManga);
        mangaPages.addAll(Arrays.asList(file.listFiles()));
    }

    public void readManga() {
        chapterNumber();
        readMangaBook();
    }

    private void chapterNumber() {

        database.openDb(Values.DB_NAME_MANGA.getValue());
        ResultSet resultSet = database.filterManga("currently_reading", "title_id", Integer.toString(selectedManga));

        try {
            totalChaptersNumber = Integer.parseInt(resultSet.getString("total_chapters"));
            currentPageNumber = Integer.parseInt(resultSet.getString("current_page"));
            lastChapterReadNumber = Integer.parseInt(resultSet.getString("last_chapter_read"));
            database.createBookmark("resume_last_manga", selectedManga, totalChaptersNumber, lastChapterReadNumber, currentPageNumber);
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println(e);
        }

//        if (currentPage.equals("null")) {
//            currentPage = "0";
//        }
//        if (lastChapterRead.equals("null")) {
//            lastChapterRead = "0";
//        }

        database.closeDb();
    }

    private void setNewChaptersFlagFalse() {
        database.openDb(Values.DB_NAME_MANGA.getValue());
        database.modifyManga(Values.DB_TABLE_READING.getValue(), selectedManga, Values.DB_TABLE_NEW_CHAPTERS.getValue(), 0);
    }

    private void readMangaBook() {
        try {
            readMangaPane.setVisible(true);
            sidebarPane.setVisible(true);
            catalogPane.setVisible(false);
            popupClose();
            readMangaPane.requestFocus();
            fetchManga(Values.DIR_ROOT.getValue() + File.separator + Values.DIR_MANGA.getValue() + File.separator + selectedManga + File.separator + lastChapterReadNumber);
            imageView.setImage(new Image(new FileInputStream(mangaPages.get(currentPageNumber))));
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            sidebarChapterNumber.setText(Integer.toString(lastChapterReadNumber + 1));
            sidebarPageNumber.setText(Integer.toString(currentPageNumber + 1));
            setNewChaptersFlagFalse();
        } catch (Exception e) {
            //not possible
        }
    }

    public void turnMangaBookPage(KeyEvent event) {
        readMangaPane.requestFocus();
        if (event.getCode() == KeyCode.D && currentPageNumber < mangaPages.size() || event.getCode() == KeyCode.RIGHT && currentPageNumber < mangaPages.size()) {
            currentPageNumber++;
            displayNextOrPreviousMangaBookPage(currentPageNumber);
            scrollImagePane.setVvalue(0.0);
//            modifyThread.execute(this::calculateChapterNumber);
//            new Thread(mangaPageTurned).start();
            calculateChapterNumber();
        } else if (event.getCode() == KeyCode.A && currentPageNumber > 0 || event.getCode() == KeyCode.LEFT && currentPageNumber > 0) {
            currentPageNumber--;
            displayNextOrPreviousMangaBookPage(currentPageNumber);
        } else if (event.getCode() == KeyCode.W && scrollImagePane.getVvalue() > 0.0 || event.getCode() == KeyCode.UP && scrollImagePane.getVvalue() > 0.0) {
            try {
                Robot ghostMouse = new Robot();
                ghostMouse.mouseWheel(-3);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else if (event.getCode() == KeyCode.S && scrollImagePane.getVvalue() < 1.0 || event.getCode() == KeyCode.DOWN && scrollImagePane.getVvalue() < 1.0) {
            try {
                Robot ghostMouse = new Robot();
                ghostMouse.mouseWheel(3);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    private void calculateChapterNumber() {
        int totalPages = mangaPages.size();
        if (currentPageNumber == totalPages) {
            lastChapterReadNumber++;
            currentPageNumber = 0;
            readMangaBook();
        }
        database.openDb(Values.DB_NAME_MANGA.getValue());
        database.modifyManga("currently_reading", selectedManga, "last_chapter_read", lastChapterReadNumber);
        database.modifyManga("currently_reading", selectedManga, "current_page", currentPageNumber);
        database.createBookmark("resume_last_manga", selectedManga, totalChaptersNumber, lastChapterReadNumber, currentPageNumber);
        database.closeDb();
        if (lastChapterReadNumber == totalChaptersNumber) {
            finishedReading();
        }
    }

    private void finishedReading() {
        database.openDb(Values.DB_NAME_MANGA.getValue());
        database.moveManga("currently_reading", "completed", selectedManga);
        database.createBookmark("resume_last_manga", -1, -1, -1, -1);
        database.closeDb();
        readMangaPane.setVisible(false);
        catalogPane.setVisible(true);
        currentActivity.setValue("My Library");
        databaseInit();
    }

    public void sidebarVisible() {
        sidebarPane.setOpacity(1);
    }

    public void sidebarInvisible() {
        sidebarPane.setOpacity(0);
    }

    public void sidebarFavorite() {
        MangaListView view = mangaId.get(thumbNumber);
        view.setFavorite(true);
        populateDisplay();

        database.openDb(Values.DB_NAME_MANGA.getValue());
        database.modifyManga("currently_reading",selectedManga,"favorite",1);
        database.closeDb();
        // modify the manga list itself to change favorite value from 0 to 1 and then run populate display. allows us to add a favorite without querying the database
    }

    public void mangaBookPageZoomInOrOut(ActionEvent event) {
        Button button = (Button) event.getSource();

        if (button.getId().equals("sidebarPane_size_decrease")) {
            switch (String.valueOf(imageView.getFitWidth())) {
                case "1920.0":
                    imageView.setFitWidth(1720);
                    sidebarPaneSizeIncrease.setVisible(true);
                    break;
                case "1720.0":
                    imageView.setFitWidth(1520);
                    break;
                case "1520.0":
                    imageView.setFitWidth(1320);
                    break;
                case "1320.0":
                    imageView.setFitWidth(1120);
                    break;
                case "1120.0":
                    imageView.setFitWidth(920);
                    sidebarPaneSizeDecrease.setVisible(false);
                    break;
            }
        } else if (button.getId().equals("sidebarPane_size_increase")) {
            switch (String.valueOf(imageView.getFitWidth())) {
                case "920.0":
                    imageView.setFitWidth(1120);
                    sidebarPaneSizeDecrease.setVisible(true);
                    break;
                case "1120.0":
                    imageView.setFitWidth(1320);
                    break;
                case "1320.0":
                    imageView.setFitWidth(1520);
                    break;
                case "1520.0":
                    imageView.setFitWidth(1720);
                    break;
                case "1720.0":
                    imageView.setFitWidth(1920);
                    sidebarPaneSizeIncrease.setVisible(false);
                    break;
            }
        }

    }

    public void sidebarGoBack() {
        readMangaPane.setVisible(false);
        sidebarPane.setVisible(false);
        launchPane.setVisible(true);
        databaseInit();
    }

    private void forceNumbersOnly(Number oldValue, Number newValue, TextField textField) {
        if (newValue.intValue() > oldValue.intValue()) {
            char character = textField.getText().charAt(oldValue.intValue());
            if (!(character >= '0' && character <= '9')) {
                textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
            }
        }
    }

    public void goToLastMangaBookPageViewed() {
        database.openDb(Values.DB_NAME_MANGA.getValue());
        ResultSet resultSet = database.retrieveBookmark("resume_last_manga");

        try {
            if (resultSet.next()) {
                selectedManga = resultSet.getInt("title_id");
                totalChaptersNumber = resultSet.getInt("total_chapters");
                lastChapterReadNumber = resultSet.getInt("last_chapter_read");
                currentPageNumber = resultSet.getInt("current_page");
                database.closeDb();
                readMangaBook();
            } else {
                launchPane.setVisible(false);
                catalogPane.setVisible(true);
                currentActivity.setValue("My Library");
                databaseInit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    private Runnable mangaPageTurned = () -> {
////        database.modifyManga("currently_reading", selectedManga, "current_page", currentPageNumber);
//        calculateChapterNumber();
//    };

    private void displayNextOrPreviousMangaBookPage(int currentPage) {
        try {
            FileInputStream inputStream = new FileInputStream(mangaPages.get(currentPage));
            Image mangaPage = new Image(inputStream);
            imageView.setImage(mangaPage);
            sidebarPageNumber.setText(Integer.toString(currentPage + 1));
        } catch (Exception e) {
            //not possible
        }
    }

    public void showAvailableMangaBooksPane() {
        popupClose();
        launchPane.setVisible(false);
        currentActivity.setValue("Available Manga");
        catalogPane.setVisible(true);
    }

    public void showOwnedMangaBooksPane() {
        popupClose();
        launchPane.setVisible(false);
        currentActivity.setValue("My Library");
        catalogPane.setVisible(true);
//        databaseInit();
    }

    public void genreIncludeExclude(ActionEvent event) {
        joiner = new StringJoiner(" AND ");
        CheckBox checkBox = (CheckBox) event.getSource();
        int indexNumber = (Integer.parseInt(checkBox.getId().substring(5)));
//        StringJoiner joiner = new StringJoiner(" AND ");
//        GenreMap genreMap = new GenreMap();
//        Map<String, String> genres = genreMap.getGenreMap();

        popupClose();
        pageNumber = 0;

        if (checkBox.isSelected()) {
            genreStrings.set(indexNumber, "genre" + indexNumber + ".include");
        } else if (!checkBox.isSelected() && !checkBox.isIndeterminate()) {
            genreStrings.set(indexNumber, "genre" + indexNumber + ".exclude");
        } else if (checkBox.isIndeterminate()) {
            genreStrings.set(indexNumber, "");
        }
        searchStringBuilder();
        databaseInit();
    }

    private void searchStringBuilder() {
//        GenreMap genreMap = new GenreMap();
//        Map<String, String> genres = genreMap.getGenreMap();
        Map<String, String> genres = GenreMap.getGenreMap();

        for (int i = 0; i < 39; i++) {
            if (genreStrings.get(i).length() > 0) {
                joiner.add(genres.get(genreStrings.get(i)));
            }
        }
    }

    public void ignoreManga() {
        if (currentActivity.getValue().equals("Available Manga") || currentActivity.getValue().equals("My Library")) {
            database.openDb(Values.DB_NAME_MANGA.getValue());
            database.moveManga(currentDatabase(), "not_interested", selectedManga);
            database.closeDb();
        } else if (currentActivity.getValue().equals("Not Interested")) {
            modifyThread.execute(this::moveToPendingDownload);
            modifyThread.execute(this::copyToDownloading);
        }
        database.closeDb();
        popupClose();
        modifyThread.execute(this::databaseInit);
        navButtonVisibility();
    }

    public void ascendDescend() {
        popupClose();
        databaseInit();
    }

    public void genreReset() {
        popupClose();

        for (int i = 0; i < 39; i++) {
            genres = checkBoxes.get(i);
            genres.setSelected(false);
            genres.setIndeterminate(true);
            genreStrings.add(i, "");
        }
        joiner = new StringJoiner(" AND ");
        databaseInit();
    }

    private String currentDatabase() {
        String database = "";

        switch (currentActivity.getValue()) {
            case "Available Manga":
                database = "available_manga";
                break;
            case "My Library":
                database = "currently_reading";
                break;
            case "Not Interested":
                database = "not_interested";
                break;
            case "Finished Reading":
                database = "completed";
                break;
        }
        return database;
    }

    public void searchBox() {
        popupClose();
        if (searchBox.getText().equals("")) {
            System.out.println("search box empty");
        } else {
            joiner = new StringJoiner(" AND ");
            searchStringBuilder();
            joiner.add("summary LIKE '%" + searchBox.getText() + "%' OR title LIKE '%" + searchBox.getText() + "%'");
            searchBox.clear();
            databaseInit();
        }
    }

    public void choiceBox() {
        popupClose();
        pageNumber = 0;
        databaseInit();
    }

    public void databaseInit() {
        String sortOrder;

        if (ascendDescend.isSelected()) {
            sortOrder = "ASC";
        } else {
            sortOrder = "DESC";
        }
        if (joiner.length() > 0) {
            if (currentActivity.getValue().equals("My Library")) {
                dbSearch("title_id, new_chapters, favorite", currentDatabase(), " WHERE " + joiner.toString(), sortOrder);
            } else {
                dbSearch("title_id", currentDatabase(), " WHERE " + joiner.toString(), sortOrder);
            }
        } else if (joiner.length() == 0) {
            if (currentActivity.getValue().equals("My Library")) {
                dbSearch("title_id, new_chapters, favorite", currentDatabase(), "", sortOrder);
            } else {
                dbSearch("title_id", currentDatabase(), "", sortOrder);
            }
        }
    }

    public void onlyComplete() throws Exception {
        database.openDb(Values.DB_NAME_MANGA.getValue());
        ResultSet results = database.filterManga("available_manga", "status", "Completed");

        while (results.next()) {
            System.out.println(results.getString("title"));
        }
        database.closeDb();
    }

    public void completeIncomplete() throws Exception {

        //NEEDS TO BE A BUTTON!! THREE STAGE
        if (completeIncomplete.isSelected()) {

        }

        database.openDb(Values.DB_NAME_MANGA.getValue());
        ResultSet results = database.filterManga("available_manga", "status", "Ongoing");

        while (results.next()) {
            System.out.println(results.getString("title"));
        }
        database.closeDb();
    }


    private void dbSearch(String columnName, String tableName, String requestedData, String sortOrder) {
        String sortByValue;

        if (currentActivity.getValue().equals("My Library")) {
            sortByValue = " ORDER BY new_chapters DESC, title_id ";
        } else {
            sortByValue = " ORDER BY title_id ";
        }

        mangaId.clear();
        database.openDb(Values.DB_NAME_MANGA.getValue());
        ResultSet resultSet = database.workingGenreFilter(columnName, tableName, requestedData, sortByValue, sortOrder);


            try {
                if (resultSet.next()) {
                    do {
                        if (currentActivity.getValue().equals("My Library")) {
                            mangaId.add(new MangaListView(resultSet.getString("title_id"),resultSet.getBoolean("new_chapters"),resultSet.getBoolean("favorite")));
                        } else {
//                            mangaId.add(resultSet.getString("title_id"));
                            mangaId.add(new MangaListView(resultSet.getString("title_id"), false, false));
                        }
                    } while (resultSet.next());
                }
            } catch (Exception e) {
                System.out.println(e + "  supposedly not possible fml");
            }

        database.closeDb();
        populateDisplay();
    }

    private void populateDisplay() {
        clearThumbs();
        hideThumbs();
        int numberOfLoops = 30;

            if (mangaId.size() < 30) {
                numberOfLoops = mangaId.size();
            }


        try {
            for (int i = 0; i < numberOfLoops; i++) {
                MangaListView view = mangaId.get(i + pageNumber);
                thumbImage = imageViews.get(i);
                favoriteOverlay = favoriteOverlayViews.get(i);
                newChaptersOverlay = newChaptersOverlayViews.get(i);
                thumbImage.setSmooth(true);
                    thumbImage.setImage(new Image(new FileInputStream(thumbsPath + File.separator + view.getMangaNumber() + ".jpg")));
                if (view.isFavorite()) {
                    favoriteOverlay.setImage(new Image("assets/favorite_overlay.png"));
                }
                if (view.isNewChapters()) {
                    newChaptersOverlay.setImage(new Image("assets/new_chapter_multiple.png"));
                }
                thumbImage.setVisible(true);
                favoriteOverlay.setVisible(true);
                newChaptersOverlay.setVisible(true);
            }
        } catch (Exception e) {
            System.out.println(e + "   super vague?");
        }
        navButtonVisibility();
    }

    public void turnIndexPage(ActionEvent event) {
        Button button = (Button) event.getSource();
        popupClose();

        switch (button.getId()) {
            case "pageFirst":
                pageNumber = 0;
                break;
            case "pagePrevious":
                pageNumber = pageNumber - 30;
                break;
            case "pageNext":
                pageNumber = pageNumber + 30;
                break;
            case "pageLast":
                pageNumber = mangaId.size() - 30;
                break;
        }
        populateDisplay();
    }

    public void popupOpen(MouseEvent event) throws Exception {

        innerPopupClose();
        ImageView image = (ImageView) event.getSource();

        thumbNumber = (Integer.parseInt(image.getId().substring(18)));

        if (currentActivity.getSelectionModel().getSelectedItem().equals("My Library")) {
            ignoreMangaShort.setVisible(true);
            favorite.setVisible(true);
            readManga.setVisible(true);
            ignoreManga.setVisible(false);
            getInnerPopup.setVisible(false);
            unignore.setVisible(false);
        } else if (currentActivity.getSelectionModel().getSelectedItem().equals("Available Manga")) {
            ignoreMangaShort.setVisible(false);
            favorite.setVisible(false);
            readManga.setVisible(false);
            ignoreManga.setVisible(true);
            getInnerPopup.setVisible(true);
            unignore.setVisible(false);
        } else if (currentActivity.getSelectionModel().getSelectedItem().equals("Not Interested")) {
            ignoreMangaShort.setVisible(false);
            favorite.setVisible(false);
            readManga.setVisible(false);
            ignoreManga.setVisible(false);
            getInnerPopup.setVisible(false);
            unignore.setVisible(true);
        }

        if (thumbNumber <= 14) {
            popupPane.setLayoutX(1144);
            innerPopup.setLayoutX(1329);
        } else if (thumbNumber >= 15) {
            popupPane.setLayoutX(15);
            innerPopup.setLayoutX(200);
        }
        popupPane.setVisible(true);

        database.openDb(Values.DB_NAME_MANGA.getValue());
//        ResultSet results = database.filterManga(currentDatabase(), "title_id", mangaId.get(thumbNumber + pageNumber));
        MangaListView view = mangaId.get(thumbNumber + pageNumber);
        ResultSet results = database.filterManga(currentDatabase(), "title_id", view.getMangaNumber());

        if (results.getString("title").length() > 63) {
            String finalTitle = results.getString("title").substring(0, 63) + " ...";
            mangaTitle.setText(finalTitle);
        } else {
            mangaTitle.setText(results.getString("title"));
        }
        mangaThumb.setImage(new Image(new FileInputStream(thumbsPath + File.separator + view.getMangaNumber() + ".jpg")));
        mangaSummary.setText(results.getString("summary"));
        mangaGenres.setText(results.getString("genre_tags").substring(0, results.getString("genre_tags").length() - 1));
        mangaAuthors.setText(results.getString("authors").substring(0, results.getString("authors").length() - 1));
        webAddress = results.getString("web_address");
        database.closeDb();
        results.close();
        selectedManga = Integer.parseInt(view.getMangaNumber());
    }

    public void popupClose() {
        popupPane.setVisible(false);
        innerPopupClose();
    }

    private void innerPopupClose() {
        innerPopup.setVisible(false);
        haveNotRead.setSelected(false);
        haveRead.setSelected(false);
        lastChapterRead.clear();
        innerPopupNumbox.setVisible(false);
    }

    public void showInnerPopup() {
        innerPopup.setVisible(true);
    }

    public void getMangaButton() {
        startReading.setVisible(true);
        if (haveRead.isSelected()) {
            innerPopupNumbox.setVisible(true);
            lastChapterRead.requestFocus();
        } else if (haveNotRead.isSelected()) {
            innerPopupNumbox.setVisible(false);
        }
    }

    public void getThisManga() {
        startingChapter = 0;
        int totalChapters = 0;
        if (lastChapterRead.getText().length() > 0) {
            startingChapter = Integer.parseInt(lastChapterRead.getText());
        }

        if (startingChapter > 0 && haveRead.isSelected() || haveNotRead.isSelected()) {
            totalChapters = writePreDownloadValues();
        } else if (startingChapter <= 0 && haveRead.isSelected()) {
            lastChapterRead.requestFocus();
        }
        popupClose();

        if (startingChapter < totalChapters) {
            modifyThread.execute(this::moveToPendingDownload);
            modifyThread.execute(this::copyToDownloading);
        } else {
            modifyThread.execute(this::moveToCompleted);
        }
        haveRead.setSelected(false);
        haveNotRead.setSelected(false);
        modifyThread.execute(this::databaseInit);
    }

    private int writePreDownloadValues() {
        ArrayList chapterCount = null;
        try {
            chapterCount = indexMangaChapters.getChapterCount(webAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.openDb(Values.DB_NAME_MANGA.getValue());
        database.modifyManga(Values.DB_TABLE_AVAILABLE.getValue(), selectedManga, "last_chapter_read", startingChapter);
        database.modifyManga(Values.DB_TABLE_AVAILABLE.getValue(), selectedManga, "total_chapters", chapterCount.size());
        database.closeDb();
        return chapterCount.size();
    }

    private void moveToCompleted() {
        database.openDb(Values.DB_NAME_MANGA.getValue());
        database.moveManga(Values.DB_TABLE_AVAILABLE.getValue(), Values.DB_TABLE_COMPLETED.getValue(), selectedManga);
        database.closeDb();
    }

    private void moveToPendingDownload() {
        String tableName = null;
        if (currentActivity.getValue().equals("Available Manga")) {
            tableName = Values.DB_TABLE_AVAILABLE.getValue();
        } else if (currentActivity.getValue().equals("Not Interested")) {
            tableName = Values.DB_TABLE_NOT_INTERESTED.getValue();
        }

        database.openDb(Values.DB_NAME_MANGA.getValue());
        database.moveManga(tableName,Values.DB_TABLE_DOWNLOAD_PENDIND.getValue(),selectedManga);
        database.closeDb();
    }

    private void copyToDownloading() {
        database.openDb(Values.DB_NAME_DOWNLOADING.getValue());
        database.downloadQueueAdd("downloading",selectedManga,webAddress,startingChapter,0);
        database.closeDb();
    }

    private Runnable downloadManga = () -> {
        int downloadingStartingChapter;
        int downloadingLastChapterDownloaded;
        String downloadingTitleId;
        String downloadingWebAddress;
        database.openDb(Values.DB_NAME_DOWNLOADING.getValue());
        ResultSet resultSet = database.downloadQueueFetch("downloading");

        try {
            if (resultSet.next()) {
                downloadingStartingChapter = resultSet.getInt("starting_chapter");
                downloadingLastChapterDownloaded = resultSet.getInt("last_chapter_downloaded");
                downloadingTitleId = resultSet.getString("title_id");
                downloadingWebAddress = resultSet.getString("web_address");
                database.closeDb();

                if (downloadingLastChapterDownloaded > downloadingStartingChapter) {
                    downloadingStartingChapter = downloadingLastChapterDownloaded; //so this makes sure that a resumed download starts at the last chapter that the last download left off
                }
                downloadMangaPages.getChapterPages(downloadingStartingChapter, downloadingWebAddress, downloadingTitleId);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    };

    private void navButtonVisibility() {
        if (mangaId.size() > 30) {
            pageNumTextPane.setVisible(true);
        } else if (mangaId.size() <= 30) {
            pageNumTextPane.setVisible(false);
        }

        if (pageNumber == 0 && mangaId.size() > 30) {
            pageFirst.setVisible(false);
            pagePrevious.setVisible(false);
            pageNext.setVisible(true);
            pageLast.setVisible(true);
        } else if (pageNumber >= 30 && pageNumber < mangaId.size() - 30) {
            pageFirst.setVisible(true);
            pagePrevious.setVisible(true);
            pageNext.setVisible(true);
            pageLast.setVisible(true);
        } else if (pageNumber != 0 && pageNumber == (mangaId.size() - mangaId.size() % 30) - 30 && mangaId.size() % 30 == 0 || pageNumber != 0 && pageNumber == (mangaId.size() - mangaId.size() % 30)) {
            pageFirst.setVisible(true);
            pagePrevious.setVisible(true);
            pageNext.setVisible(false);
            pageLast.setVisible(false);
        } else if (pageNumber == 0 && mangaId.size() <= 30) {
            pageFirst.setVisible(false);
            pagePrevious.setVisible(false);
            pageNext.setVisible(false);
            pageLast.setVisible(false);
        }
        pageNumberDisplay();
    }

    private void pageNumberDisplay() {
        int currentPageNumber = (pageNumber / 30) + 1;
        int totalPagesNumber = mangaId.size() / 30;

        if (mangaId.size() % 30 > 0) {
            totalPagesNumber++;
        }

        if (currentPageNumber < 10) {
            currentPageNumberDisplay.setLayoutX(1669);
        } else if (currentPageNumber < 100) {
            currentPageNumberDisplay.setLayoutX(1655);
        } else {
            currentPageNumberDisplay.setLayoutX(1642);
        }

        currentPageNumberDisplay.setText(Integer.toString(currentPageNumber));
        lastPageNumberDisplay.setText(Integer.toString(totalPagesNumber));
    }

    public void gotoSpecificPage() {
        popupClose();
        pageNumber = (Integer.parseInt(enterPageNumber.getText()) - 1) * 30;
        enterPageNumber.clear();
        populateDisplay();
    }

    private void clearThumbs() {
        for (int i = 0; i < 30; i++) {
            thumbImage = imageViews.get(i);
            favoriteOverlay = favoriteOverlayViews.get(i);
            newChaptersOverlay = newChaptersOverlayViews.get(i);
            thumbImage.setImage(null);
            favoriteOverlay.setImage(null);
            newChaptersOverlay.setImage(null);
        }
    }

    private void hideThumbs() {
        for (int i = 0; i < 30; i++) {
            thumbImage = imageViews.get(i);
            favoriteOverlay = favoriteOverlayViews.get(i);
            newChaptersOverlay = newChaptersOverlayViews.get(i);
            thumbImage.setVisible(false);
            favoriteOverlay.setVisible(false);
            newChaptersOverlay.setVisible(false);
        }
    }

    public void lastPage() throws Exception {
        clearThumbs();
        hideThumbs();
        popupClose();
//        int position = mangaId.size() - mangaId.size() % 30; - this is a duplicate of pagenumber, whats its fucking purpose? lol I dont know.
        int view = 0;
        pageNumber = mangaId.size() - mangaId.size() % 30;

        for (int i = pageNumber; view < mangaId.size() % 30; i++) {
            MangaListView listView = mangaId.get(i);
            thumbImage = imageViews.get(view);
            favoriteOverlay = favoriteOverlayViews.get(view);
            newChaptersOverlay = newChaptersOverlayViews.get(view);
            thumbImage.setImage(new Image(new FileInputStream(thumbsPath + File.separator + listView.getMangaNumber() + ".jpg"), 167, 250, false, true));
            thumbImage.setVisible(true);
            favoriteOverlay.setVisible(true);
            newChaptersOverlay.setVisible(true);
            view++;
        }
        navButtonVisibility();
    }

}
