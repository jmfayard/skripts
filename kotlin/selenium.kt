#!/usr/bin/env kotlin-script.sh
package selenium

import jmfayard.printList
import org.docopt.Docopt
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.WebDriverWait

/**
 * https://blog.kotlin-academy.com/your-first-selenium-data-scraper-in-kotlin-51ce22083fb9
 *
 */

private val HELP =
        """
Kotlin scripts for Android devs.

Usage:
  selenium.kt wbw
  selenium.kt hn

Options:
  -h --help         Show this screen.
  --version         Show version.
""".trim()

fun main(args: Array<String>) {
    val p: Map<String, Any> = Docopt(HELP).withVersion("0.1").parse(args.toList())

    when {
        p["wbw"] == true -> waitButWhyArchive()
        p["hn"] == true -> hackerNews()
        else -> println(HELP)
    }

}

fun hackerNews() {
    val driver = ChromeDriver()
    driver.get("https://news.ycombinator.com/login")
    driver.findElementByName("acct").sendKeys("jmfayard")
    driver.findElementByName("pw").sendKeys("RANychEStersTREB")
    driver.findElementByTagName("form").submit()
    driver.waitUntilPageIsReady()
    driver.get("https://news.ycombinator.com/upvoted?id=jmfayard")
    val titles = driver.findElementsByClassName("athing")
            .flatMap { element -> element.findElements(By.className("title")) }
            .flatMap { it.findElements(By.tagName("a")) }
            .map { it.getAttribute("href") to it.text }
    titles.printList("HN")
    driver.close()
    driver.quit()
}

fun waitButWhyArchive() {
    val driver = ChromeDriver()
    driver.get("https://waitbutwhy.com/archive")
    driver.waitUntilPageIsReady()
    val titles = driver.findElementsByClassName("post-right")
            .map { element -> element.findElement(By.tagName("H5")) }
            .map { it.findElement(By.tagName("A")) }
            .map { it.text }

    titles.printList("WaitButWhy")
    driver.close()
}

fun ChromeDriver.waitUntilPageIsReady(): ChromeDriver {
    val executor = this as JavascriptExecutor
    WebDriverWait(this, 1)
            .until { executor.executeScript("return document.readyState") == "complete" }
    return this
}