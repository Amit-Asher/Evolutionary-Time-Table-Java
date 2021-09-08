package MainForm.Pages.Statistics;

import EvolutionEngineImpl.HistoryDocument;
import TimeTableModel.EvolutionaryTimeTableModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    private EvolutionaryTimeTableModel modelRef;

    @FXML
    private VBox VBoxStatistics;

    @FXML
    private javafx.scene.chart.LineChart<?, ?> LineChart;

    @FXML
    private CategoryAxis GenerationAxis;

    @FXML
    private NumberAxis FitnessAxis;

    public void setDescriptorRef(EvolutionaryTimeTableModel modelRef) {
        this.modelRef = modelRef;
    }

    public VBox getVboxMain() {
        if(LineChart.getData() != null)
            LineChart.getData().clear();

        LineChart.getXAxis().setLabel("Generation");
        LineChart.getYAxis().setLabel("Fitness");

        XYChart.Series series = new XYChart.Series();
        List<HistoryDocument> historyDocuments = modelRef.GetGenerationHistory();
        historyDocuments.forEach(hd -> {
            series.getData().add(new XYChart.Data(Integer.toString(hd.getGenerationDoc()), hd.getBestFitnessDoc()));
        });
        series.setName("Generation to Fitness Performance");

        LineChart.getData().addAll(series);


        return VBoxStatistics;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}