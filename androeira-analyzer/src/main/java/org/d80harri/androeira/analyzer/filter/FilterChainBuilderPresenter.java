package org.d80harri.androeira.analyzer.filter;

import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import org.d80harri.androeira.analyzer.filter.integrate.IFilterView;
import org.d80harri.androeira.analyzer.filter.integrate.IntegrateFilterView;
import org.d80harri.androeira.analyzer.utils.filter.FilterChain;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by d80harri on 25.06.16.
 */
public class FilterChainBuilderPresenter implements Initializable {

    private static final AvailableEntry[] availableFilters = new AvailableEntry[]{
            new AvailableEntry("Integrate", IntegrateFilterView.class)
    };

    @FXML
    private ListView<AvailableEntry> availableFiltersList;
    @FXML
    private Accordion appliedFilters;
    @FXML
    private Button addBtn;
    private ObservableList<IFilterView> addedFilters = FXCollections.observableArrayList();
    private ObjectProperty<FilterChain> chain = new SimpleObjectProperty<>();

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
        addedFilters.addListener(new ListChangeListener<IFilterView>() {
            @Override
            public void onChanged(Change<? extends IFilterView> c) {
                FilterChain chain = new FilterChain();

                while (c.next()) {
                    if (c.wasAdded()) {
                        List<? extends IFilterView> addedSubList = c.getAddedSubList();
                        List<TitledPane> panesToAdd = addedSubList.stream().map((Function<IFilterView, TitledPane>) iFilterView -> new TitledPane(iFilterView.getName(), iFilterView.getView())).collect(Collectors.toList());
                        appliedFilters.getPanes().addAll(panesToAdd);
                        for (IFilterView filterView : addedSubList) {
                            chain.andThen(filterView.getPresenter().getFilter());
                        }
                    }
                }

                FilterChainBuilderPresenter.this.chain.set(chain);
            }
        });
        addBtn.setOnMouseClicked(event -> this.addSelectedFilter());

        availableFiltersList.getItems().addAll(availableFilters);
    }

    public void addSelectedFilter() {
        AvailableEntry selectedItem = availableFiltersList.getSelectionModel().getSelectedItem();
        try {
            IFilterView iFilterView = selectedItem.viewClass.newInstance();
            addedFilters.add(iFilterView);
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

    public static void main(String[] args) {
        String s1 = "Hello1";
        String s2 = "Hello2";
        String s3 = "Hello3";

        ObjectProperty<String> blubb = new SimpleObjectProperty<>();
        blubb.addListener((obs, o, n) -> System.out.println(n));

        blubb.setValue(s1);
        blubb.setValue(s2);
        blubb.setValue(s2);
    }
}
