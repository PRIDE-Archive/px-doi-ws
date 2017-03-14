package uk.ac.ebi.pride.px.doi.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.px.doi.model.DoiMetaData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class carries out the registration of a DOI.
 */
public class DoiRegister {

  private static final Logger log = LoggerFactory.getLogger(DoiRegister.class);

  private static final String DATE_FORMAT = "yyyyMMddHHmm";
  private static final String host = "doi.crossref.org";
  private static final String test_host = "test.crossref.org";
  private static final int port = 80;

  private DoiMetaData doiMetaData;
  private boolean test = false;

  /**
   * Constructor, sets the DOI metadata.
   * @param doiMetaData the DOI metadata
   */
  public DoiRegister(DoiMetaData doiMetaData) {
    this.doiMetaData = doiMetaData;
  }

  /**
   * This method registers a DOI.
   * @param doi the DOI to register
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
      tmpFile = File.createTempFile("doi_" + pxAccession,".xml");
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
   * @param fileWriter the file writer
   * @param doi the DOI to register
   * @param mappedUrl the URL to map to
   * @throws IOException any problem during the DOI temporary file creation process.
   */
  private void writeMetadata(FileWriter fileWriter, String doi, String mappedUrl) throws IOException{
    String pxAccession = doi.split("/")[1];
    String header = "<doi_batch xmlns=\"http://www.crossref.org/schema/4.3.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"4.3.0\" xsi:schemaLocation=\"http://www.crossref.org/schema/4.3.0 http://www.crossref.org/schema/deposit/crossref4.3.0.xsd\">\n";
    fileWriter.write(header);
    String head = "<head>\n<doi_batch_id>" + pxAccession + "</doi_batch_id>\n"; //add the uniquely identifier for this dataset
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    String tmsp = sdf.format(new Date()); //get current timestamp
    head += "<timestamp>" + tmsp + "</timestamp>\n";
    head += "<depositor>\n";
    head += "<name>" + doiMetaData.getOrganization() + "</name>\n";
    head += "<email_address>" + doiMetaData.getEmail() +"</email_address>\n";
    head += "</depositor>\n";
    head += "<registrant>"+ doiMetaData.getDataOwner() +"</registrant>\n";
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

  /**
   * This method sends a POST message to register a DOI in production.
   * @throws Exception any problems during the DOI registration
   */
  public static boolean sendPOST(String XMLFile, String username, String password) throws Exception {
    return DoiRegister.sendPOST(XMLFile, username, password, false);
  }

  /**
   * This method sends a POST message to register a DOI in the test environment or not.
   *  Note: the test environment does NOT work!
   * @param XMLFile  the XML file containing the DOI request parameters.
   *                 See http://www.crossref.org/schema/deposit/crossref4.3.0.xsd for XML schema details.
   * @param username the user name to use for authentication
   * @param password the password linked to the user name
   * @param test     true for the registration query is send to a test domain, rather
   *                 than the live production domain of the CrossRef service.
   *                 Note: the test mode does not work, as the parameter does not seem to be accepted by the service of CrossRef.
   * @return true if crossref.org has successfully received the registration request.
   *                  Note: this is not a confirmation that the DOI has actually been registered!
   * @throws Exception any problems during the DOI registration
   * @see this#sendPOST(String, String, String)
   */
  public static boolean sendPOST(String XMLFile, String username, String password, boolean test) throws Exception {
    boolean registered = false;
    HTTPClient.NVPair[] uploadOpts = new HTTPClient.NVPair[1];
    HTTPClient.NVPair[] uploadFileOpts = new HTTPClient.NVPair[1];
    HTTPClient.NVPair[] ct_hdr = new HTTPClient.NVPair[1];
    /*     CrossRef service options
            DEPOSIT      = "doMDUpload"
            DEPOSIT_REFS = "doDOICitUpload"
            QUERY        = "doQueryUpload"
            DOIQUERY     = "doDOIQueryUpload"
     we specify the use case option as 'doMDUpload' for meta-data upload*/
    uploadOpts[0] = new HTTPClient.NVPair("operation", "doMDUpload");
    uploadFileOpts[0] = new HTTPClient.NVPair("fname", XMLFile);
    byte[] uploadBytes = HTTPClient.Codecs.mpFormDataEncode(uploadOpts, uploadFileOpts, ct_hdr);
    HTTPClient.CookieModule.setCookiePolicyHandler(null);
    HTTPClient.HTTPConnection httpConn;
    if (test) {
      httpConn = new HTTPClient.HTTPConnection(test_host, port);
    } else {
      httpConn = new HTTPClient.HTTPConnection(host, port);
    }
    HTTPClient.HTTPResponse httpResp = httpConn.Post("/servlet/deposit?login_id=" + username + "&login_passwd=" + password, uploadBytes, ct_hdr);
    if (httpResp.getStatusCode() == 200) {
      registered = true;
    }
    httpConn.stop();
    return registered;
  }
}