package org.d80harri.androeira.analyzer.filter;

import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import org.d80harri.androeira.analyzer.filter.integrate.IFilterView;
import org.d80harri.androeira.analyzer.filter.integrate.IntegrateFilterView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by d80harri on 25.06.16.
 */
public class FilterChainBuilderPresenter implements Initializable {
    private static final AvailableEntry[] availableFilters = new AvailableEntry[] {
        new AvailableEntry("Integrate", IntegrateFilterView.class)
    };

    public ListView<AvailableEntry> availableFiltersList;
    public Accordion appliedFilters;
    public Button addBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        availableFiltersList.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<AvailableEntry>() {
            @Override
            public String toString(AvailableEntry object) {
                return object.name;
            }

            @Override
            public AvailableEntry fromString(String string) {
                throw new RuntimeException("NYI");
            }
        }));
        addBtn.setOnMouseClicked(event -> this.applySelectedFilter());

        availableFiltersList.getItems().addAll(availableFilters);
    }

    public void applySelectedFilter() {
        AvailableEntry selectedItem = availableFiltersList.getSelectionModel().getSelectedItem();
        try {
            IFilterView iFilterView = selectedItem.viewClass.newInstance();
            appliedFilters.getPanes().add(new TitledPane(selectedItem.name, iFilterView.getView()));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static class AvailableEntry {
        public Class<? extends IFilterView> viewClass;
        public String name;

        public AvailableEntry(String name, Class<IntegrateFilterView> viewClass) {
            this.name = name;
            this.viewClass = viewClass;
        }
    }
}
