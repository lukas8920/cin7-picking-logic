package org.kehrbusch.cin7backend.picking.repository.database;

import org.kehrbusch.cin7backend.picking.entities.PickOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickOrderJpaRepository extends JpaRepository<PickOrder, Long> {
    public void deleteAllBySaleOrderId(String saleOrderId);
    public PickOrder findFirstBySaleOrderId(String saleOrderId);
}
