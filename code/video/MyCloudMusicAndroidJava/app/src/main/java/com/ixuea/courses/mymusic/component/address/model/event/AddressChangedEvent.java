package com.ixuea.courses.mymusic.component.address.model.event;

import com.ixuea.courses.mymusic.component.address.model.Address;

/**
 * 更改了收货地址事件
 */
public class AddressChangedEvent {
    private Address data;

    public AddressChangedEvent(Address data) {
        this.data = data;
    }

    public Address getData() {
        return data;
    }

    public void setData(Address data) {
        this.data = data;
    }
}
