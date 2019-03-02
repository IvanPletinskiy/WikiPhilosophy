package com.handen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Index {

    private Map<String, Set<TermCounter>> index = new HashMap<String, Set<TermCounter>>();

    public void add(String term, TermCounter tc) {
        Set<TermCounter> set = get(term);

        if(set == null) {
            set = new HashSet<TermCounter>();
            index.put(term, set);
        }

        set.add(tc);
    }

    public Set<TermCounter> get(String term) {
        return index.get(term);
    }
}
