package com.jocata.oms.data.um.dao;

import com.jocata.oms.datamodel.um.entity.Address;

public interface AddressDao {

    Address createAddress(Address address);

    Address getAddressById(Integer id);

    Address updateAddress(Address address);

}
