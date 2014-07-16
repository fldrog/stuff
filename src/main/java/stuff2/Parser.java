package stuff2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    static Pattern startFlights = Pattern.compile("<span class=\"flights_daylist\"");
    static Pattern startSpan = Pattern.compile("<span ");
    static Pattern endSpan = Pattern.compile("</span>");

    public static void main(String[] args) throws IOException {
        Path file =
                Paths.get("/Users/florinbotis/Documents/eclipse_default_workspace/stuff2/src/main/resources/test.xml");

        String fileContents = new String(Files.readAllBytes(file), Charset.forName("UTF-8"));
        Matcher flightsMatcher = startFlights.matcher(fileContents);

        while (flightsMatcher.find()) {
            int start = flightsMatcher.start();
            int end = -1;

            Matcher startSpanMatch = startSpan.matcher(fileContents.substring(flightsMatcher.end()));
            Matcher endSpanMatcher = endSpan.matcher(fileContents.substring(flightsMatcher.end()));

            if (startSpanMatch.find()) {
                if (endSpanMatcher.find()) {
                    if (startSpanMatch.start() > endSpanMatcher.start()) {
                        end = endSpanMatcher.end();
                        System.out.println(fileContents.subSequence(start, start + end));
                        continue;
                    }
                }
            }

            if (startSpanMatch.find()) {
                if (endSpanMatcher.find()) {
                    if (startSpanMatch.start() > endSpanMatcher.start()) {
                        end = endSpanMatcher.end();
                        System.out.println(fileContents.subSequence(start, start + end));
                        continue;
                    }
                }
            }

        }

    }
}
