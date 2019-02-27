package com.handen;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    static String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
    static String destination = "https://en.wikipedia.org/wiki/Philosophy";
    static long lastRequestTime = -1;
    static int minInterval = 1000;

    static ArrayList<String> visited = new ArrayList<>();

    public static void main(String [] args) throws IOException{
        processPage(source, 0);
    }

    static void processPage(String title, int depth) throws IOException {
        if(title.equals(destination)) {
            System.out.println("Found! " + "depth: " + depth);
            System.exit(0);
        }

        for(String s : visited) {
            if(title.equals(s)) {
                System.out.println("Already visited! " + title);
                return;
            }
        }
        visited.add(title);

        sleepIfNeeded();

        Connection conn = Jsoup.connect(title);
        Document doc = conn.get();

       // Element content = doc.getElementById("mv-content-text");
        Element content = doc.getElementById("mw-content-text");

        Elements paragraphs = content.select("p");
        for(org.jsoup.nodes.Node node : paragraphs) {
            if(node instanceof TextNode) {
                System.out.println(node);
            }
        }
    }

    static private void sleepIfNeeded() {
        if (lastRequestTime != -1) {
            long currentTime = System.currentTimeMillis();
            long nextRequestTime = lastRequestTime + minInterval;
            if (currentTime < nextRequestTime) {
                try {
                    Thread.sleep(nextRequestTime - currentTime);
                } catch (InterruptedException e) {
                    System.err.println("Warning: sleep interrupted in fetchWikipedia.");
                }
            }
        }
        lastRequestTime = System.currentTimeMillis();
    }
}
