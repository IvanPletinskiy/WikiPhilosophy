package com.handen;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

class TermCounter {
    /*
    static String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
    WikiFetcher wf = new WikiFetcher();
    Elements paragraphs = getPageParagraphs(url);
    TermCounter counter = new TermCounter(url);
    counter.processElements(paragraphs);
    count.printCounts();
*/

    private Map<String, Integer> map;
    private String label;

    public TermCounter(String label) {
        this.label = label;
        map = new HashMap<String, Integer>();
    }

    public void put(String term, int count) {
        map.put(term, count);
    }

    public Integer get(String term) {
        Integer count = map.get(term);
        return count == null ? 0 : count;
    }

    public void incrementTermCount(String term) {
        put(term, get(term) + 1);
    }

    public void processElements(Elements paragraphs) {
        for(Node node : paragraphs) {
            processTree(node);
        }
    }

    public void processTree(Node root) {
        for(Node node : new WikiNodeIterable(root)) {
            if(node instanceof TextNode) {
                //processText((TextNode) node)
            }
        }
    }

    public void processText(String text) {
        String[] array = text.replaceAll("\\pP", " ").toLowerCase().split("\\s+");

        for(int i = 0; i < array.length; ++i) {
            String term = array[i];
            incrementTermCount(term);
        }
    }

}
