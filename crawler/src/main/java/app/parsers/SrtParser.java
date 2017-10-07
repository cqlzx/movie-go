package app.parsers;

import org.apache.commons.lang.StringUtils;

import java.io.*;

public class SrtParser {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please input two arguments: 1. raw data dir, 2. destination data dir ");
            return;
        }

        String rawDataDir = args[0];
        String destDataDir = "./../" + args[1];

        try {
            File f = new File(rawDataDir);

//            System.out.println(f.getAbsolutePath());

            File[] rawFiles = f.listFiles();

            if (rawFiles == null) {
                return;
            }
//            System.out.println(rawFiles.length);

            for (File file : rawFiles) {
                if (!file.getName().endsWith(".srt")) {
                    continue;
                }

//                System.out.println(file.getName());

                int counter = 1;
                String sequence = String.format("%04d", counter);
                String movieName = getMovieName(file);
                String extension = "txt";
                String fileName = destDataDir + "/" + movieName + "^" + sequence + "." + extension;

                String line;
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getMovieName(File file) {
        String name = file.getName();
        String[] parts = name.split("\\.");

        //File invalid or bad file name
        if (parts.length < 2) {
            return "BadFile---" + name;
        }

        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            //Empty string
            if (part.length() == 0) {
                continue;
            }

            //Movie year, the end of movie name
            if (StringUtils.isNumeric(part) && part.length() == 4) {
                break;
            }

            sb.append(part).append(" ");
        }

        return sb.substring(0, sb.length() - 1);
    }
}
