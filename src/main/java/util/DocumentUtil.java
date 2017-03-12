package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by Yauheni on 05.02.17.
 */
public class DocumentUtil {
    public static Document get(String url) throws IOException {
        return Jsoup.connect(url).timeout(100*1000).get();
    }
}
