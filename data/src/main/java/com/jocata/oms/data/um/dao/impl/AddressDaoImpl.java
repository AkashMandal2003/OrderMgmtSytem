package com.jocata.oms.data.um.dao.impl;

import com.jocata.oms.data.config.HibernateConfig;
import com.jocata.oms.data.um.dao.AddressDao;
import com.jocata.oms.datamodel.um.entity.Address;
import org.springframework.stereotype.Repository;

@Repository
public class AddressDaoImpl implements AddressDao {

    private final HibernateConfig hibernateConfig;

    public AddressDaoImpl(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    @Override
    public Address createAddress(Address address) {
        return hibernateConfig.saveEntity(address);
    }
}
