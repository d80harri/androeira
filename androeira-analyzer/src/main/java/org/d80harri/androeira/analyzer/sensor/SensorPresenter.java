package org.d80harri.androeira.analyzer.sensor;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import org.d80harri.androeira.socket.client.Client;
import org.d80harri.androeira.socket.client.ServiceLocator;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.d80harri.androeira.socket.intf.ServiceLocation;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SensorPresenter implements Initializable {
    private static final Logger logger = Logger.getLogger(SensorPresenter.class);

    @FXML
    public NumberAxis xAxis;
    @FXML
    private LineChart<Long, Float> acceleratorChart;
    @FXML
    private ComboBox serviceLocation;

    private XYChart.Series<Long, Float> xSeries;
    private XYChart.Series<Long, Float> ySeries;
    private XYChart.Series<Long, Float> zSeries;
    private Client client;
    private ServiceLocator serviceLocator = new ServiceLocator();

   private long firstTs = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceLocation.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell call(ListView param) {
                return new ListCell<ServiceLocation>() {

                    private final Text text;
                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        text = new Text();
                    }

                    @Override
                    protected void updateItem(ServiceLocation item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            text.setText("[none]");
                        } else {
                            text.setText(item.getName() + "(" + item.getAddress() + " on port " + item.getPort() + ")");
                        }
                        setGraphic(text);
                    }
                };
            }
        });
        serviceLocation.valueProperty().addListener((observable, oldValue, newValue) -> {
            ServiceLocation serviceLocation = (ServiceLocation) newValue;
            client = new Client(serviceLocation.getAddress(), serviceLocation.getPort());
            try {
                client.openConnection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            new Thread(() -> {
                try {
                    AcceloratorRawData data;
                    while((data = client.read()) != null) {
                        final AcceloratorRawData _data = data;
                        Platform.runLater(() -> {
                            long timestamp = _data.getTimestamp();
                            if (firstTs == -1)
                                firstTs = timestamp;
                            timestamp -= firstTs;
                            xSeries.getData().add(new XYChart.Data<>(timestamp, _data.getX()));
                            ySeries.getData().add(new XYChart.Data<>(timestamp, _data.getY()));
                            zSeries.getData().add(new XYChart.Data<>(timestamp, _data.getZ()));
                        });
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }).start();
        });
        xSeries = new XYChart.Series<>();
        ySeries = new XYChart.Series<>();
        zSeries = new XYChart.Series<>();

        acceleratorChart.getData().add(xSeries);
        acceleratorChart.getData().add(ySeries);
        acceleratorChart.getData().add(zSeries);

        serviceLocator.setServiceAddedListener((s) -> Platform.runLater(() -> this.onServiceAdded(s)));
        try {
            serviceLocator.start();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void onServiceAdded(ServiceLocation serviceLocation) {
        this.serviceLocation.getItems().add(serviceLocation);
    }

}
