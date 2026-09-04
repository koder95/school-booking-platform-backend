package pl.koder95.sbp.backend.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.koder95.sbp.backend.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, UUID> {
    Page<Resource> findAllByType(Resource.Type type, Pageable pageable);

    Optional<Resource> findByUrl(String url);
}
