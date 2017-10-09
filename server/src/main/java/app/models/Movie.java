package app.models;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String name;

    private String snapshot;

    private List<SubtitleSnippet> snippets;

    public Movie(String name_) {
        name = name_;
        snippets = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public List<SubtitleSnippet> getSnippets() {
        return snippets;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }
}
