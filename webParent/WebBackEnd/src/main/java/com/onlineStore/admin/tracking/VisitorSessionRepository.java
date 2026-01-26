package com.onlineStore.admin.tracking;

import com.onlineStoreCom.entity.tracking.VisitorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitorSessionRepository extends JpaRepository<VisitorSession, Integer> {

    Optional<VisitorSession> findBySessionId(String sessionId);

    Long countByLastActiveTimeAfter(java.util.Date date);
}
