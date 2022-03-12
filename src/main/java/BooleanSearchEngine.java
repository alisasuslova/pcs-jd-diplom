import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    protected HashMap<String, List<PageEntry>> mapResult = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) {

        HashMap<String, PageEntry> mapOnePage = new HashMap<>();

        try {
            File dir = new File(String.valueOf(pdfsDir));
            File[] fileArray = dir.listFiles();

            String fileName;
            int numberOfPage;
            int countOfWords;


            for (int i = 0; i < fileArray.length; i++) {
                var doc = new PdfDocument(new PdfReader(fileArray[i]));
                int numberOfPages = doc.getNumberOfPages();
                fileName = fileArray[i].toString();

                for (int j = 1; j < numberOfPages + 1; j++) {
                    numberOfPage = j;
                    PdfPage onePage = doc.getPage(j);
                    var text = PdfTextExtractor.getTextFromPage(onePage);
                    var words = text.split("\\P{IsAlphabetic}+");

                    HashMap<String, Integer> mapWordsCounts = unique(words);

                    PageEntry pageEntry;
                    List <PageEntry> listValues = new ArrayList<>();

                    List<String> tempListKeys = new ArrayList<>(mapWordsCounts.keySet());
                    for (int k = 0; k < tempListKeys.size(); k++) {
                        String tempWord = tempListKeys.get(k);
                        countOfWords = mapWordsCounts.get(tempWord);
                        pageEntry = new PageEntry(fileName, numberOfPage, countOfWords);
                        mapOnePage.put(tempWord, pageEntry);
                        if (!mapResult.containsKey(tempWord)) {
                            listValues = new ArrayList<>();
                            listValues.add(pageEntry);
                            mapResult.put(tempWord, listValues);
                        } else {
                            mapResult.get(tempWord).add(pageEntry);
                            Collections.sort(mapResult.get(tempWord), Collections.reverseOrder());
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PageEntry> search(String word)  {
        word = word.toLowerCase();
        return mapResult.get(word);
    }

    public static HashMap<String, Integer> unique(String[] words) {

        HashSet<String> distinctKey = new HashSet<>();
        HashMap<String, Integer> map = new HashMap<>();

        for (int i = 0; i < words.length; i++) {
            if (distinctKey.add(words[i]))
                map.put(words[i], 1);
            else
                map.put(words[i], (map.get(words[i])) + 1);
        }
        return map;
    }
}