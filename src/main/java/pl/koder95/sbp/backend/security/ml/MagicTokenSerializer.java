package pl.koder95.sbp.backend.security.ml;

public interface MagicTokenSerializer {
    String serialize(MagicToken magicToken);
}
