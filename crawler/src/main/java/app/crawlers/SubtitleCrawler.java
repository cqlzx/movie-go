package app.crawlers;

import com.google.common.io.Files;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class SubtitleCrawler extends WebCrawler {

    private static final Pattern filter =
            Pattern.compile("#*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf|rm|smil|wmv|swf|wma|rar|gz))#");

    private static final Pattern zipPatterns =
            Pattern.compile(".*(\\.(zip))$");

    private static final String downloadDomain = "http://www.yifysubtitles.com/subtitles/";

    private static String[] crawlDomains;

    private static File dataStorage;

    private int counter = 0;

//    private Set<String> set = new HashSet<>();

    public static void configure(String[] domains, String dataFolder) {
        crawlDomains = domains;
        dataStorage = new File(dataFolder);
        if (!dataStorage.exists()) {
            dataStorage.mkdirs();
        }
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        if (filter.matcher(href).matches()) {
            return false;
        }

        if (zipPatterns.matcher(href).matches()) {
            return true;
        }

        for (String domain : crawlDomains) {
            if (domain.equals(downloadDomain)) {
                if (href.endsWith(".zip")) {
                    String[] segments = href.split("-");
                    return segments.length > 2 && segments[segments.length - 2].equals("english");
                } else {
                    String[] segments = href.split("-");
                    return segments.length > 3 && segments[segments.length - 3].equals("english");
                }
            } else {
                if (href.startsWith(domain)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void visit(Page page) {
        logger.info("-----------------------" + counter++ + " pages visited!");
        String url = page.getWebURL().getURL();

        if (!zipPatterns.matcher(url).matches()
                || !(page.getParseData() instanceof BinaryParseData)
                || page.getContentData().length < 10 * 1024) {
            return;
        }

        String extension = url.substring(url.lastIndexOf("."));
        String hashName = UUID.randomUUID() + extension;

        String fileName = dataStorage.getAbsolutePath() + "/" + hashName;
        try {
            Files.write(page.getContentData(), new File(fileName));
            logger.info("Stored: {}", url);
        } catch (IOException e) {
            logger.error("Failed to write file: " + fileName);
            e.printStackTrace();
        }
    }
}
