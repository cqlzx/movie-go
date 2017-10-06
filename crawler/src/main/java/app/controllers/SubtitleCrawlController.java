package app.controllers;

import app.crawlers.SubtitleCrawler;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubtitleCrawlController {
    private static final Logger logger = LoggerFactory.getLogger(SubtitleCrawlController.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            logger.info("3 parameters Needed: ");
            logger.info("\t rootFolder (it will contain intermediate crawl data)");
            logger.info("\t numberOfCralwers (number of concurrent threads)");
            logger.info("\t storageFolder (a folder for storing downloaded images)");
            return;
        }

        String dataFolder = args[2];
        String rootFolder = args[0];
        int numOfCrawler = Integer.parseInt(args[1]);

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(rootFolder);

        config.setPolitenessDelay(200);

        config.setMaxPagesToFetch(100);



        config.setIncludeBinaryContentInCrawling(true);

        String[] domains = {
//                "http://www.yifysubtitles.com/movie-imdb/tt4703048",
//                "http://www.yifysubtitles.com/subtitles/6-days-english-yify-115233",
                "http://www.yifysubtitles.com/language/english",
                "http://www.yifysubtitles.com/movie-imdb/",
                "http://www.yifysubtitles.com/subtitles/",
        };

        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController crawlController = new CrawlController(config, pageFetcher, robotstxtServer);

        for (String domain : domains) {
            crawlController.addSeed(domain);
        }

        SubtitleCrawler.configure(domains, dataFolder);

        crawlController.start(SubtitleCrawler.class, numOfCrawler);

    }
}
