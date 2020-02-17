package operations;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class FFMPEGOperation {
	private  int blockSize = 5*6*1024*1024;
    private ObjectMapper mapper = new ObjectMapper();  
	private Map getMovieInfo(String filePath) {
        String cmd =  "ffprobe -v quiet -print_format json -show_format -i " + filePath;
        System.out.println(cmd);
        try {
            Runtime rt = Runtime.getRuntime();
            Process process = rt.exec(cmd);
            BufferedInputStream in = new BufferedInputStream(process.getInputStream());
            BufferedReader inBr = new BufferedReader(new InputStreamReader(in));
            StringBuffer sb = new StringBuffer();
            String lineStr;
            while ((lineStr = inBr.readLine()) != null)
            {
                sb.append(lineStr);
                //System.out.println(lineStr);
            }
            process.waitFor();
            inBr.close();
            in.close();
            //System.out.print(sb);
            return analyseInfo(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	private Map analyseInfo(String json) throws IOException, JsonMappingException, IOException {
        HashMap map = mapper.readValue(json, HashMap.class);
        Map format = (Map) map.get("format");
        return  format;
	}

	public ArrayList<String>  split(String filePath,String outputPath) throws IOException, InterruptedException{
		ArrayList<String> fileList = new ArrayList<String>();
		File file = new File(outputPath+"list.txt");
		FileOutputStream fos = new FileOutputStream(outputPath+"list.txt");
		Map movieInfo = getMovieInfo(filePath);
		double size = Double.valueOf( (String) movieInfo.get("size"));
		double start_time =Double.valueOf(  (String)movieInfo.get("start_time"));
		double duration = Double.valueOf( (String)movieInfo.get("duration"));
		int blockNum=(int) (size/blockSize)+1;
		double length = duration/blockNum;
		
        Runtime rt  =Runtime.getRuntime();
        String cmd;
        Process process;
        
        Map splitInfo;
        double splitStartTime = start_time;
        int num;
        for(num=0;splitStartTime+length<start_time+duration;num++){
        	fileList.add(num+".mkv");
        	String splitName = "\'"+num+".mp4"+'\'';
        	fos.write("file ".getBytes());
        	fos.write(splitName.getBytes());
        	fos.write("\n".getBytes());
            cmd = "ffmpeg -y -i "+filePath+" -ss "+splitStartTime+"  -t "+length+" -vcodec copy -acodec copy "+outputPath+num+".mkv";
            System.out.println(cmd);
            process = rt.exec(cmd);
            process.waitFor();
            process.destroy();
            splitInfo=getMovieInfo(outputPath+num+".mkv");
            //System.out.println(splitInfo);
            splitStartTime += Double.valueOf((String)splitInfo.get("duration"));
            if((splitStartTime+length)>(start_time+duration))
            	length = start_time+duration-splitStartTime;
        }
        fos.close();
		return fileList;
	}

	public String concat(String inputPath,String outputPath) throws IOException, InterruptedException{
        Runtime rt  =Runtime.getRuntime();
        String cmd;
        Process process;
		 cmd = "ffmpeg -y -f concat -i "+inputPath+"list.txt  -vcodec copy -acodec copy "+outputPath+"connect.mkv";
         System.out.println(cmd);
         process = rt.exec(cmd);
         process.waitFor();
         process.destroy();
		return outputPath+"out.mkv";
	}

	public int push(String filePath,String url) throws IOException, InterruptedException{
        Runtime rt  =Runtime.getRuntime();
        String cmd;
        Process process;
		 cmd = "ffmpeg -re -i "+filePath+" -vcodec libx264 -acodec copy -f flv "+url;
         System.out.println(cmd);
         process = rt.exec(cmd);
         process.waitFor();
         process.destroy();
		return 0;
	}
}
