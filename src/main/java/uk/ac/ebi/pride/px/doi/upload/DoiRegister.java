package uk.ac.ebi.pride.px.doi.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.pride.px.doi.model.DoiMetaData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class carries out the registration of a DOI.
 */
public class DoiRegister {

    private static final Logger log = LoggerFactory.getLogger(DoiRegister.class);

    private static final String DATE_FORMAT = "yyyyMMddHHmm";
    private static final String host = "https://doi.crossref.org";
    private static final String test_host = "https://test.crossref.org";
    private DoiMetaData doiMetaData;
    private boolean test = false;

    /**
     * Constructor, sets the DOI metadata.
     *
     * @param doiMetaData the DOI metadata
     */
    public DoiRegister(DoiMetaData doiMetaData) {
        this.doiMetaData = doiMetaData;
    }

    /**
     * This method registers a DOI.
     *
     * @param doi       the DOI to register
     * @param mappedUrl the URL to map to.
     * @return true if the registation was successful, false othwerise.
     * @throws Exception any problems during the DOI registration process.
     */
    public boolean registerDOI(String doi, String mappedUrl) throws Exception {
        boolean registered;
        File tmpFile;
        FileWriter fw = null;
        String pxAccession = doi.split("/")[1];
        try {
            tmpFile = File.createTempFile("doi_" + pxAccession, ".xml");
            tmpFile.deleteOnExit();
            fw = new FileWriter(tmpFile);
            writeMetadata(fw, doi, mappedUrl);
            fw.flush();
        } finally {
            if (fw != null) {
                fw.close();
            }
        }
        registered = DoiRegister.sendPOST(tmpFile.getAbsolutePath(), doiMetaData.getUser(), doiMetaData.getPassword(), test);
        boolean deletedTemp = tmpFile.delete();
        if (deletedTemp) {
            log.info("Successfully deleted temp file.");
        } else {
            log.error("Problem deleting temp file.");
        }
        return registered;
    }

    /**
     * This method will write the required metadata in the XML for the DOI deposition
     *
     * @param fileWriter the file writer
     * @param doi        the DOI to register
     * @param mappedUrl  the URL to map to
     * @throws IOException any problem during the DOI temporary file creation process.
     */
    private void writeMetadata(FileWriter fileWriter, String doi, String mappedUrl) throws IOException {
        String pxAccession = doi.split("/")[1];
        String header = "<doi_batch xmlns=\"http://www.crossref.org/schema/4.3.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"4.3.0\" xsi:schemaLocation=\"http://www.crossref.org/schema/4.3.0 http://www.crossref.org/schema/deposit/crossref4.3.0.xsd\">\n";
        fileWriter.write(header);
        String head = "<head>\n<doi_batch_id>" + pxAccession + "</doi_batch_id>\n"; //add the uniquely identifier for this dataset
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String tmsp = sdf.format(new Date()); //get current timestamp
        head += "<timestamp>" + tmsp + "</timestamp>\n";
        head += "<depositor>\n";
        head += "<name>" + doiMetaData.getOrganization() + "</name>\n";
        head += "<email_address>" + doiMetaData.getEmail() + "</email_address>\n";
        head += "</depositor>\n";
        head += "<registrant>" + doiMetaData.getDataOwner() + "</registrant>\n";
        head += "</head>\n";
        fileWriter.write(head);
        String body = "<body>\n<database>\n<database_metadata>\n<titles>\n";
        body += "<title>" + doiMetaData.getTitle() + "</title>\n</titles>\n";
        body += "<doi_data>\n<doi>";
        body += doi;
        body += "</doi>\n<resource>";
        body += mappedUrl;
        body += "</resource>\n</doi_data>\n</database_metadata>\n</database>\n</body>\n</doi_batch>\n";
        fileWriter.write(body);
    }

    private static boolean sendPOST(String xmlFilePath, String username, String password, boolean test) throws Exception {
        boolean registered = false;

        // Prepare multipart body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("operation", "doMDUpload");
        // Add file as a resource
        FileSystemResource fileResource = new FileSystemResource(new File(xmlFilePath));
        body.add("fname", fileResource);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Wrap request
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Build URL
        String baseUrl = test ? test_host : host;
        String url = baseUrl + "/servlet/deposit?login_id=" + username + "&login_passwd=" + password;

        // Send request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            registered = true;
        }

        return registered;
    }

    /**
     * Confirms if this is using a test mode.
     *
     * @return true for test mode, false oterwise (default).
     */
    public boolean isTest() {
        return test;
    }

    /**
     * Sets the test mode.
     *
     * @param test true for test mode, false otherwise (default).
     */
    public void setTest(boolean test) {
        this.test = test;
    }

}