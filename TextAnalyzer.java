package cen3024c;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TextAnalyzer { 
	public static void main(String[] args) throws IOException {
		Map<String, Word> map = new HashMap<String, Word>();
        Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();

        //Get the text from the page, excluding HTML
        String text = doc.body().text();

        //Create BufferedReader to count the allWords
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
        String line;
        
        while ((line = reader.readLine()) != null) {
            String[] allWords = line.split(" ");
            for (String word : allWords) {
                Word wordFreq = map.get(word);
                if (wordFreq == null) {
                    wordFreq = new Word();
                    wordFreq.word = word;
                    wordFreq.count = 0;
                    map.put(word, wordFreq);
                }
                wordFreq.count++;
            }
        }

        reader.close();
 
        SortedSet<Word> sortedWords = new TreeSet<Word>(map.values());
        
        int i = 0;
        int maxWords = 20;
        System.out.println("The " + maxWords + " most used words:");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
        
        for (Word currentWord : sortedWords) {
            if (i >= maxWords) { 
                break;
            }
            System.out.println(currentWord.word + "\t" + currentWord.count );
            i++;
       }
   }

   public static class Word implements Comparable<Word> {
	   String word;
       int count;

       @Override
       public int hashCode() {
    	   return word.hashCode(); 
       }

       @Override
       public boolean equals(Object obj) { 
    	   return word.equals(((Word)obj).word);
       }

       @Override
       public int compareTo(Word a) { 
       return a.count - count;
       }
   }
}