import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;


public class NERecognition {
	private POSTagger tagger = null;
	
	public NERecognition(String modelFile) {
		try {
			POSModel model = new POSModel(new FileInputStream(modelFile));
			tagger = new POSTaggerME(model);
		}
		catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static void trainMyModel(String trainFile, String modelFile) {
		
		POSModel model = null;
		
		InputStream dataIn = null;
		try {
			
			dataIn = new FileInputStream(trainFile);
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(new File(trainFile)), "UTF-8");
			ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
			POSTaggerFactory factory = new POSTaggerFactory();
			model = POSTaggerME.train("en", sampleStream, TrainingParameters.defaultParams(), factory);
			if (dataIn != null) 
				dataIn.close();
			OutputStream w = new BufferedOutputStream(new FileOutputStream(modelFile));
			model.serialize(w);
			w.close();
		}
		catch (IOException e) {
		// Failed to read or parse training data, training failed
			e.printStackTrace();
		}		
	}

	public String tag(String s) {
		String ret = "";
		
		String words[] = s.split("\\s+");
		String pos[] = tagger.tag(words);
		System.out.println(Arrays.toString(pos));
		System.out.println(pos.length);

		
		for (int i = 0; i < words.length; i++) 
			ret = ret + words[i] + "_" + pos[i] + " ";
		return ret;
	}


	/* Hàm trả về các thực thể theo loại câu hỏi trong 1 tài liệu, trong đó các thực thể có cùng nhãn 
	liên tiếp nhau sẽ nối thành 1 thực thể
	*/
	public ArrayList<String> getEntities(String doc, String type){  
		ArrayList<String> entities = new ArrayList<>();

		String words[] = doc.split("\s+|\n");
		String pos[] = tagger.tag(words);

		String term = "";
		for (int i = 0; i < pos.length; i++) {
			if(pos[i].equals(type)) {
				term += words[i] + " ";
				if(!pos[i+1].equals(type)){
					term = term.replaceFirst(".$", "");
					entities.add(term);
					term = "";
				}
				continue;
			}
		}
		return entities;
	}

	/* Tiền xử lý văn bản trước khi dự đoán nhãn*/
	public String formatDoc(String s){ 
		String after = s.replaceAll("\\(", "\s\\(\s");
		after = after.replaceAll("\\)", "\s\\)\s");
		after = after.replaceAll(",", "\s,\s");
		after = after.replaceAll("\\.", "\s\\.\s");
		after = after.replaceAll(":", "\s:\s");	
		after = after.replaceAll("”", "\s”\s");
		after = after.replaceAll("“", "\s“\s");
		after = after.replaceAll("…", "\s…\s");
		after = after.replaceAll("([0-9])\s,\s([0-9])", "$1,$2");
		after = after.replaceAll("([0-9])\s\\.\s([0-9])", "$1\\.$2");
		after = after.replaceAll(";", "\s;\s");
		after = after.replaceAll("\"", "\s\"\s");
		after = after.replaceAll("/", "\s/\s");			
		return after;
	}

	public static void main(String arg[]) {
		// NERecognition.trainMyModel("D:/output-tagged.txt", "NER.dat");
		NERecognition obj = new NERecognition("NER.dat");
		String noidung = "sân bóng có diện tích 7ha";
		noidung = " Năm 1962 Bộ Văn hóa - Thông tin  ( Việt Nam )  đã xếp hạng vịnh Hạ Long là di tích danh thắng cấp quốc gia đồng thời quy hoạch vùng bảo vệ .  Năm_NA 1994 vùng lõi của vịnh Hạ Long được UNESCO công nhận là Di sản Thiên nhiên Thế giới với giá trị thẩm mỹ  ( tiêu chuẩn vii )  ,  và được tái công nhận lần thứ 2 với giá trị ngoại hạng toàn cầu về địa chất-địa mạo  ( tiêu chuẩn viii )  vào năm 2000 .  Cùng với vịnh Nha Trang và vịnh Lăng Cô của Việt Nam ,  vịnh Hạ Long là một trong số 29 vịnh được Câu lạc bộ những vịnh đẹp nhất thế giới xếp hạng và chính thức công nhận vào tháng 7 năm 2003 . ";
		noidung = "Năm 1959, bức tượng Đức Bà Hòa Bình làm bằng đá granite lấy từ Rome được dựng bên ngoài Nhà thờ";
		noidung = obj.formatDoc(noidung);

		// Tiền xử lý

		String st = obj.tag(noidung);
		System.out.println(st);
	}
}