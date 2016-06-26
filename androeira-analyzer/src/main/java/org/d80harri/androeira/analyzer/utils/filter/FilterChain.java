package org.d80harri.androeira.analyzer.utils.filter;

import org.d80harri.androeira.socket.intf.AcceloratorRawData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d80harri on 24.06.16.
 */
public class FilterChain {
    FilterChainEntry head;
    FilterChainEntry tail;
    List<FilterChainEntry> list = new ArrayList<>();

    public FilterChain andThen(IFilter filter) {
        tail = new FilterChainEntry(filter, null, tail);

        if (list.isEmpty()) {
            head = tail;
        }

        list.add(tail);

        return this;
    }

    public AcceloratorRawData apply(AcceloratorRawData value) {
        AcceloratorRawData result = value;
        for (FilterChainEntry entry : list) {
            result = entry.getFilter().filter(result);
        }
        return result;
    }

    protected static class FilterChainEntry {
        final IFilter filter;
        private final FilterChainEntry next;
        private final FilterChainEntry prev;

        public FilterChainEntry(IFilter filter, FilterChainEntry next, FilterChainEntry prev) {
            this.filter = filter;
            this.next = next;
            this.prev = prev;
        }

        public IFilter getFilter() {
            return filter;
        }

        public FilterChainEntry getNext() {
            return next;
        }

        public FilterChainEntry getPrev() {
            return prev;
        }
    }
}
