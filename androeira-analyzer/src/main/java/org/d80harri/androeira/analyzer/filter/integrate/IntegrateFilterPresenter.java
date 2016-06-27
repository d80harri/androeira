package org.d80harri.androeira.analyzer.filter.integrate;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import org.d80harri.androeira.analyzer.utils.filter.IFilter;
import org.d80harri.androeira.analyzer.utils.filter.IntegrateFilter;

/**
 * Created by d80harri on 24.06.16.
 */
public class IntegrateFilterPresenter implements IFilterPresenter {
    public Button appy;
    public CheckBox interpolate;

    @Override
    public IFilter getFilter() {
        return new IntegrateFilter(interpolate.isSelected());
    }
}
