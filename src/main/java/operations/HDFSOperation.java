package operations;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class HDFSOperation {
	public ArrayList<String> getVideoList() throws IOException{
		ArrayList<String> movielist = new ArrayList<String>();
		String path = "/user/hadoop/video";
		
        String uri = "hdfs:/Master:9000/";  
        Configuration config = new Configuration();  
        FileSystem hdfs = FileSystem.get(URI.create(uri), config);  
		Path fpath = new Path(path);
		FileStatus dirStatus = hdfs.getFileStatus(fpath);
		if(dirStatus.isDir())
		{
			System.out.println("这是一个目录");
			for(FileStatus fs:hdfs.listStatus(fpath))
			{
				String movieName = fs.getPath().getName();
				System.out.println(movieName);
				movielist.add(movieName);
			}
		}
		return movielist;
	}
	public String upLoad(String name ,String localFilePath,ArrayList<String> fileList) throws IOException{
        Configuration config = new Configuration();
        String hdfsPath = "/user/hadoop/video/";
        FileSystem hdfs = FileSystem.get(config);
        Path remotePath = new Path(hdfsPath+name);
        boolean result = hdfs.mkdirs(remotePath);
        System.out.print(result);
        System.out.println(remotePath.toString());
        if(result){
        	for(String fileName : fileList)
        	{
        		Path localPath = new Path(localFilePath+fileName);
        		hdfs.copyFromLocalFile(true, true,localPath,remotePath);
        	}
        }
        Path listPath = new Path(localFilePath+"list.txt");
        hdfs.copyFromLocalFile(true, true,listPath,remotePath);
        hdfs.close();
		return remotePath.toString();
	}
	public void upLoad(String hdfsPath,String filePath) throws IOException{
        Configuration config = new Configuration();        
        FileSystem hdfs = FileSystem.get(config);
        Path remotePath = new Path(hdfsPath);
		Path localPath = new Path(filePath);
		hdfs.copyFromLocalFile(true, true,localPath,remotePath);
		hdfs.close();
		}
    public boolean deleteFile (String remoteFilePath, boolean recursive) throws IOException {
        Configuration config = new Configuration();        
    	FileSystem hdfs = FileSystem.get(config);
        boolean result = hdfs.delete(new Path(remoteFilePath), recursive);
        hdfs.close();
        return result;
    }
    public  boolean exits(String path) throws IOException {
        Configuration config = new Configuration();        
        FileSystem hdfs = FileSystem.get(config);
        return hdfs.exists(new Path(path));
    }
    public  boolean createDirectory( String dirName) throws IOException {
        Configuration config = new Configuration();        
        FileSystem hdfs = FileSystem.get(config);
        Path dir = new Path(dirName);
        boolean result =hdfs.mkdirs(dir);
        hdfs.close();
        return result;
    }
}
