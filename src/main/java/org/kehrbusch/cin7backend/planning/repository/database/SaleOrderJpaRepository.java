package org.kehrbusch.cin7backend.planning.repository.database;

import org.kehrbusch.cin7backend.planning.entities.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleOrderJpaRepository extends JpaRepository<SaleOrder, String> {
    List<SaleOrder> findBySaleOrderIdIs(String saleOrderId);
    SaleOrder findFirstBySaleOrderId(String saleOrderId);
    void deleteAllBySaleOrderId(String saleOrderId);
}
