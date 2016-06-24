package org.d80harri.androeira.analyzer.filter;

import org.assertj.core.api.Assertions;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by d80harri on 24.06.16.
 */
public class FilterChainTest {
    @Test
    public void apply() throws Exception {
        FilterChain chain = new FilterChain();

        IFilter filter = i -> new AcceloratorRawData(i.getTimestamp(), i.getX()+1, i.getY()+1, i.getZ()+1);

        chain.andThen(filter).andThen(filter).andThen(filter);

        AcceloratorRawData result = chain.apply(new AcceloratorRawData(1, 2, 3, 4));

        Assertions.assertThat(result.getTimestamp()).isEqualTo(1);
        Assertions.assertThat(result.getX()).isEqualTo(5);
        Assertions.assertThat(result.getY()).isEqualTo(6);
        Assertions.assertThat(result.getZ()).isEqualTo(7);
    }

    @Test
    public void andThen() throws Exception {
        FilterChain chain = new FilterChain();

        Assertions.assertThat(chain.list).isEmpty();
        Assertions.assertThat(chain.head).isNull();
        Assertions.assertThat(chain.tail).isNull();

        IFilter f1 = new MockFilter();
        IFilter f2 = new MockFilter();
        IFilter f3 = new MockFilter();

        chain.andThen(f1);
        Assertions.assertThat(chain.list).extracting(i -> i.getFilter()).containsExactly(f1);
        Assertions.assertThat(chain.head.getFilter()).isSameAs(f1);
        Assertions.assertThat(chain.tail.getFilter()).isSameAs(f1);

        chain.andThen(f2);
        Assertions.assertThat(chain.list).extracting(i -> i.getFilter()).containsExactly(f1, f2);
        Assertions.assertThat(chain.head.getFilter()).isSameAs(f1);
        Assertions.assertThat(chain.tail.getFilter()).isSameAs(f2);

        chain.andThen(f3);
        Assertions.assertThat(chain.list).extracting(i -> i.getFilter()).containsExactly(f1, f2, f3);
        Assertions.assertThat(chain.head.getFilter()).isSameAs(f1);
        Assertions.assertThat(chain.tail.getFilter()).isSameAs(f3);
    }


    private static final class MockFilter implements IFilter {

        @Override
        public AcceloratorRawData filter(AcceloratorRawData value) {
            throw new RuntimeException("Not intended to run");
        }
    }
}
