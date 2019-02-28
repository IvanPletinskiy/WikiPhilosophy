package com.handen;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    static String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
    //  static String source = "https://ru.wikipedia.org/wiki/%D0%9E%D0%B1%D1%8B%D0%BA%D0%BD%D0%BE%D0%B2%D0%B5%D0%BD%D0%BD%D1%8B%D0%B9_%D1%81%D0%BA%D0%B2%D0%BE%D1%80%D0%B5%D1%86";
    static String destination = "https://en.wikipedia.org/wiki/Philosophy";
    static long lastRequestTime = -1;
    static int minInterval = 1000;
    static int parenthesisCount = 0;

    static ArrayList<String> visited = new ArrayList<>();

    public static void main(String[] args) throws IOException {
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
        for(org.jsoup.nodes.Node p : paragraphs) {
            List<Node> paragraphNodes = ((Element) p).childNodes();
            for(Node node : paragraphNodes) {
                if(node instanceof TextNode) {
                    processTextNode((TextNode) node);
                }
                if(node instanceof Element) {
                    processElement((Element) node);
                }
            }
        }
    }

    private static void processElement(Element element) {
        if (!element.tagName().equals("a")) {
            return;
        }
        // in italics
        if (isItalic(element)) {
            return;
        }
        // in parenthesis
        if (isInParens(element)) {
            return;
        }
        // a bookmark
        if (startsWith(element, "#")) {
            return;
        }
        // a Wikipedia help page
        if (startsWith(element, "/wiki/Help:")) {
            return;
        }
       
    }

    private static void processTextNode(TextNode node) {
        StringTokenizer st = new StringTokenizer(node.text(), " ()", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            // System.out.print(token);
            if (token.equals("(")) {
                parenthesisCount++;
            }
            if (token.equals(")")) {
                if(parenthesisCount == 0)
                    System.err.println("parenthesis wrong count");
                parenthesisCount--;
            }
        }
    }

    static private void sleepIfNeeded() {
        if(lastRequestTime != -1) {
            long currentTime = System.currentTimeMillis();
            long nextRequestTime = lastRequestTime + minInterval;
            if(currentTime < nextRequestTime) {
                try {
                    Thread.sleep(nextRequestTime - currentTime);
                }
                catch(InterruptedException e) {
                    System.err.println("Warning: sleep interrupted in fetchWikipedia.");
                }
            }
        }
        lastRequestTime = System.currentTimeMillis();
    }
}
