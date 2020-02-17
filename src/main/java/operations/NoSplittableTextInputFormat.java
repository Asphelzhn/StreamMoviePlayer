package operations;

import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;


public  class NoSplittableTextInputFormat extends FileInputFormat<Text,Text>{
	@Override
	protected boolean isSplitable(JobContext context,Path file){
		return false;
	}

	@Override
	public RecordReader<Text,Text> createRecordReader(InputSplit split,
			TaskAttemptContext arg1) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		return new  MyRecordReader();
	}
}