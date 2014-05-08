package uk.ac.ebi.pride.archive.doi.error.doi;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class DOIRegistrationException extends RuntimeException {

    private String doi;
    private String mappedUrl;

    public DOIRegistrationException(String doi, String mappedUrl) {
        this.doi = doi;
        this.mappedUrl = mappedUrl;
    }

    public DOIRegistrationException(String message, String doi, String mappedUrl) {
        super(message);
        this.doi = doi;
        this.mappedUrl = mappedUrl;
    }

    public DOIRegistrationException(String message, Throwable cause, String doi, String mappedUrl) {
        super(message, cause);
        this.doi = doi;
        this.mappedUrl = mappedUrl;
    }

    public DOIRegistrationException(Throwable cause, String doi, String mappedUrl) {
        super(cause);
        this.doi = doi;
        this.mappedUrl = mappedUrl;
    }

    public String getDoi() {
        return doi;
    }

    public String getMappedUrl() {
        return mappedUrl;
    }
}
