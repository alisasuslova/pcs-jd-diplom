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
            File dir = new File(String.valueOf(pdfsDir)); //путь указывает на директорию
            File[] fileArray = dir.listFiles(); //содержимое папки, все файлы в одном массиве

            String fileName; // для PageEntry, поля pdfName
            int numberOfPage;  // для PageEntry, поля page
            int countOfWords;  // для PageEntry, поля count


            for (int i = 0; i < fileArray.length; i++) { // для каждого документа в директории "pdfs"
                var doc = new PdfDocument(new PdfReader(fileArray[i])); //создаем объект pdf-документа, pdf-файл целиком, для каждого из файлов в директории
                int numberOfPages = doc.getNumberOfPages(); // получаем кол-во страниц в этом pdf-документе
                fileName = fileArray[i].toString(); //дальше для PageEntry, поля pdfName

                for (int j = 1; j < numberOfPages + 1; j++) { // для одного документа листаем все его страницы, получаем все страницы как отдельные объекты. 1 и +1 империчеким путем
                    numberOfPage = j; // номер страницы для PageEntry, поля page
                    PdfPage onePage = doc.getPage(j); // onePage - получили объект одной страницы документа
                    var text = PdfTextExtractor.getTextFromPage(onePage); //получили текст с этой страницы
                    var words = text.split("\\P{IsAlphabetic}+"); // разбиваем текст на слова, получаем массив слов на одной странице

                    HashMap<String, Integer> mapWordsCounts = unique(words);// Map уникальных слов и их количество для одной страницы

                    PageEntry pageEntry;
                    List <PageEntry> listValues = new ArrayList<>();

                    List<String> tempListKeys = new ArrayList<>(mapWordsCounts.keySet());
                    for (int k = 0; k < tempListKeys.size(); k++) {
                        String tempWord = tempListKeys.get(k); //key
                        countOfWords = mapWordsCounts.get(tempWord);
                        pageEntry = new PageEntry(fileName, numberOfPage, countOfWords); // value
                        mapOnePage.put(tempWord, pageEntry); // Map для одной страницы
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

    public static HashMap<String, Integer> unique(String[] words) { //работает правильно!

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