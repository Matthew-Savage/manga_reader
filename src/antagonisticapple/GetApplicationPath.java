package antagonisticapple;

import java.io.File;

public class GetApplicationPath {

    public static String getPath() {
        try {
            File mangaReader = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            return mangaReader.getParent();
        } catch (Exception e) {
            //Not Possible
        }
        return null;
    }
}
