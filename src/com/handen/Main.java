package com.handen;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Start: " + "\n");
        source = reader.readLine().trim();
        System.out.println("\n" + "Destination: " + "\n");
      //  destination = reader.readLine().trim();
        System.out.println(destination);
        System.out.println("\n");
        processPage(source.substring(source.lastIndexOf("/") + 1), source, 0);
    }

    static void processPage(String title, String adress, int depth) throws IOException {
        if(adress.equals(destination)) {
            System.out.println("Found! " + "\t" + "depth: " + depth);
            System.exit(0);
        }

        for(String s : visited) {
            if(adress.equals(s)) {
                System.err.println(title + "\t" + "Already visited!");
                return;
            }
        }

        System.out.println(title + "\t" + "depth: " + depth);

        visited.add(adress);
        sleepIfNeeded();

        Elements paragraphs = getPageParagraphs(adress);

        for(org.jsoup.nodes.Node p : paragraphs) {
            List<Node> paragraphNodes = ((Element) p).childNodes();
            for(Node node : paragraphNodes) {
                if(node instanceof TextNode) {
                    processTextNode((TextNode) node);
                }
                if(node instanceof Element) {
                    processElement((Element) node, depth);
                }
            }
        }
    }

    public static Elements getPageParagraphs(String adress) throws IOException {
        Connection conn = Jsoup.connect(adress);
        Document doc = conn.get();
        Element content = doc.getElementById("mw-content-text");

        Elements paragraphs = content.select("p");
        return paragraphs;
    }

    private static void processElement(Element element, int depth) throws IOException {
        if(!element.tagName().equals("a")) {
            return;
        }
        // in italics
        if(isItalic(element)) {
            return;
        }
        // in parenthesis
        if(isInParens(element)) {
            return;
        }
        // a bookmark
        if(startsWith(element, "#")) {
            return;
        }
        // a Wikipedia help page
        if(startsWith(element, "/wiki/Help:")) {
            return;
        }
        String title = element.attr("title");
        String href = element.attr("href");
        String adress = "https://en.wikipedia.org" + href;
        processPage(title, adress, depth + 1);
    }

    private static void processTextNode(TextNode node) {
        StringTokenizer st = new StringTokenizer(node.text(), " ()", true);
        while(st.hasMoreTokens()) {
            String token = st.nextToken();
            // System.out.print(token);
            if(token.equals("(")) {
                parenthesisCount++;
            }
            if(token.equals(")")) {
                if(parenthesisCount == 0)
                    System.err.println("parenthesis wrong count");
                parenthesisCount--;
            }
        }
    }

    private static boolean startsWith(Element elt, String s) {
        //System.out.println(elt.attr("href"));
        return (elt.attr("href").startsWith(s));
    }

    private static boolean isInParens(Element elt) {
        // check whether there are any parentheses on the stack
        return parenthesisCount != 0;
    }

    private static boolean isItalic(Element start) {
        // follow the parent chain until we get to null
        for(Element elt = start; elt != null; elt = elt.parent()) {
            if(elt.tagName().equals("i") || elt.tagName().equals("em")) {
                return true;
            }
        }
        return false;
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

