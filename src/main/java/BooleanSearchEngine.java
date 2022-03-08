import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    //protected HashMap<String, PageEntry> mapOnePage = new HashMap<>();
    protected HashMap<String, ArrayList<PageEntry>> mapResult = new HashMap<>();
    //protected List<Map<String, PageEntry>> listAllPages = new ArrayList<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {

        List<Map<String, PageEntry>> listAllPages = new ArrayList<>();
        HashMap<String, PageEntry> mapOnePage = new HashMap<>();

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

                mapOnePage.clear();
                List<String> tempListKeys = new ArrayList<>(mapWordsCounts.keySet());
                for (int k = 0; k < tempListKeys.size(); k++) {
                    String tempWord = tempListKeys.get(k); //key
                    countOfWords = mapWordsCounts.get(tempWord);
                    pageEntry = new PageEntry(fileName, numberOfPage, countOfWords); // value
                    mapOnePage.put(tempWord, pageEntry); // Map для одной страницы
                }
                listAllPages.add(mapOnePage); // Лист из Мар для всех страниц, всех документов, 87 элементов-страниц в листе
            }
        }
        mapResult = modify(listAllPages); // 41
        System.out.println(mapResult);
        System.out.println(listAllPages.size());
        System.out.println(mapResult.size());
    }

    public HashMap<String, ArrayList<PageEntry>> modify(List<Map<String, PageEntry>> listAllPages) {
        HashMap<String, ArrayList<PageEntry>> map = new HashMap<>();

        for (int i = 0; i < listAllPages.size(); i++) {
            Map<String , PageEntry> mapI = listAllPages.get(i);
            for (Map.Entry entry: mapI.entrySet()) {

                String keyWord = (String) entry.getKey();
                PageEntry value = (PageEntry)entry.getValue();

                if(map.containsKey(keyWord)) {
                    ArrayList<PageEntry> exsistList = map.get(keyWord);
                    exsistList.add(value);
                } else {
                    map.put(keyWord, new ArrayList<>());
                    map.get(keyWord).add(value);
                }
            }
        }
        return map;
    }

    @Override
    public List<PageEntry> search(String word)  {
        ArrayList<PageEntry> list = new ArrayList<>();
        System.out.println("SEARCH: ");

        for (Map.Entry<String, ArrayList<PageEntry>> entry : mapResult.entrySet()) {
            if (entry.getKey().equals(word)) {
                list = entry.getValue();
            }
        }
        return list;
    }

    public static String writeToJson(List<PageEntry> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<PageEntry>>() {
        }.getType();
        String json = gson.toJson(list, listType);


        try (FileWriter fileWriter = new FileWriter("data.json")) {
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
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