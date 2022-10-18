package application;

import static org.junit.Assert.assertEquals;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import application.JavaFxTest.Word;

class wordFindTest {

	@Test
	void wordFindTest() throws IOException{
		Map<String, Word> map = new HashMap<String, Word>(); 
		Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get(); 
		String text = doc.body().text();
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
	    
		JavaFxTest test = new JavaFxTest();
		Collection<Word> result = test.wordFind();
		assertEquals(sortedWords, result);
	}
}