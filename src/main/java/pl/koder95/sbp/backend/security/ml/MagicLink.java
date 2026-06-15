package pl.koder95.sbp.backend.security.ml;

public record MagicLink(
        String baseUrl,
        String frontendEndpoint,
        String paramName,
        String paramValue
) {
    public MagicLink {
        if (baseUrl == null || frontendEndpoint == null
                || paramName == null || paramValue == null) {
            throw new IllegalArgumentException("MagicLink parameters cannot be null");
        }
    }

    @Override
    public String toString() {
        return baseUrl + '/' + frontendEndpoint + '?' + paramName + '=' + paramValue;
    }
}
