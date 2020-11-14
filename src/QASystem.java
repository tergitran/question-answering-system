import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import info.debatty.java.stringsimilarity.Cosine;

public class QASystem {
    Pattern perType, timeType, numType, placeType;
    Pattern NG_QType, TG_QType, SL_Qtype, DD_QType, NA_QType, DT_QType, DC_QType, CD_QType, VT_QType;
    public String list_key = "";

    // Khởi tạo các biểu thức chính quy
    QASystem() {
        NG_QType = Pattern.compile(".*(\sai|Ai|vị.*nào).*");
        TG_QType = Pattern.compile(".*(((khi|thời điểm|thời kỳ|thời gian) nào)|(từ bao giờ)|(bao lâu)).*");
        NA_QType = Pattern.compile(".*(Năm|năm) (nào|bao nhiêu).*");
        DD_QType = Pattern.compile(
                "(.*(nơi|Nơi|sông|Sông|hang|Hang) (nào).*)|(.*((T|t)ên gọi khác|tên gọi|được gọi).*)");
        DT_QType = Pattern.compile(".*(d|D)iện tích.*");
        DC_QType = Pattern.compile(".*(Độ|độ|Chiều|chiều)* cao (bao nhiêu)*.*");
        CD_QType = Pattern.compile(".*(((Độ|độ|Chiều|chiều|) dài)|(cách .* bao (nhiêu|xa))).*");
        SL_Qtype = Pattern.compile(".*((b|B)ao nhiêu|(c|C)ó mấy).*");
        VT_QType = Pattern.compile("(.*(T|t)ỉnh nào.*)|(.*ở đâu.*)");
    }

    // Phân loại câu hỏi dựa vào biểu thức chính quy
    String getType(String q) {
        boolean check = false;

        check = NG_QType.matcher(q).matches();
        if (check)
            return "NG";

        check = TG_QType.matcher(q).matches();
        if (check)
            return "TG";

        check = DD_QType.matcher(q).matches();
        if (check)
            return "DD";

        check = NA_QType.matcher(q).matches();
        if (check)
            return "NA";

        check = DT_QType.matcher(q).matches();
        if (check)
            return "DT";

        check = DC_QType.matcher(q).matches();
        if (check)
            return "DC";

        check = CD_QType.matcher(q).matches();
        if (check)
            return "CD";

        check = SL_Qtype.matcher(q).matches();
        if (check)
            return "SL";

        check = VT_QType.matcher(q).matches();
        if (check)
            return "VT";

        return "";
    }



    // Truy vấn văn bản trên bộ dữ liệu bất kỳ với path là đường dẫn chứa dữ liệu
    ArrayList<String> getDocs(String path, String query) {
        Search obj = new Search("index"); // "index" là thư mục lucene lưu chỉ mục
        ArrayList<String> result = obj.search(query); // Trả về danh sách tên files
        ArrayList<String> docs = new ArrayList<String>(); // Lưu nội dung các file vào mảng docs
        String dir = path + "\\";
        try {
            for (String s : result) {
                BufferedReader r;

                r = new BufferedReader(new InputStreamReader(new FileInputStream(dir + s)));

                String st;
                String content = "";
                while ((st = r.readLine()) != null) {
                    content += st + "\n";
                }
                docs.add(content);

                r.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docs;
    }

    // Truy vấn văn bản trên bộ dữ liệu mặc định của hệ thống
    ArrayList<String> getDocs(String query) {
        Search obj = new Search("index");
        ArrayList<String> result = obj.search(query);
        ArrayList<String> docs = new ArrayList<String>();
        String dir = "data";
        try {
            
            for (String s : result) {
                BufferedReader r;

                r = new BufferedReader(new InputStreamReader(new FileInputStream(dir + s)));

                String st;
                String content = "";
                while ((st = r.readLine()) != null) {
                    content += st + "\n";
                }
                docs.add(content);

                r.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docs;
    }


    // Hàm trả về danh sách các câu văn có chứa thực thể
    public static ArrayList<String> getSentence(String source, String word) {
        ArrayList<String> sentences = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(source);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String s = source.substring(start, end);
            if (s.contains(word)) {
                s = s.replaceAll("\n", "");
                sentences.add(s);
            }
        }
        return sentences;
    }


    // Hàm trả về danh sách các thực thể và score tương ứng, được sắp xếp theo độ chính xác giảm dần
    public String getResult(String path, String query) {

        // Phân loại câu hỏi
        String typ = this.getType(query);
        System.out.println(typ);

        // Truy vấn văn bản: chỉ lấy văn bản có score cao nhất
        ArrayList<String> docs = this.getDocs(path, query);
        String doc = docs.get(0);
        System.out.println(docs.size());


        System.out.println("==================================================================");

        // Rút trích thực thể trên văn bản vừa tìm được
        NERecognition obj = new NERecognition("NER.dat");
        String doc_format = obj.formatDoc(doc);  // Tiền xử lý các docs
        ArrayList<String> entities = obj.getEntities(doc_format, typ);

        // Loại bỏ thực thể trùng lặp
        Set<String> set = entities.stream().collect(Collectors.toSet());
        System.out.println(Arrays.toString(set.toArray()));
        System.out.println(set.size());


        // Xếp hạng thực thể, hàm trả về tên các thực thể và score tương 
        Map<String, Double> map = new HashMap<String, Double>();
        for (String entity : set) {
            ArrayList<String> sentences = getSentence(doc, entity); // danh sách các câu văn chứa thực thể entity
            for (String s : sentences) {
                Double dist = new Cosine().distance(query, s);  // Khoảng cách cosine giữa câu văn chứa thực thể và câu truy vấn
                if (!map.containsKey(entity))
                    map.put(entity, dist);

                else if (map.get(entity) > dist) { // Chỉ lưu score của câu văn có khoảng cách cosine nhỏ nhất ứng với từng thực thể
                    map.put(entity, dist);
                }
            }
        }

        System.out.println("********************* RESULT ******************");

        // Tìm score thấp nhất
        Double min = 1.0;
        String key_min = "";
        for (String key : map.keySet()) {
            if (map.get(key) < min) {
                min = map.get(key);
                key_min = key;
            }
        }


        // Sắp xếp theo thứ hạng và xuât kết quả
        Map<String, Double> sortedMap = map.entrySet().stream().sorted(Entry.comparingByValue())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        sortedMap.forEach((k,v) -> {
            list_key += k + ": " + v + "\n" ;
        });

        System.out.println(list_key);
        System.out.println("Câu trả lời: " + key_min);
        return key_min;
    }

    public static void main(String[] args) {

        QASystem test = new QASystem();

        // Câu truy vấn đưa vào
        String query = "";
        query = "Tam Cốc - Bích động còn biết đến với tên gọi nào";


        // Phân loại câu hỏi
        String typ = test.getType(query);
        System.out.println(typ);


        // Truy vấn văn bản: chỉ lấy văn bản có score cao nhất
        ArrayList<String> docs = test.getDocs(query);
        String doc = docs.get(0);


        System.out.println("==================================================================");

        // Rút trích thực thể
        NERecognition obj = new NERecognition("NER.dat");
        String doc_format = obj.formatDoc(doc); // Tiền xử lý các docs
        ArrayList<String> entities = obj.getEntities(doc_format, typ);
        
        // Loại bỏ thực thể trùng lặp
        Set<String> set = entities.stream().collect(Collectors.toSet());
        System.out.println(Arrays.toString(set.toArray()));
        System.out.println(set.size());

        // Xếp hạng thực thể
        Map<String, Double> map = new HashMap<String, Double>();
        for (String entity : set) {
            ArrayList<String> sentences = getSentence(doc, entity);
            for (String s : sentences) {
                Double dist = new Cosine().distance(query, s);
                if (!map.containsKey(entity))
                    map.put(entity, dist);
                else if (map.get(entity) > dist) {
                    map.put(entity, dist);
                }
            }
        }


        System.out.println("********************* RESULT ******************");
        // Tìm score thấp nhất
        Double min = 1.0;
        for (String key : map.keySet()) {
            if (map.get(key) < min) {
                min = map.get(key);
            }
        }

        // Các thực thể có cùng score thấp nhất
        ArrayList<String> key_mins = new ArrayList<>();
        for (String key : map.keySet()) {
            if (String.valueOf(map.get(key)).equals(String.valueOf(min))) {
                key_mins.add(key);
            }
        }

        // Sắp xếp theo thứ hạng và xuât kết quả
        Map<String, Double> sortedMap = map.entrySet().stream().sorted(Entry.comparingByValue())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        sortedMap.forEach((k,v) -> {
            System.out.println(k + "[" + v + "]");
        });
                
        System.out.println("Câu trả lời: " + Arrays.toString(key_mins.toArray()));
    }
}



/*

    public ArrayList<String> findEntities(Pattern p, String doc) {
        ArrayList<String> entities = new ArrayList<String>();

        Matcher matcher = p.matcher(doc);
        boolean found = false;
        while (matcher.find()) {
            entities.add(matcher.group());
            // System.out.println(matcher.group());
            found = true;
        }
        if (!found) {
            System.out.println("No match found.");
        }
        return entities;
    };


        public static boolean isContain(String source, String subItem) {
        String pattern = "\\b" + subItem + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.find();
    }

*/