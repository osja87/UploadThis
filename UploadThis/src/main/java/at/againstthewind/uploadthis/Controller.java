package at.againstthewind.uploadthis;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.HttpHeaders;

@RestController
public class Controller
{
    @Autowired
    private StorageService storageService;
    
    private Integer Id = 0; 

    @PostMapping("/uploadFile")
    public FileToUpload fileToUpload(@RequestParam("file") MultipartFile file,
                                     @RequestParam(required = false, name = "sha-256") String sha256) throws IOException, NoSuchAlgorithmException
    {
        String fileName = storageService.saveFile(file, sha256);

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();
        
        ++Id;
        return new FileToUpload(fileName, file.getContentType(), file.getSize(), fileDownloadUri, sha256, Id);
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downlaodFile(@PathVariable String fileName, HttpServletRequest request)
            throws IOException
    {
        Resource resource = storageService.loadFile(fileName);

        String contentType = null;
        try
        {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }
        catch (IOException e)
        {
            System.out.println("Could not determine file type.");
        }

        if (contentType == null)
        {
            contentType = "application/octet-stream";
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
