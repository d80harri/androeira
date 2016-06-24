package org.d80harri.androeira.analyzer.filter;

import org.assertj.core.api.Assertions;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by d80harri on 24.06.16.
 */
public class IntegrateFilterTest {

    @Test
    public void filterWithoutInterpolate() throws Exception {
        IntegrateFilter filter = new IntegrateFilter(false);

        AcceloratorRawData running = filter.filter(new AcceloratorRawData(10, 1, 1, 1));
        Assertions.assertThat(running.getTimestamp()).isEqualTo(10);
        Assertions.assertThat(running.getX()).isEqualTo(0);
        Assertions.assertThat(running.getY()).isEqualTo(0);
        Assertions.assertThat(running.getZ()).isEqualTo(0);

        running = filter.filter(new AcceloratorRawData(20, 2, 2, 2));
        Assertions.assertThat(running.getTimestamp()).isEqualTo(20);
        Assertions.assertThat(running.getX()).isEqualTo(10);
        Assertions.assertThat(running.getY()).isEqualTo(10);
        Assertions.assertThat(running.getZ()).isEqualTo(10);
    }

    @Test
    public void filterWithInterpolate() throws Exception {
        IntegrateFilter filter = new IntegrateFilter(true);

        AcceloratorRawData running = filter.filter(new AcceloratorRawData(10, 1, 1, 1));
        Assertions.assertThat(running.getTimestamp()).isEqualTo(10);
        Assertions.assertThat(running.getX()).isEqualTo(0);
        Assertions.assertThat(running.getY()).isEqualTo(0);
        Assertions.assertThat(running.getZ()).isEqualTo(0);

        running = filter.filter(new AcceloratorRawData(20, 2, 2, 2));
        Assertions.assertThat(running.getTimestamp()).isEqualTo(20);
        Assertions.assertThat(running.getX()).isEqualTo(15);
        Assertions.assertThat(running.getY()).isEqualTo(15);
        Assertions.assertThat(running.getZ()).isEqualTo(15);
    }

}