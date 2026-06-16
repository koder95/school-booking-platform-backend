package pl.koder95.sbp.backend.security.ml;

public interface MagicTokenDeserializer {
    MagicToken deserialize(String token);
}
