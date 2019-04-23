package antagonisticapple;
import java.io.File;
import java.sql.*;

public class Database {

    private Connection dbConnect;

    public void openDb(String databaseFilename) {
        try {
            dbConnect = DriverManager.getConnection("jdbc:sqlite:" + Values.DIR_ROOT.getValue() + File.separator + Values.DIR_DB.getValue() + File.separator + databaseFilename);
        } catch (SQLException e) {
            System.out.println("Opening DB connect failed");
        }
    }

    public void addNewManga(String tableName, int mangaId, String title, String authors, String status, String summary, String webAddress, String genreTags, int totalChapters, int currentPage, int lastChapterRead, int lastChapterDownloaded, int newChaptersBoolean, int favoriteBoolean) {

        try(Statement sqlStatement = dbConnect.createStatement()) {
            sqlStatement.execute("INSERT INTO " + tableName + " (title_id, title, authors, status, summary, web_address, genre_tags, total_chapters, current_page, last_chapter_read, last_chapter_downloaded, new_chapters, favorite) VALUES " +
                    "('" + mangaId + "', '" + title + "', '" + authors + "', '" + status + "', '" + summary + "', '" + webAddress + "', '" + genreTags + "', '" + totalChapters + "', '" + currentPage + "', '" + lastChapterRead + "', '" + lastChapterDownloaded + "', '" + newChaptersBoolean + "', '" + favoriteBoolean + "')");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    <T> void modifyManga(String tableName, int mangaId, String columnToModify, T newColumnValue) {
        try(Statement sqlStatement = dbConnect.createStatement()) {
            sqlStatement.execute("UPDATE " + tableName + " SET " + columnToModify + " = '" + newColumnValue + "' WHERE title_id = '" + mangaId + "'");
        } catch (SQLException e) {
            System.out.println(e.getMessage() + "   db message");
        }

    }

//    public void modifyManga(String tableName, int mangaID, String columnToModify, int newColumnValue) {
//        try(Statement sqlStatement = dbConnect.createStatement()) {
//            sqlStatement.execute("UPDATE " + tableName + " SET " + columnToModify + " = " + newColumnValue + " WHERE title_id = '" + mangaID + "'");
//        } catch (SQLException e) {
//            System.out.println(e.getMessage() + "   db message");
//        }
//    }
//
//    public void modifyMangaValueString(String tableName, int mangaID, String columnToModify, String newColumnValue) {
//        try(Statement sqlStatement = dbConnect.createStatement()) {
//            sqlStatement.execute("UPDATE " + tableName + " SET " + columnToModify + " = " + newColumnValue + " WHERE title_id = '" + mangaID + "'");
//        } catch (SQLException e) {
//            System.out.println(e.getMessage() + "   db message");
//        }
//    }

    void downloadQueueAdd(String tableName, int titleId, String webAddress, int startingChapter, int lastChapterDownloaded) {
        try(Statement sqlStatement = dbConnect.createStatement()) {
            sqlStatement.execute("INSERT INTO " + tableName + " (title_id, web_address, starting_chapter, last_chapter_downloaded) VALUES ('" + titleId + "', '" + webAddress + "', '" + startingChapter + "', '" + lastChapterDownloaded + "')");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet downloadQueueFetch(String tableName) {
        try {
            Statement sqlStatement = dbConnect.createStatement();
            return sqlStatement.executeQuery("SELECT * FROM " + tableName + " ORDER BY ROWID ASC LIMIT 1");
        } catch (Exception e) {

        } return null;
    }

    void downloadQueueRemove(String tableName, int mangaId) {
        try(Statement sqlStatement = dbConnect.createStatement()) {
            sqlStatement.execute("DELETE FROM " + tableName + " WHERE title_id = '" + mangaId + "'");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void downloadDbAttach() {
        try(Statement sqlStatement = dbConnect.createStatement()) {
            sqlStatement.execute("ATTACH DATABASE '" + Values.DIR_ROOT.getValue() + File.separator + Values.DIR_DB.getValue() + File.separator + Values.DB_NAME_DOWNLOADING.getValue() + "' AS downloadDb");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void downloadDbDetach() {
        try(Statement sqlStatement = dbConnect.createStatement()) {
            sqlStatement.execute("DETACH DATABASE 'downloadDb'");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void deleteManga(String sourceTable, int mangaID) {
        try(Statement sqlStatement = dbConnect.createStatement()) {
            sqlStatement.execute("DELETE FROM " + sourceTable + " WHERE title_id = '" + mangaID + "'");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void moveManga(String sourceTable, String destinationTable, int mangaID) {
        try(Statement sqlStatement = dbConnect.createStatement()) {
            sqlStatement.execute("INSERT INTO " + destinationTable + " SELECT * FROM " + sourceTable + " WHERE title_id = '" + mangaID + "'");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ResultSet workingGenreFilter(String column, String table, String tagQuery, String sortByValue, String sortOrder) {
        try {
            Statement sqlStatement = dbConnect.createStatement();
            System.out.println("SELECT " + column + " FROM " + table + tagQuery + sortByValue + sortOrder);
            return sqlStatement.executeQuery("SELECT " + column + " FROM " + table + tagQuery + sortByValue + sortOrder);

        } catch (Exception e) {

        } return null;
    }

    public ResultSet filterManga(String tableName, String column, String databaseSearchString) {
//needs a lot of work to make it valid for the 19 different fetches we need

        try {
            Statement sqlStatement = dbConnect.createStatement();
            return sqlStatement.executeQuery("SELECT * FROM " + tableName + " WHERE " + column + " = '" + databaseSearchString + "'");
        } catch (Exception e) {

        } return null;
    }

    public void updateNewestManga(String tableName, int mangaId, String title) {
        try {
            Statement sqlStatement = dbConnect.createStatement();
//            sqlStatement.execute("DELETE FROM " + tableName + " WHERE rowid=1"); //replacing this with a deleteall instead of only rowid1
            sqlStatement.execute("DELETE FROM " + tableName);
            sqlStatement.execute("INSERT OR REPLACE INTO " + tableName + " (title_id, title) VALUES ('" + mangaId + "', '" + title + "')");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void createBookmark(String tableName, int mangaID, int totalChapters, int lastChapterRead, int currentPage) {
        System.out.println("create bookmark running!  - " + mangaID + " - " + lastChapterRead + " - " + currentPage);
        try {
            Statement sqlStatement = dbConnect.createStatement();
            sqlStatement.execute("DELETE FROM " + tableName + " WHERE rowid=1");
            sqlStatement.execute("INSERT OR REPLACE INTO " + tableName + " (title_id, total_chapters, last_chapter_read, current_page) VALUES ('" + mangaID + "', '" + totalChapters + "', '" + lastChapterRead + "', '" + currentPage + "')");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ResultSet retrieveBookmark(String tableName) {
        try {
            Statement sqlStatement = dbConnect.createStatement();
            return sqlStatement.executeQuery("SELECT * FROM " + tableName);
        } catch (Exception e) {

        } return null;
    }

    public void saveSettings(String tableName, String settingName, String settingValue) {

        try {
            Statement sqlStatement = dbConnect.createStatement();
            sqlStatement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (settingName TEXT, settingValue TEXT, CONSTRAINT singular UNIQUE (settingName))");
            sqlStatement.execute("INSERT OR REPLACE INTO " + tableName + " (settingName, settingValue) VALUES ('" + settingName + "', '" + settingValue + "')");
        } catch (Exception e) {
        }
    }

    public String retrieveSettings(String tableName, String settingName) {
        ResultSet settingValue;
        String settingValueFinal = null;

        try {
            Statement sqlStatement = dbConnect.createStatement();
            settingValue = sqlStatement.executeQuery("SELECT settingValue FROM " + tableName + " WHERE settingName = '" + settingName + "'");
            settingValueFinal = settingValue.getString("SettingValue");

        } catch (Exception e) {
        }
        return settingValueFinal;
    }


    public void closeDb() {
        try {
            if (dbConnect != null) {
                dbConnect.close();
            }
        } catch (SQLException e) {
            System.out.println("Closing DB connect failed");
        }
    }
}