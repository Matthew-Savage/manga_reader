package antagonisticapple;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;

class AddressCheckAndCorrect {

    private static String databaseMangaName;
    private static Database db = new Database();

    static String verifyAddress(String webAddress, String mangaName) {

        String mangaNameFormatted = mangaName.replaceAll("[^a-zA-Z0-9 ]", "").replace(" ", "_");
        databaseMangaName = mangaName;

        if (isAddressInvalid(webAddress)) {
            return checkResultAddresses(getResultAddresses(getSearchResults(mangaNameFormatted)));
        }
        return webAddress;
    }

    private static boolean isAddressInvalid(String webAddress) {

        try {
            Document mangaError = Jsoup.connect(webAddress).get();
            return mangaError.select(".login").text().equals("Sorry, the page you have requested cannot be found. Click here go visit our homepage");
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    private static Document getSearchResults(String mangaName) {
        try {
            return Jsoup.connect(Values.URL_ROOT.getValue() + Values.URL_SEARCH.getValue() + mangaName).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<String> getResultAddresses(Document searchResults) {
        ArrayList<String> addressList = new ArrayList<>();

        for (Element addresses : searchResults.select(".story_name")) {
            addressList.add(addresses.select("h3 a").first().attr("abs:href"));
        }
        return addressList;
    }

    private static String checkResultAddresses(ArrayList<String> addressList) {

        for (String address : addressList) {
            try {
                if (getFunctionalAddress(Jsoup.connect(address).get())) {
                    return address;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static boolean getFunctionalAddress(Document document) {
        return document.select(".manga-info-text li h1").text().replace("'", "").equals(databaseMangaName);
    }
}
