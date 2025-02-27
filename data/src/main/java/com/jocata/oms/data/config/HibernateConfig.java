package com.jocata.oms.data.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.hibernate.query.criteria.JpaRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

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
}
