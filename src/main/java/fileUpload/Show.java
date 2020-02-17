package fileUpload;

import java.io.IOException;
import java.util.ArrayList;

import operations.*;

public class Show {
	private ArrayList<String> videoList = new ArrayList<String>();
	private HDFSOperation ho = new HDFSOperation();
	public String showAllVideos() throws IOException{
		ArrayList<String> tempList = ho.getVideoList();
		for (String i : tempList){
			String[] temp = i.split("-");
			if(!temp[temp.length-1].equals("afterTransCode"))
				videoList.add(i);
		}
		return "success";
	}
	public ArrayList<String> getVideoList() {
		return videoList;
	}
	public void setVideoList(ArrayList<String> videoList) {
		this.videoList = videoList;
	}
}
