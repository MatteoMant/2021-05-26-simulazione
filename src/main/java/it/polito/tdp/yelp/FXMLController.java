/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.yelp;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnLocaleMigliore"
    private Button btnLocaleMigliore; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbCitta"
    private ComboBox<String> cmbCitta; // Value injected by FXMLLoader

    @FXML // fx:id="txtX"
    private TextField txtX; // Value injected by FXMLLoader

    @FXML // fx:id="cmbAnno"
    private ComboBox<Integer> cmbAnno; // Value injected by FXMLLoader

    @FXML // fx:id="cmbLocale"
    private ComboBox<Business> cmbLocale; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	Business partenza = cmbLocale.getValue();
    	if (partenza == null) {
    		txtResult.appendText("Per favore selezionare un locale!\n");
    		return;
    	}
    	
    	double soglia = -1;
    	try {
    		soglia = Double.parseDouble(txtX.getText());
    		if (soglia < 0.0 || soglia > 1.0) {
    			txtResult.appendText("Inserire come soglia un numero compreso tra 0 e 1!\n");
    			return;
    		}
    	} catch (NumberFormatException e) {
    		txtResult.appendText("Inserire come soglia un numero compreso tra 0 e 1!\n");
    		return;
    	}
    	
    	// se sono arrivato qui possiamo calcolare il percorso migliore tra 'partenza' e 'arrivo'
    	Business arrivo = this.model.calcolaLocaleMigliore();
    	
    	List<Business> percorsoMigliore = this.model.calcolaPercorsoMigliore(partenza, arrivo, soglia);
    	
    	if (percorsoMigliore == null) {
    		txtResult.appendText("Non esiste un percorso\n");
    	} else {
    		txtResult.appendText("Il percorso migliore per arrivare dal locale '" + partenza + "' al locale '" + arrivo +"' è: \n\n");
    		for (Business b : percorsoMigliore) {
    			txtResult.appendText(b + "\n");
    		}
    	}
    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	cmbLocale.getItems().clear();
    	String citta = cmbCitta.getValue();
    	if (citta == null) {
    		txtResult.setText("Per favore selezionare una citta!\n");
    		return;
    	}
    	Integer anno = cmbAnno.getValue();
    	if (anno == null) {
    		txtResult.setText("Per favore selezionare un anno!\n");
    		return;
    	}
    	this.model.creaGrafo(citta, anno);
    	
    	txtResult.setText("Grafo creato!\n");
    	txtResult.appendText("# Vertici: " + this.model.getNumVertici() + "\n");
    	txtResult.appendText("# Archi: " + this.model.getNumArchi() + "\n");
    	
    	List<Business> locali = new LinkedList<>(this.model.getAllVertici());
    	Collections.sort(locali);
    	cmbLocale.getItems().addAll(locali);
    }

    @FXML
    void doLocaleMigliore(ActionEvent event) {
    	txtResult.clear();
    	Business localeMigliore = this.model.calcolaLocaleMigliore();
    	txtResult.appendText("LOCALE MIGLIORE: " + localeMigliore + "\n");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnLocaleMigliore != null : "fx:id=\"btnLocaleMigliore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbCitta != null : "fx:id=\"cmbCitta\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtX != null : "fx:id=\"txtX\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbLocale != null : "fx:id=\"cmbLocale\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	List<String> citta = this.model.getAllCitta();
    	Collections.sort(citta);
    	cmbCitta.getItems().addAll(citta);
    	for(int anno = 2005; anno <= 2013; anno++) {
    		cmbAnno.getItems().add(anno);
    	}
    }
}
