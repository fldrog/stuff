package stuff2;

import java.io.BufferedWriter;
import java.io.IOException;
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
import org.openqa.selenium.WebDriver;
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

        WebDriver driver = new PhantomJSDriver();
        // And now use this to visit Google
        driver.get("http://wizzair.com/en-GB/TimeTable");
        List<String> departures = getDepartures(driver);
        driver.quit();

        for (String departure : departures) {
            driver = new PhantomJSDriver();
            driver.get("http://wizzair.com/en-GB/TimeTable");
            ((JavascriptExecutor) driver)
                    .executeScript("document.getElementById('WizzTimeTableControl_TxtDeparture').value='" + departure
                            + "'");
            // Find the text input element by its name
            WebElement element = driver.findElement(By.id("WizzTimeTableControl_ButtonSubmit"));
            // Now submit the form. WebDriver will find the form for us from the element
            element.submit();
            WebElement selectTo = driver.findElement(By.id("WizzTimeTableControl_AutocompleteTxtArrival"));
            selectTo.click();

            List<String> arrivals = getArrivals(driver);
            for (String arrival : arrivals) {
                ((JavascriptExecutor) driver)
                        .executeScript("document.getElementById('WizzTimeTableControl_TxtArrival').value='" + arrival
                                + "'");
                element = driver.findElement(By.id("WizzTimeTableControl_ButtonSubmit"));
                element.submit();
                element = driver.findElement(By.id("WizzTimeTableControl_ButtonSubmit"));
                element.click();

                saveFile(departure, arrival, driver.getPageSource());
            }
            driver.quit();
        }

        // Close the browser
        // driver.quit();
    }

    private static void saveFile(String departure, String arrival, String pageSource) {
        // Create a new Path
        SimpleDateFormat dateFormat = new SimpleDateFormat("-yyyy-MM-dd_HH:mm");
        String filename = departure + "-" + arrival + dateFormat.format(new Date());
        Path newFile = Paths.get(filename + ".html");
        try {
            Files.deleteIfExists(newFile);
            newFile = Files.createFile(newFile);
        } catch (IOException ex) {
            System.out.println("Error creating file");
            ex.printStackTrace();
        }

        // Writing to file4
        try (BufferedWriter writer = Files.newBufferedWriter(newFile, Charset.defaultCharset())) {
            writer.append(pageSource);
            writer.flush();
        } catch (IOException exception) {
            System.out.println("Error writing to file");
            exception.printStackTrace();
        }
        System.out.println(filename + " Saved");
    }

    private static List<String> getArrivals(WebDriver driver) {
        List<String> arrivals = new ArrayList<String>();
        WebElement loc =
                driver.findElements(By.tagName("div").className("box-autocomplete").className("inContent")).get(1);

        List<WebElement> toels = loc.findElements(By.tagName("li"));
        for (WebElement el : toels) {
            arrivals.add(el.getAttribute("data-iata"));
        }
        return arrivals;
    }

    private static List<String> getDepartures(WebDriver driver) {
        List<String> departures = new ArrayList<String>();
        WebElement loc = driver.findElement(By.tagName("div").className("box-autocomplete").className("inContent"));
        List<WebElement> toels = loc.findElements(By.tagName("li"));
        for (WebElement el : toels) {
            departures.add(el.getAttribute("data-iata"));
        }
        return departures;
    }
}
