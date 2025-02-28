package com.jocata.oms.data.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaPredicate;
import org.hibernate.query.criteria.JpaRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class HibernateConfig {

    private final SessionFactory sessionFactory;

    @Autowired
    public HibernateConfig(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Session getSession() {
        return this.getSessionFactory().openSession();
    }

    public void closeSession(Session session) {
        if(session != null) {
            session.close();
        }
    }

    public <T> T saveEntity(T entity) {
        Session session = null;
        Transaction tx = null;
        try {
            session=this.getSession();
            tx=session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        }catch(Exception e) {
            if(tx!=null) tx.rollback();
        }finally {
            closeSession(session);
        }
        return null;
    }

    public <T> T findEntityById(Class<T> entityClass, Serializable primaryId) {

        Session session = null;
        try {
            session = this.getSession();
            return session.get(entityClass, primaryId);
        } catch (Exception e) {
        } finally {
            this.closeSession(session);
        }
        return null;
    }

    public <T> T updateEntity(T entity) {
        Session session = null;
        Transaction tx = null;
        try {
            session=getSession();
            tx=session.beginTransaction();
            session.merge(entity);
            tx.commit();
            return entity;
        }catch(Exception e) {
            if(tx!=null) tx.rollback();
        }finally {
            closeSession(session);
        }
        return null;
    }

    public <T> T deleteEntity(T entity, Serializable primaryId) {
        Session session = null;
        Transaction tx =null;
        try {
            session = this.getSession();
            tx=session.beginTransaction();
            Object dataObject = session.get(entity.getClass(), primaryId);
            session.remove(dataObject);
            tx.commit();
            return entity;

        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            this.closeSession(session);
        }
        return null;
    }

    public <T> T softDeleteEntity(T entity) {
        Session session = null;
        Transaction tx =null;
        try {
            session = this.getSession();
            tx=session.beginTransaction();
            session.merge(entity);
            tx.commit();
            return entity;

        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            this.closeSession(session);
        }
        return null;
    }

    public <T> T findEntityByCriteria(Class<T> entityClass, String primaryPropertyName, Serializable primaryId) {

        Session session = null;
        try {
            session = this.getSession();
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            JpaRoot<T> root = criteriaQuery.from(entityClass);

            String[] props = checkIfSplit(primaryPropertyName);
            if(props.length == 2) {
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(props[0]).get(props[1]), primaryId));
            }else {
                criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(primaryPropertyName), primaryId));
            }

            return session.createQuery(criteriaQuery).getSingleResult();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            this.closeSession(session);
        }
        return null;
    }

    public <T> T findEntityByMultipleCriteria(Class<T> entityClass, Map<String, Object> criteriaMap) {
        Session session = null;
        try {
            session = this.getSession();
            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            JpaRoot<T> root = criteriaQuery.from(entityClass);

            List<JpaPredicate> predicates = new ArrayList<>();
            for (Map.Entry<String, Object> entry : criteriaMap.entrySet()) {
                String propertyName = entry.getKey();
                Object propertyValue = entry.getValue();

                String[] props = checkIfSplit(propertyName);
                if (props.length == 2) {
                    predicates.add(criteriaBuilder.equal(root.get(props[0]).get(props[1]), propertyValue));
                } else {
                    predicates.add(criteriaBuilder.equal(root.get(propertyName), propertyValue));
                }
            }

            criteriaQuery.select(root).where(criteriaBuilder.and(predicates.toArray(new JpaPredicate[0])));
            return session.createQuery(criteriaQuery).getSingleResult();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            this.closeSession(session);
        }
        return null;
    }

    public <T> List<T> loadEntitiesByCriteria(Class<T> entityClass) {
        Session session = null;
        try {
            session = this.getSession();

            HibernateCriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            JpaCriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
            JpaRoot<T> root = criteriaQuery.from(entityClass);

            criteriaQuery.select(root);
            Query<T> query = session.createQuery(criteriaQuery);

            return query.getResultList();
        } catch (Exception e) {
            session.getTransaction().rollback();
        } finally {
            this.closeSession(session);
        }
        return Collections.emptyList();
    }

    private String[] checkIfSplit(String primaryPropertyName) {
        return primaryPropertyName.split("\\.");
    }


}
