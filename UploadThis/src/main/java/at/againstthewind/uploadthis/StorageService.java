package at.againstthewind.uploadthis; 

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService
{
    private final Path storageDirectory;

    @Autowired
    public StorageService(Properties property) throws IOException
    {
        this.storageDirectory = Paths.get(property.getUploadDir()).toAbsolutePath().normalize();

        try
        {
            Files.createDirectories(this.storageDirectory);
        }
        catch (IOException e)
        {
            throw new IOException("An Error accured while creating the directory for the uplaoded file.", e);
        }
    }

    public String saveFile(MultipartFile file, String sha256) throws IOException, NoSuchAlgorithmException
    {
        
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String filePath = this.storageDirectory.resolve(fileName).normalize().toString();
        
        try
        {
            if (file.isEmpty())
            {
                throw new IOException("An Error accured while saving your file. Your file:" + fileName + " is Empty!");
            }
            else if (fileName.contains(".."))
            {
                throw new IOException("Sorry! File name contains invalid path sequence " + fileName + " ");
            }
            else if (isFileExistent(filePath))
            {
                throw new IOException("An Error accured while saving your file. Your file: " + fileName + " already exsists! ");
            }
            else if (isPathInvalid(filePath))
            {
                throw new IOException("An Error accured while saving your file. Your file: " + fileName + " ");
            }
            else if (!isFilePathCanonical(filePath))
            {
                throw new IOException("An Error accured while creating the directory for the uplaoded file. ");
            }
            
            Path saveFileLocation = this.storageDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), saveFileLocation, StandardCopyOption.REPLACE_EXISTING);
            
            if (sha256 != null)
            {                
                MessageDigest shaDigest = MessageDigest.getInstance("SHA-256");
                
                if (sha256.equals(getFileChecksum(shaDigest, file.getInputStream())))
                {
                    writeToFile(saveFileLocation.toString() + ".sha256", sha256);
                }
                else
                {
                    Files.delete(saveFileLocation);
                }
            }
            
            return fileName;
        }
        catch (IOException e)
        {
            throw new IOException("Could not store file " + fileName + ". Please try again!", e);
        }
    }

    public Resource loadFile(String fileName) throws IOException
    {
        try
        {
            Path filePath = this.storageDirectory.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists())
            {
                return resource;
            }
            else
            {
                throw new IOException("File: " + fileName + "not found!");
            }
        }
        catch (MalformedURLException e)
        {
            throw new IOException("File: " + fileName + "not found!", e);
        }
    }

    private boolean isFileExistent(String path)
    {
        boolean ret = false;
        File f = new File(path);
        
        if (f.exists() && !f.isDirectory())
        {
            ret = true;
        }
        
        return ret;
    }

    private boolean isPathInvalid(String path)
    {
        boolean ret = false;

        try
        {
            Paths.get(path);
        }
        catch (InvalidPathException e)
        {
            ret = true;
        }

        return ret;
    }

    private boolean isFilePathCanonical(String path)
    {
        boolean ret = false;
        File f = new File(path);

        try
        {
            f.getCanonicalPath();
            ret = true;
        }
        catch (IOException e)
        {
            // Redundant but for better readability
            ret = false;
        }

        return ret;
    }
    
    private static String getFileChecksum(MessageDigest digest, InputStream is) throws IOException
    {
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;
        
        while ((bytesCount = is.read(byteArray)) != -1)
        {
            digest.update(byteArray, 0, bytesCount);
        };
        
        is.close();
        
        byte[] bytes = digest.digest();
        
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < bytes.length; i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
    }
    
    private void writeToFile(String fileName, String fileData) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(fileData);
  
        writer.close();
    }
}
