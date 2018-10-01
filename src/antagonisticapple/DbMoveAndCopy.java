package antagonisticapple;

public class DbMoveAndCopy {

    private Database database = new Database();

    public void copyToDownloading(int selectedManga, String webAddress, int startingChapter) {
        System.out.println("manga being copied to downloads db");
        database.openDb(Values.DB_NAME_DOWNLOADING.getValue());
        database.downloadQueueAdd("downloading",selectedManga,webAddress,startingChapter,0);
        database.closeDb();
    }
}
