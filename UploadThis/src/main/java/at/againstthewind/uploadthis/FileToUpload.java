package at.againstthewind.uploadthis;

public class FileToUpload
{
    private String name;
    private String type;
    private long size;
    private String uRL;
    private String sha256;
    private long ID;

    public FileToUpload(String name, String type, long size, String uRL, String sha256, long id)
    {
        this.name = name;
        this.type = type;
        this.size = size;
        this.uRL = uRL;
        this.sha256 = sha256;
        this.ID = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize(long size)
    {
        this.size = size;
    }

    public String getuRL()
    {
        return uRL;
    }

    public void setuRL(String uRL)
    {
        this.uRL = uRL;
    }

    public String getSha256()
    {
        return sha256;
    }

    public void setSha256(String sha256)
    {
        this.sha256 = sha256;
    }
    
    public long getID()
    {
        return ID;
    }

    public void setID(long iD)
    {
        ID = iD;
    }
}
