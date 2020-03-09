package com.example.administrator.xiaoshuoyuedushenqi.entity.bean;

/**
 * Created by Administrator on 2017/9/9.
 */
public class FileInfo
{
    private String filename;
    private String filepath;
    private String filetype;

    public String getFilestate() {
        return filestate;
    }

    public void setFilestate(String filestate) {
        this.filestate = filestate;
    }

    private String filestate;

    public FileInfo(String filename, String filepath, String filetype,String filestate) {
        this.filename = filename;
        this.filepath = filepath;
        this.filetype = filetype;
        this.filestate = filestate;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

}
