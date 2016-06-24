package org.d80harri.androeira.analyzer.filter;

import org.d80harri.androeira.socket.intf.AcceloratorRawData;

/**
 * Created by d80harri on 24.06.16.
 */
public interface IFilter {
    public AcceloratorRawData filter(AcceloratorRawData value);
}
