package org.kehrbusch.cin7backend.picking.repository.database;

import org.kehrbusch.cin7backend.picking.entities.PickOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
@ActiveProfiles("mock")
public class PickOrderRepositoryTest {
    @Autowired
    private PickOrderRepository pickOrderRepository;

    @Test
    @Transactional
    public void testToteIdGeneration(){
        PickOrder pickOrder = new PickOrder();
        pickOrder.setToteNr("00001");

        pickOrderRepository.saveOrUpdate(pickOrder);

        String toteId = pickOrderRepository.generateToteId();

        assertThat(toteId, is("00002"));
    }

    @Test
    @Transactional
    public void testToteIdExists(){
        PickOrder pickOrder = new PickOrder();
        pickOrder.setToteNr("00001");

        pickOrderRepository.saveOrUpdate(pickOrder);

        String toteId = pickOrderRepository.generateToteId("00002");

        assertThat(toteId, is("00003"));
    }

    @Test
    @Transactional
    public void testPickOrderRemoval(){
        PickOrder pickOrder = new PickOrder();
        pickOrder.setSaleOrderId("1");

        pickOrderRepository.saveOrUpdate(pickOrder);

        PickOrder deletedPickOrder1 = pickOrderRepository.removePickOrders(pickOrder.getSaleOrderId());
        assertThat(deletedPickOrder1.getSaleOrderId(), is("1"));

        PickOrder deletedPickOrder2 = pickOrderRepository.removePickOrders(pickOrder.getSaleOrderId());
        assertThat(deletedPickOrder2, nullValue());
    }
}
