package app.parsers;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class SrtParser {

    private static Set<String> movieNames = new HashSet<>();

    //Total files in the directory
    private static int totalFiles = 0;

    //Total wrong files including files not ending in .srt or duplicate subtitles or bad file name
    private static int wrongFiles = 0;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please input two arguments: 1. raw data dir, 2. destination data dir ");
            return;
        }

        String rawDataDir = args[0];
        String destDataDir = "./../" + args[1];

        File f = new File(rawDataDir);
        File[] rawFiles = f.listFiles();

        if (rawFiles == null) {
            return;
        }
        for (File file : rawFiles) {
            //Count total srt files
            totalFiles++;

            if (!file.getName().endsWith(".srt")) {
                wrongFiles++;
                continue;
            }

            String movieName = getMovieName(file);
            if (movieName.equals("error") || movieNames.contains(movieName)) {
                //Invalid name or duplicate file with same name
                wrongFiles++;
                continue;
            }
            movieNames.add(movieName);

            int counter = 1;
            String sequence = String.format("%04d", counter);
            String extension = "txt";
            String fileName = destDataDir + "/" + movieName + "^" + sequence + "." + extension;


            String line;

            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                while ((line = bufferedReader.readLine()) != null) {

                    if (line.length() != 0 && StringUtils.isNumeric(line) && Integer.parseInt(line) % 10 == 1) {
                        writer.close();

                        counter++;
                        sequence = String.format("%04d", counter);
                        fileName = destDataDir + "/" + movieName + "^" + sequence + "." + extension;

                        writer = new BufferedWriter(new FileWriter(fileName));
                    }

                    writer.write(line);
                    writer.write("\n");
                }

                writer.close();
                bufferedReader.close();
                fileReader.close();

                System.out.println(counter + " files written for " + file.getName());
            } catch (IOException e) {
                wrongFiles++;
                e.printStackTrace();
            }

        }

        System.out.println();
        System.out.println(totalFiles + " files parsed! " + wrongFiles + " wrong files!");

    }

    private static String getMovieName(File file) {
        String name = file.getName();
        name = name.replaceAll("\\(.*\\)", "");
        name = name.replaceAll("\\[.*\\]", "");
        name = name.trim();

        String[] parts = name.split(" |-|_|\\.");

        //File invalid or bad file name
        if (parts.length < 2) {
            return "error";
        }

        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            part = part.trim();

            //Empty string
            if (part.length() == 0) {
                continue;
            }

            part = part.toLowerCase();

            //Movie resolution
            if (part.equals("720p") || part.equals("1080p")) {
                break;
            }

            //Subtitle language
            if (part.equals("en") || part.equals("eng") || part.equals("english")) {
                break;
            }

            //Web domain and suffix
            if (part.equals("yify") || part.equals("srt")) {
                break;
            }

            //Movie year
            if (StringUtils.isNumeric(part) && part.length() == 4 && sb.length() != 0) {
                break;
            }

            sb.append(part).append(" ");
        }

        //Invalid file name
        if (sb.length() == 0) {
            return "error";
        }

        return sb.substring(0, sb.length() - 1);
    }
}
