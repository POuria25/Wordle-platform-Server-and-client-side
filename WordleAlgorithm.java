import java.util.*;

public class WordleAlgorithm {
    private Set<String> words;

    public WordleAlgorithm(Set<String> words) {
        if (words.isEmpty()) {
            throw new IllegalArgumentException("Word list cannot be empty");
        }
        this.words = words;
    }

    public String selectRandomword() {
        List<String> wordList = new ArrayList<>(words);
        Random word = new Random();
        int randomIndex = word.nextInt(wordList.size());
        return wordList.get(randomIndex);
    }

    public String checkWord(String word1, String word2) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < word1.length(); i++) {
            char serverWord = word1.charAt(i);
            char clientWord = word2.charAt(i);
            if (serverWord == clientWord) {
                result.append("G");
            } else if (word1.indexOf(clientWord) == -1) {
                result.append("B");
            } else {
                result.append("X");
            }
        }

        Map<Character, Integer> word1Count = new HashMap<>();
        for (int i = 0; i < word1.length(); i++) {
            if (result.charAt(i) != 'G')
                word1Count.put(word1.charAt(i), word1Count.getOrDefault(word1.charAt(i), 0) + 1);
        }

        for (int i = 0; i < word1.length(); i++) {
            if (result.charAt(i) == 'X') {
                char guessChar = word2.charAt(i);
                if (word1Count.containsKey(guessChar) && word1Count.get(guessChar) > 0) {
                    result.setCharAt(i, 'Y');
                    word1Count.put(guessChar, word1Count.get(guessChar) - 1);
                } else {
                    result.setCharAt(i, 'B');
                }
            }
        }
        return result.toString();
    }
}
