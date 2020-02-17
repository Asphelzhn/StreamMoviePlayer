package fileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionSupport;

import operations.FFMPEGOperation;
import operations.HDFSOperation;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


@SuppressWarnings("serial")
public class movieUpload extends ActionSupport implements ServletRequestAware, ServletResponseAware {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	public static final String UPLOAD_DIR = "/home/hadoop/test/file";
	
	private String resumableChunkSize;
	private String resumableTotalSize;
    private String resumableIdentifier;
    private String resumableFilename;
    private String resumableRelativePath;
    private String resumableChunkNumber; 
    
 
	public String execute() throws Exception {
		String method = request.getMethod();
		int chunkNumber = HttpUtils.toInt(getResumableChunkNumber(), -1);
		ResumableInfo info = getResumableInfo();
		
		if (chunkNumber == 1){
            DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date2 = new Date();
            System.out.println("Start: "+dateFormat2.format(date2));
        }
		
		if (method.equals("GET")) {
            if(info.uploadedChunks.contains(new ResumableInfo.ResumableChunkNumber(chunkNumber))) {
            	response.getWriter().write("Uploaded.");
            	return SUCCESS;
            } else {
            	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return ERROR;
            }
        }
		
		System.out.println("Method: "+method);
	
		if (!info.uploadedChunks.contains(new ResumableInfo.ResumableChunkNumber(chunkNumber))) {
            RandomAccessFile raf = new RandomAccessFile(info.resumableFilePath, "rw");
            raf.seek((chunkNumber - 1) * (long)info.resumableChunkSize);
            
            InputStream is = request.getInputStream();
            //Save to file

            long readed = 0;
            long content_length = request.getContentLength();
            byte[] bytes = new byte[1024 * 100];
            while(readed < content_length) {
                int r = is.read(bytes);
                if (r < 0)  {
                    break;
                }
                raf.write(bytes, 0, r);
                readed += r;
            }
            raf.close();
            is.close();
        }
        
        info.uploadedChunks.add(new ResumableInfo.ResumableChunkNumber(chunkNumber));
        if (info.checkIfUploadFinished()) {
            ResumableInfoStorage.getInstance().remove(info);              
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println("Finished: "+dateFormat.format(date));
            
            String name   = getResumableFilename();
            String filePath = UPLOAD_DIR+"/"+name;
            String outputPath = "/home/hadoop/test/split/";
            HDFSOperation ho = new HDFSOperation();
            FFMPEGOperation fo = new FFMPEGOperation();
    		ArrayList<String> fileList = fo.split(filePath, outputPath);
            ho.upLoad(name, outputPath, fileList);
            
            response.getWriter().write("All finished.");
        } else {
            System.out.println("Not finished yet .."+chunkNumber);
            response.getWriter().write("Upload");
        }
        return SUCCESS;
		
	}

	private ResumableInfo getResumableInfo() throws ServletException {
        String base_dir = UPLOAD_DIR;

        int resumableChunkSize          = HttpUtils.toInt(getResumableChunkSize(), -1);
        long resumableTotalSize         = HttpUtils.toLong(getResumableTotalSize(), -1);
        String resumableIdentifier      = getResumableIdentifier();
        String resumableFilename        = getResumableFilename();
        String resumableRelativePath    = getResumableRelativePath();
        
        //Here we add a ".temp" to every upload file to indicate NON-FINISHED
        new File(base_dir).mkdir();
        String resumableFilePath        = new File(base_dir, resumableFilename).getAbsolutePath() + ".temp";

        ResumableInfoStorage storage = ResumableInfoStorage.getInstance();

        ResumableInfo info = storage.get(resumableChunkSize, resumableTotalSize,
                resumableIdentifier, resumableFilename, resumableRelativePath, resumableFilePath);
        if (!info.vaild())         {
            storage.remove(info);
            throw new ServletException("Invalid request params.");
        }
        return info;
    }
	
	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}    
	 
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getResumableChunkSize() {
		return resumableChunkSize;
	}


	public void setResumableChunkSize(String resumableChunkSize) {
		this.resumableChunkSize = resumableChunkSize;
	}


	public String getResumableTotalSize() {
		return resumableTotalSize;
	}


	public void setResumableTotalSize(String resumableTotalSize) {
		this.resumableTotalSize = resumableTotalSize;
	}


	public String getResumableIdentifier() {
		return resumableIdentifier;
	}


	public void setResumableIdentifier(String resumableIdentifier) {
		this.resumableIdentifier = resumableIdentifier;
	}


	public String getResumableFilename() {
		return resumableFilename;
	}


	public void setResumableFilename(String resumableFilename) {
		this.resumableFilename = resumableFilename;
	}


	public String getResumableRelativePath() {
		return resumableRelativePath;
	}


	public void setResumableRelativePath(String resumableRelativePath) {
		this.resumableRelativePath = resumableRelativePath;
	}


	public String getResumableChunkNumber() {
		return resumableChunkNumber;
	}


	public void setResumableChunkNumber(String resumableChunkNumber) {
		this.resumableChunkNumber = resumableChunkNumber;
	}
	
}
