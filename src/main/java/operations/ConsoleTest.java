package operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ConsoleTest {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		HDFSOperation ho = new HDFSOperation();
		FFMPEGOperation fo = new FFMPEGOperation();
		MapReduceOperation mo = new MapReduceOperation();
		String filePath = "/mnt/hgfs/Share/hadoop/the.big.bang.theory.1011.hdtv-lol.mkv";
		String outputPath = "/home/hadoop/test/split/";
		//ho.getMovieList();
		//切割文件
		//ArrayList<String> fileList = fo.split(filePath, outputPath);
		//获取列表
		ArrayList<String> videoList = ho.getVideoList();
		for (String videoInfo:videoList){
			System.out.println(videoInfo);
		}
		//上传到集群
		//ho.upLoad("lalala" , outputPath, fileList);
		//转码并且推流
		mo.transCode("video/lalala", "output");
	}

}
