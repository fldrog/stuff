package stuff2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class Stuff {
    public static void main(String[] args) throws InterruptedException {
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface,
        // // not the implementation.
        if (args != null && args.length > 0) {
            System.setProperty("phantomjs.binary.path", args[0]);
        }

        PhantomJSDriver driver = new PhantomJSDriver();
        try {
            List<String> departures = getDepartures(driver);

            for (String departure : departures) {
                List<String> arrivals = getArrivals(driver, departure);

                for (String arrival : arrivals) {
                    String pageSrc = getHTMLPage(driver, departure, arrival);

                    if (driver.getPageSource().contains("price")) {
                        saveFile(departure, arrival, pageSrc);
                    } else {
                        saveFile(departure, arrival + "_ERROR", pageSrc);
                    }
                }

            }
        } finally {
            driver.quit();
        }

        // Close the browser
        // driver.quit();
    }

    private static String getHTMLPage(PhantomJSDriver driver, String departure, String arrival) {
        navigateToTimetable(driver);
        ((JavascriptExecutor) driver)
                .executeScript("document.getElementById('WizzTimeTableControl_TxtDeparture').value='" + departure + "'");
        // Find the text input element by its name
        WebElement submitButton = driver.findElement(By.id("WizzTimeTableControl_ButtonSubmit"));
        // Now submit the form. WebDriver will find the form for us from the element
        submitButton.submit();

        ((JavascriptExecutor) driver)
                .executeScript("document.getElementById('WizzTimeTableControl_TxtArrival').value='" + arrival + "'");

        submitButton = driver.findElement(By.id("WizzTimeTableControl_ButtonSubmit"));
        submitButton.submit();

        submitButton = driver.findElement(By.id("WizzTimeTableControl_ButtonSubmit"));
        submitButton.click();
        String pageSrc = driver.getPageSource();

        return pageSrc;
    }

    private static void saveFile(String departure, String arrival, String pageSource) {
        // Create a new Path
        SimpleDateFormat dateFormat = new SimpleDateFormat("-HH-mm");
        SimpleDateFormat dirDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Path dir = Paths.get(dirDateFormat.format(new Date()));
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
            }
            String filename = departure + "-" + arrival + dateFormat.format(new Date());
            Path newFile = Paths.get(dir.toAbsolutePath().toString(), filename + ".html");
            Files.deleteIfExists(newFile);
            newFile = Files.createFile(newFile);
            System.out.println(newFile.toAbsolutePath() + " Saved");
            try (BufferedWriter writer = Files.newBufferedWriter(newFile, Charset.forName("UTF-8"))) {
                writer.write(pageSource);
                writer.flush();
            }
        } catch (IOException ex) {
            System.out.println("Error creating file");
            PrintStream print = new PrintStream(System.out);
            ex.printStackTrace(print);
        }

        // Writing to file4

    }

    private static List<String> getArrivals(PhantomJSDriver driver, String departure) {
        navigateToTimetable(driver);

        ((JavascriptExecutor) driver)
                .executeScript("document.getElementById('WizzTimeTableControl_TxtDeparture').value='" + departure + "'");
        WebElement element = driver.findElement(By.id("WizzTimeTableControl_ButtonSubmit"));
        element.submit();
        WebElement selectTo = driver.findElement(By.id("WizzTimeTableControl_AutocompleteTxtArrival"));
        selectTo.click();

        List<String> arrivals = new ArrayList<String>();
        WebElement loc =
                driver.findElements(By.tagName("div").className("box-autocomplete").className("inContent")).get(1);

        List<WebElement> toels = loc.findElements(By.tagName("li"));
        for (WebElement el : toels) {
            arrivals.add(el.getAttribute("data-iata"));
        }

        return arrivals;
    }

    private static void navigateToTimetable(PhantomJSDriver driver) {
        driver.manage().deleteAllCookies();
        driver.get("http://wizzair.com/en-GB/TimeTable");
    }

    private static List<String> getDepartures(PhantomJSDriver driver) {
        navigateToTimetable(driver);
        List<String> departures = new ArrayList<String>();
        WebElement loc = driver.findElement(By.tagName("div").className("box-autocomplete").className("inContent"));
        List<WebElement> toels = loc.findElements(By.tagName("li"));
        for (WebElement el : toels) {
            departures.add(el.getAttribute("data-iata"));
        }

        return departures;
    }
}
