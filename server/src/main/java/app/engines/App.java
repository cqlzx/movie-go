package app.engines;

import app.engines.filters.TxtFilter;
import app.engines.indexers.SubtitleIndexer;

import java.io.IOException;

public class App {

    public static void main(String[] args) {
        App application = new App();

        if (args.length != 2) {
            System.out.println("Two parameters required!");
            System.out.println("1. Index directory");
            System.out.println("2. Data directory");
            return;
        }

        try {
            System.out.println("Indexing...");

            application.createIndex(args[0], args[1]);

            System.out.println("Indexed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createIndex(String indexDir, String dataDir) throws IOException {
        SubtitleIndexer indexer = new SubtitleIndexer(indexDir);

        long startTime = System.currentTimeMillis();

        int numIndexed = indexer.createIndex(dataDir, new TxtFilter());

        long endTime = System.currentTimeMillis();

        indexer.close();

        System.out.println("numIndexed: " + numIndexed + ". Time used: " + (endTime - startTime) + "ms");
    }
}
