package fileUpload;

import java.io.IOException;

import operations.MapReduceOperation;

public class Play {
	private String name ;
	private MapReduceOperation mo = new MapReduceOperation();
	public String playMovie() throws ClassNotFoundException, IOException, InterruptedException{
		String inputPath = "video/"+name;
		mo.transCode(inputPath, "output");
		return "success";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
