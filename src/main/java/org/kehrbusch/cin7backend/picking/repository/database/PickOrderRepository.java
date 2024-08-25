package org.kehrbusch.cin7backend.picking.repository.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.kehrbusch.cin7backend.picking.entities.PickOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PickOrderRepository {
    private final PickOrderJpaRepository pickOrderJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PickOrderRepository(PickOrderJpaRepository pickOrderJpaRepository){
        this.pickOrderJpaRepository = pickOrderJpaRepository;
    }

    public void saveOrUpdate(PickOrder pickOrder){
        this.pickOrderJpaRepository.save(pickOrder);
    }

    public String generateToteId(){
        String jpql = "SELECT MAX(p.toteNr) FROM PickOrder p";
        TypedQuery<String> query = entityManager.createQuery(jpql, String.class);

        String id = query.getSingleResult();
        int number = id == null ? 0 : Integer.parseInt(id);
        number += 1;
        return String.format("%0" + (id == null ? 1 : id.length()) + "d", number);
    }

    public String generateToteId(String lastRequestedToteId){
        String nextId = generateToteId();
        if (nextId.equals(lastRequestedToteId)){
            int number = Integer.parseInt(lastRequestedToteId);
            number += 1;
            return String.format("%0" + lastRequestedToteId.length() + "d", number);
        } else {
            return nextId;
        }
    }

    @Transactional
    public PickOrder removePickOrders(String saleOrderId){
        PickOrder pickOrder = this.pickOrderJpaRepository.findFirstBySaleOrderId(saleOrderId);
        if (pickOrder != null){
            this.pickOrderJpaRepository.deleteAllBySaleOrderId(saleOrderId);
        }
        return pickOrder;
    }
}
