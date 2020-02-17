package operations;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class MyRecordReader extends RecordReader<Text, Text> {

    //起始位置(相对整个分片而言)  
    private long start;  
    //结束位置(相对整个分片而言)  
    private long end;  
    //当前位置  
    private long pos;  
    //文件输入流  
    private FSDataInputStream fin = null;  
    //key、value  
    private Text key = null;  
    private Text value = null;  
    //文件路径
    private Path path;
    //文件数据
    private byte[] buffer;
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Text getCurrentKey() throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		return key;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
        //获取分片  
        FileSplit fileSplit = (FileSplit) split;  
        //获取起始位置  
        start = fileSplit.getStart();  
        //获取结束位置  
        end = start + fileSplit.getLength();  
        //创建配置  
        Configuration conf = context.getConfiguration();  
        //获取文件路径  
        path = fileSplit.getPath();  
        //根据路径获取文件系统  
        FileSystem fileSystem = path.getFileSystem(conf);  
        //打开文件输入流  
        fin = fileSystem.open(path);  
        //找到开始位置开始读取  
        fin.seek(start);  
        //将当期位置置为1  
        pos = start;  
        //申请空间
        buffer = new byte[(int)(end - start)];
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if (pos<end)
		{
			key = new Text();
            value = new Text();
            key.set(path.getName());
            value.set(path.getParent().toString());
            //System.out.println("---内容: " + value.toString());  
            pos += buffer.length;    
			return true;
		}
		else
			return false;
	}

}
