package com.cool.nfckiosk.data.detailedTable;

import com.cool.nfckiosk.data.order.Order;
import com.cool.nfckiosk.data.store.Store;

import java.io.Serializable;
import java.util.Objects;

public class DetailedTable extends Store.Table {

    private final Order order;

    public DetailedTable(Store.Table table, Order order) {
        super(table.getNumber(), table.isActive());
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DetailedTable that = (DetailedTable) o;
        return Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), order);
    }
}
