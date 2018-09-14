package antagonisticapple;

import java.util.HashMap;
import java.util.Map;

public class GenreMap {

    private static Map<String, String> genreMap = new HashMap<>();

    public static Map<String, String> getGenreMap() {
        return genreMap;
    }

    public static void createGenreString() {
        genreMap.put("genre0.include","instr(genre_tags, 'Action') > 0");
        genreMap.put("genre0.exclude","instr(genre_tags, 'Action') = 0");
        genreMap.put("genre1.include","instr(genre_tags, 'Adult') > 0");
        genreMap.put("genre1.exclude","instr(genre_tags, 'Adult') = 0");
        genreMap.put("genre2.include","instr(genre_tags, 'Adventure') > 0");
        genreMap.put("genre2.exclude","instr(genre_tags, 'Adventure') = 0");
        genreMap.put("genre3.include","instr(genre_tags, 'Comedy') > 0");
        genreMap.put("genre3.exclude","instr(genre_tags, 'Comedy') = 0");
        genreMap.put("genre4.include","instr(genre_tags, 'Cooking') > 0");
        genreMap.put("genre4.exclude","instr(genre_tags, 'Cooking') = 0");
        genreMap.put("genre5.include","instr(genre_tags, 'Doujinshi') > 0");
        genreMap.put("genre5.exclude","instr(genre_tags, 'Doujinshi') = 0");
        genreMap.put("genre6.include","instr(genre_tags, 'Drama') > 0");
        genreMap.put("genre6.exclude","instr(genre_tags, 'Drama') = 0");
        genreMap.put("genre7.include","instr(genre_tags, 'Ecchi') > 0");
        genreMap.put("genre7.exclude","instr(genre_tags, 'Ecchi') = 0");
        genreMap.put("genre8.include","instr(genre_tags, 'Fantasy') > 0");
        genreMap.put("genre8.exclude","instr(genre_tags, 'Fantasy') = 0");
        genreMap.put("genre9.include","instr(genre_tags, 'Gender bender') > 0");
        genreMap.put("genre9.exclude","instr(genre_tags, 'Gender bender') = 0");
        genreMap.put("genre10.include","instr(genre_tags, 'Harem') > 0");
        genreMap.put("genre10.exclude","instr(genre_tags, 'Harem') = 0");
        genreMap.put("genre11.include","instr(genre_tags, 'Historical') > 0");
        genreMap.put("genre11.exclude","instr(genre_tags, 'Historical') = 0");
        genreMap.put("genre12.include","instr(genre_tags, 'Horror') > 0");
        genreMap.put("genre12.exclude","instr(genre_tags, 'Horror') = 0");
        genreMap.put("genre13.include","instr(genre_tags, 'Josei') > 0");
        genreMap.put("genre13.exclude","instr(genre_tags, 'Josei') = 0");
        genreMap.put("genre14.include","instr(genre_tags, 'Manhua') > 0");
        genreMap.put("genre14.exclude","instr(genre_tags, 'Manhua') = 0");
        genreMap.put("genre15.include","instr(genre_tags, 'Manhwa') > 0");
        genreMap.put("genre15.exclude","instr(genre_tags, 'Manhwa') = 0");
        genreMap.put("genre16.include","instr(genre_tags, 'Martial arts') > 0");
        genreMap.put("genre16.exclude","instr(genre_tags, 'Martial arts') = 0");
        genreMap.put("genre17.include","instr(genre_tags, 'Mature') > 0");
        genreMap.put("genre17.exclude","instr(genre_tags, 'Mature') = 0");
        genreMap.put("genre18.include","instr(genre_tags, 'Mecha') > 0");
        genreMap.put("genre18.exclude","instr(genre_tags, 'Mecha') = 0");
        genreMap.put("genre19.include","instr(genre_tags, 'Medical') > 0");
        genreMap.put("genre19.exclude","instr(genre_tags, 'Medical') = 0");
        genreMap.put("genre20.include","instr(genre_tags, 'Mystery') > 0");
        genreMap.put("genre20.exclude","instr(genre_tags, 'Mystery') = 0");
        genreMap.put("genre21.include","instr(genre_tags, 'One shot') > 0");
        genreMap.put("genre21.exclude","instr(genre_tags, 'One shot') = 0");
        genreMap.put("genre22.include","instr(genre_tags, 'Psychological') > 0");
        genreMap.put("genre22.exclude","instr(genre_tags, 'Psychological') = 0");
        genreMap.put("genre23.include","instr(genre_tags, 'Romance') > 0");
        genreMap.put("genre23.exclude","instr(genre_tags, 'Romance') = 0");
        genreMap.put("genre24.include","instr(genre_tags, 'School life') > 0");
        genreMap.put("genre24.exclude","instr(genre_tags, 'School life') = 0");
        genreMap.put("genre25.include","instr(genre_tags, 'Sci fi') > 0");
        genreMap.put("genre25.exclude","instr(genre_tags, 'Sci fi') = 0");
        genreMap.put("genre26.include","instr(genre_tags, 'Seinen') > 0");
        genreMap.put("genre26.exclude","instr(genre_tags, 'Seinen') = 0");
        genreMap.put("genre27.include","instr(genre_tags, 'Shoujo') > 0");
        genreMap.put("genre27.exclude","instr(genre_tags, 'Shoujo') = 0");
        genreMap.put("genre28.include","instr(genre_tags, 'Shoujo ai') > 0");
        genreMap.put("genre28.exclude","instr(genre_tags, 'Shoujo ai') = 0");
        genreMap.put("genre29.include","instr(genre_tags, 'Shounen') > 0");
        genreMap.put("genre29.exclude","instr(genre_tags, 'Shounen') = 0");
        genreMap.put("genre30.include","instr(genre_tags, 'Shounen ai') > 0");
        genreMap.put("genre30.exclude","instr(genre_tags, 'Shounen ai') = 0");
        genreMap.put("genre31.include","instr(genre_tags, 'Slice of life') > 0");
        genreMap.put("genre31.exclude","instr(genre_tags, 'Slice of life') = 0");
        genreMap.put("genre32.include","instr(genre_tags, 'Smut') > 0");
        genreMap.put("genre32.exclude","instr(genre_tags, 'Smut') = 0");
        genreMap.put("genre33.include","instr(genre_tags, 'Sports') > 0");
        genreMap.put("genre33.exclude","instr(genre_tags, 'Sports') = 0");
        genreMap.put("genre34.include","instr(genre_tags, 'Supernatural') > 0");
        genreMap.put("genre34.exclude","instr(genre_tags, 'Supernatural') = 0");
        genreMap.put("genre35.include","instr(genre_tags, 'Tragedy') > 0");
        genreMap.put("genre35.exclude","instr(genre_tags, 'Tragedy') = 0");
        genreMap.put("genre36.include","instr(genre_tags, 'Webtoons') > 0");
        genreMap.put("genre36.exclude","instr(genre_tags, 'Webtoons') = 0");
        genreMap.put("genre37.include","instr(genre_tags, 'Yaoi') > 0");
        genreMap.put("genre37.exclude","instr(genre_tags, 'Yaoi') = 0");
        genreMap.put("genre38.include","instr(genre_tags, 'Yuri') > 0");
        genreMap.put("genre38.exclude","instr(genre_tags, 'Yuri') = 0");
    }
}
