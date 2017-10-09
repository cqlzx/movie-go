package app.controllers;

import app.engines.constants.SubtitleConstants;
import app.engines.searchers.SubtitleSearcher;
import app.models.Movie;
import app.models.SubtitleSnippet;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MovieController {

    @RequestMapping("/movies")
    public List<Movie> movies(@RequestParam(value = "query", defaultValue = "a") String query) {

        Map<String, Movie> map = new HashMap<>();
        SubtitleSearcher searcher;

        try {
            searcher = new SubtitleSearcher("indexes");

            long startTime = System.currentTimeMillis();

            TopDocs hits;
            hits = searcher.search(query);

            long endTime = System.currentTimeMillis();

            System.out.println(hits.totalHits + " documents found. Time used: " + (endTime - startTime) + "ms");

            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                String fileName = doc.get(SubtitleConstants.FILE_NAME);
                String[] parts = fileName.split("\\^");
                String movieName = parts[0];

                if (!map.containsKey(movieName)) {
                    map.put(movieName, new Movie(movieName));
                }

                Movie movie = map.get(movieName);
                String[] snapshotAndContent = this.readFile(doc.get(SubtitleConstants.FILE_PATH), query, movie.getSnapshot());

                if (movie.getSnapshot() == null) {
                    movie.setSnapshot(snapshotAndContent[0]);
                }
                movie.getSnippets().add(new SubtitleSnippet(snapshotAndContent[1]));

                if (map.size() >= 100) {
                    break;
                }
                System.out.println("File: " + doc.get(SubtitleConstants.FILE_NAME));
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }

        return new ArrayList<>(map.values());
    }

    private String[] readFile(String path, String query, String snapshot) {
        String[] res = new String[2];

        String matchLine = "";
        InputStream is;
        StringBuilder sb = new StringBuilder();
        try {
            is = new FileInputStream(path);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();

            String[] parts = query.split(" ");
            int maxMatch = 0;
            while(line != null){
                if (snapshot == null) {
                    int match = 0;

                    for (String part : parts) {
                        if (line.contains(part)) {
                            match++;
                        }
                    }

                    //The line with the max match will be the snapshot
                    if (match > maxMatch) {
                        maxMatch = match;
                        matchLine = line;
                    }
                }

                sb.append(line).append("\n");
                line = buf.readLine();
            }

            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        res[0] = matchLine;
        res[1] = sb.toString();

        return res;
    }
}
