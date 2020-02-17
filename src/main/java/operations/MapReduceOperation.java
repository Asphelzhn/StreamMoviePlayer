package operations;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MapReduceOperation {
	public static class TransCodeMapper extends Mapper<Text, Text, Text,Text>{
	
	 private final static Text path = new Text();
	 private Text word = new Text();
	
	 public void map(Text key, Text value, Context context
	                 ) throws IOException, InterruptedException {
		 //临时输出文件夹
		 String local = "/home/hadoop/test/";
		 //临时转码输出文件夹
		 String localoutput = "/home/hadoop/test/output/";
		 //String hdfspath = "hdfs://localhost:9000/user/hadoop/input/video/";
         HDFSOperation ho = new HDFSOperation();
         if(!ho.exits(value+"-afterTransCode"))
        	 ho.createDirectory(value+"-afterTransCode");
		 Configuration conf = new Configuration();
		 FileSystem fs = FileSystem.get(URI.create(value+"/"+key),conf);
		 FSDataInputStream fsdi = fs.open(new Path(value+"/"+key));
		 OutputStream output = new FileOutputStream(local+key.toString());
		 IOUtils.copyBytes(fsdi, output, 4096,true);
		 String[] fileName = key.toString().split("\\.");
	     if(fileName[1].equals("mkv")){
	         String cmd = "ffmpeg -y -i "+local+key.toString()+" -vcodec copy -acodec copy "+localoutput+fileName[0]+".mp4";
	         Runtime rt  =Runtime.getRuntime();
	         Process process = rt.exec(cmd);
	         process.waitFor();
	         process.destroy();
	         File file = new File(local+key.toString());  
	         if (file.isFile() && file.exists()) {  
	             file.delete(); 
	         }  
	         //写入hdfs同名加afterTransCode文件夹
	         ho.upLoad(value+"-afterTransCode/"+fileName[0]+".mp4", localoutput+fileName[0]+".mp4");
	     }
	     path.set(value+"-afterTransCode/");
	     word.set(fileName[0]);
	     context.write(path, word);
	 }
	}
	
    public static class PushReducer extends Reducer<Text,Text,Text,Text> {
		 private Text result = new Text();
		 public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			 //String hdfsout = "hdfs://Master:9000/user/hadoop/Connectout/";//hdfs合并输出
		     String out = "/home/hadoop/test/out/";//本地合并输出
		     String localoutput = "/home/hadoop/test/output/";//本地转码输出
		     //String hdfspath = "hdfs://Master:9000/user/hadoop/Codeout/";//hdfs转码输出
		     String hdfspath = key.toString();
		     //String Filename = key.toString().substring(key.toString().lastIndexOf("/")+1, key.toString().lastIndexOf("."));
		     Configuration conf = new Configuration();
		     ArrayList<Text> a=new ArrayList<Text>();
		     int CutCount = 0;
		     for (Text s : values) {
		   	  	a.add(s);
		   	  	CutCount++;
		     }
		     
		     float steping=0;
		     	
			    JobClient client = new JobClient(conf);
			 //try{
			    RunningJob runningjob=client.getJob((org.apache.hadoop.mapred.JobID) context.getJobID());
			    steping=runningjob.getJobStatus().mapProgress();
			 //}catch(Exception e){
		    //	 System.out.println("获取不到job");
		    //}
			     
		     if(steping==(float)1.0){
		    	System.out.println("当前状态："+steping);
		     
				File listfile = new File(localoutput+"list.txt");
				FileOutputStream fos = new FileOutputStream(localoutput+"list.txt");
				for (int n=0;n<CutCount-1;n++) {
					System.out.println(n+"||"+CutCount);
					FileSystem fs = FileSystem.get(URI.create((hdfspath+n+".mp4").toString()),conf);
					FSDataInputStream fsdi = fs.open(new Path((hdfspath+n+".mp4").toString()));
					OutputStream output = new FileOutputStream(localoutput+n+".mp4");
					IOUtils.copyBytes(fsdi, output, 4096,true);
					String splitName = "\'"+n+".mp4"+'\'';
					fos.write("file ".getBytes());
					fos.write(splitName.getBytes());
					fos.write("\n".getBytes());
			    }
				 //String cmd = "ffmpeg -f concat -i "+localoutput+"list.txt  -vcodec copy -acodec copy "+localoutput+"_connect.mkv";
				 //System.out.println("cmd");
				 FFMPEGOperation fo = new FFMPEGOperation();
				 
				 //FileSystem fs = FileSystem.get(URI.create("hdfs://Master:9000/user/hadoop/video/lalala/list.txt"),conf);
				 //FSDataInputStream fsdi = fs.open(new Path("hdfs://Master:9000/user/hadoop/video/lalala/list.txt"));
				 //OutputStream output = new FileOutputStream(localoutput+"list.txt");
				 //IOUtils.copyBytes(fsdi, output, 4096,true);
				 fo.concat(localoutput, out);
				 
				 for(int n=0;n<CutCount-1;n++){
				     File file = new File(localoutput+n+".mp4");  
				     if (file.isFile() && file.exists()) {  
				         file.delete(); 
				     }  
				 }
				 
				 String url = "rtmp://localhost:1935/rtmplive/"+context.getJobName();
				 //推流到rtmp://localhost:1935/rtmplive/video/+视频名字
				 //fo.push(out+"connect.mkv",url);
				 
				 HDFSOperation ho = new HDFSOperation();
				 //ho.upLoad(hdfspath, localoutput+"connect.mkv");
		     }
		     result.set("1");
		     context.write(key, result);
		 }
}
	
	public int transCode(String inputPath,String outputPath) throws IOException, ClassNotFoundException, InterruptedException{
        HDFSOperation ho = new HDFSOperation();
        ho.deleteFile(outputPath, true);
		Configuration conf = new Configuration();
        Job job = new Job(conf, inputPath);
        //Job job = Job.getInstance(conf, inputPath);
        job.setJarByClass(MapReduceOperation.class);
        job.setMapperClass(TransCodeMapper.class);
        //job.setCombinerClass(PushReducer.class);
        job.setReducerClass(PushReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(NoSplittableTextInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        job.waitForCompletion(true);
		return 0;
	}
}
