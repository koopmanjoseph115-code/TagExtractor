import java.io.*;
import java.util.*;

public class TagExtractorEngine {
    private Set<String> stopWords;
    private Map<String, Integer> tagMap;

    public TagExtractorEngine() {
        // TreeSet keeps stop words sorted automatically as a by-product
        this.stopWords = new TreeSet<>();
        // TreeMap keeps the final extracted tags sorted alphabetically
        this.tagMap = new TreeMap<>();
    }

    // Load stop words from the selected file into the Set
    public void loadStopWords(File file) throws IOException {
        stopWords.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    stopWords.add(line);
                }
            }
        }
    }

    // Process the text document and extract tag frequencies
    public void processDocument(File file) throws IOException {
        tagMap.clear(); // Reset map for a new scan
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String word = scanner.next();

                // Normalize: remove non-letter characters and force lowercase
                word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();

                // Filter out empty strings and stop words
                if (!word.isEmpty() && !stopWords.contains(word)) {
                    tagMap.put(word, tagMap.getOrDefault(word, 0) + 1);
                }
            }
        }
    }

    public Map<String, Integer> getTagMap() {
        return tagMap;
    }

    public boolean hasStopWordsLoaded() {
        return !stopWords.isEmpty();
    }
}
