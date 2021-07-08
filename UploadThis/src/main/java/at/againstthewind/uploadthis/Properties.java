package at.againstthewind.uploadthis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class Properties
{
    private String uploadDir;

    public String getUploadDir()
    {
        return uploadDir;
    }

    public void setUploadDir(String directory)
    {
        this.uploadDir = directory;
    }
}