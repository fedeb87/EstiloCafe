package com.federicoberon.estilocafe.ui.home.history;

import com.federicoberon.estilocafe.model.OrderEntity;

public interface HistoryEventListener {
    void resendOrder(OrderEntity order);
}
