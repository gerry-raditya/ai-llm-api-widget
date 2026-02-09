package com.rumahhobi.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rumahhobi.entity.ConversationSessionEntity;
import com.rumahhobi.entity.ConversationStateEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ConversationService {
    @Inject EntityManager em;

    public Long startSession(String clientId, String userId, String intent) {
        ConversationSessionEntity s = new ConversationSessionEntity();
        s.clientId = clientId;
        s.userId = userId;
        s.intent = intent;
        s.status = "ACTIVE";
        s.persist();
        return s.id;
    }

    /**
     * Ambil session aktif atau buat baru
     */
    @Transactional
    public Long getOrCreateSession(String clientId, String userId, String intent) {

        List<ConversationSessionEntity> list = em.createQuery("""
            FROM ConversationSessionEntity
            WHERE clientId = :clientId
              AND userId = :userId
              AND intent = :intent
              AND status = 'ACTIVE'
        """, ConversationSessionEntity.class)
        .setParameter("clientId", clientId)
        .setParameter("userId", userId)
        .setParameter("intent", intent)
        .getResultList();

        if (!list.isEmpty()) {
            return list.get(0).id;
        }

        ConversationSessionEntity s = new ConversationSessionEntity();
        s.clientId = clientId;
        s.userId = userId;
        s.intent = intent;
        s.status = "ACTIVE";
        s.persist();

        return s.id;
    }

    /**
     * Simpan satu field ke memory
     */
    @Transactional
    public void saveField(Long sessionId, String fieldName, String value) {

        List<ConversationStateEntity> existing = em.createQuery("""
                    FROM ConversationStateEntity
                    WHERE sessionId = :sessionId
                      AND fieldName = :fieldName
                """, ConversationStateEntity.class)
                .setParameter("sessionId", sessionId)
                .setParameter("fieldName", fieldName)
                .getResultList();

        ConversationStateEntity st;
        if (existing.isEmpty()) {
            st = new ConversationStateEntity();
            st.sessionId = sessionId;
            st.fieldName = fieldName;
        } else {
            st = existing.get(0);
        }

        st.fieldValue = value;
        st.persist();
    }

    public Map<String, String> loadState(Long sessionId) {
        List<ConversationStateEntity> rows = ConversationStateEntity.find("sessionId", sessionId).list();

        Map<String, String> map = new HashMap<>();
        for (ConversationStateEntity r : rows) {
            map.put(r.fieldName, r.fieldValue);
        }
        return map;
    }

    /**
     * Tutup session setelah selesai
     */
    @Transactional
    public void closeSession(Long sessionId) {

        em.createQuery("""
            UPDATE ConversationSessionEntity
            SET status = 'DONE'
            WHERE id = :id
        """)
        .setParameter("id", sessionId)
        .executeUpdate();
    }
}
