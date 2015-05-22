/*
* Copyright (C) 2015 "IMIS-Athena R.C.",
* Institute for the Management of Information Systems, part of the "Athena"
* Research and Innovation Centre in Information, Communication and Knowledge Technologies.
* [http://www.imis.athena-innovation.gr/]
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package privacytool.framework.data;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.apache.jena.riot.RiotException;
import privacytool.framework.anonymizationrules.AnonymizationRules;
import privacytool.framework.dictionary.Dictionary;
import privacytool.gui.ErrorWindow;
import privacytool.gui.data.ChooseMainRDFClassPanel;
import privacytool.gui.resultsTab.AnonymizedDatasetPanel;

/**
 * A class representing RDF data
 * @author serafeim
 */
public class RDFData implements Data {
    private double data[][] = null;
    
    private String inputFile = null;
    private int sizeOfRows = 0;
    private int sizeOfCol = 0;
    private Map <Integer,String> colNamesType = null;
    
    private Map <Integer,String> colNamesPosition = null;
    private Map <String, Integer> colNamesPositionInv =  null;
    private Map <Integer,Dictionary> dictionary = null;
    
    private Model model = null;
    private String mainClass = "";
    private Set<Resource> subjects = null;
    private Set<Property> properties = null;
    private Map<String, String> propLabels = null;
    private Map<String, String> propLabelsInv = null;
    
    public RDFData(String inputFile, String mainClass){
        this.mainClass = mainClass.substring(1, mainClass.length()-1);      //remove first and last '<' and '>'
        this.inputFile = inputFile;
        colNamesType = new TreeMap<>();
        colNamesPosition = new HashMap<>();
        colNamesPositionInv = new HashMap<>();
        propLabels = new HashMap<>();
        propLabelsInv = new HashMap<>();
        dictionary = new HashMap <>();
    }
    
    /**
     * Gets the array of the loaded dataset
     * @return 2-dimensional array of the loaded dataset
     */
    @Override
    public double[][] getData() {
        
        return data;
    }
    
    /**
     * Sets array representing data
     * @param _data a 2-dimensional array to be loaded
     */
    @Override
    public void setData(double[][] _data) {
        this.data = _data;
    }
    
    /**
     * Gets the length of the dataset array
     * @return length of the dataset
     */
    @Override
    public int getDataLenght() {
        return data.length;
    }
    
    /**
     * Prints the dataset
     */
    @Override
    public void print(){
        int i,j;
        for (i = 0 ; i < data.length ; i ++){
            for (j = 0 ; j < data[i].length ; j++){
                System.out.print(data[i][j]+",");
            }
            System.out.println();
        }
    }
    
    /**
     * Executes a preprocessing of the dataset
     */
    @Override
    public void preprocessing() {
        this.properties = new LinkedHashSet<>();
        this.subjects = new LinkedHashSet<>();
        
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.OFF);       //suppress Jena Log4j warnings
        
        //load RDF model from file
        try{
            model = FileManager.get().loadModel(inputFile);
        }catch(RiotException e){
            ErrorWindow.showErrorWindow("Error parsing RDF file: \n" + e.getMessage());
            return;
        }
        Statement stmt = null;
        
        //find subjects of main class
        StmtIterator iter = model.listStatements();
        while (iter.hasNext())
        {
            stmt = iter.next();
            if(stmt.getPredicate().toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type") &&
                    stmt.getObject().toString().equals(this.mainClass)){
                
                subjects.add(stmt.getSubject());
            }
        }
        
        //find overall properties
        for(Resource r : subjects){
            iter = r.listProperties();
            while(iter.hasNext()){
                stmt = iter.next();
                
                if(stmt.getObject().isLiteral()){       //store only properties leading to literals
                    this.properties.add(stmt.getPredicate());
                    
                    //search if predicate has label
                    StmtIterator labelit = stmt.getPredicate().listProperties();
                    Statement labelstmt = null;
                    while(labelit.hasNext()){
                        labelstmt = labelit.next();
                        
                        //label is found
                        if(labelstmt.getPredicate().toString().equals("http://www.w3.org/2000/01/rdf-schema#label")){
                            String uri = stmt.getPredicate().toString();
                            String label = labelstmt.getObject().toString();
                            
                            this.propLabels.put(uri, label);
                            this.propLabelsInv.put(label, uri);
                            break;
                        }
                    }
                }
            }
        }
        
        this.sizeOfRows = subjects.size();
        this.sizeOfCol = this.properties.size();
        
        int i = 0;
        
        //set properties as table columns
        for(Property prop : this.properties){
            
            //if label was found, set label
            String label = this.propLabels.get(prop.toString());
            if(label == null){
                label = prop.toString();
            }
            
            this.colNamesPosition.put(i, label);
            this.colNamesPositionInv.put(label, i);
            this.dictionary.put(i, new Dictionary());
            this.colNamesType.put(i, "string");
            i++;
        }
        
    }
    
    /**
     * Loads dataset from file to memory
     */
    @Override
    public void save() {
        data = new double[sizeOfRows][sizeOfCol];
        
        int stringCount = 1;
        int row = 0;
        
        //fill values
        for(Resource s : this.subjects){
            StmtIterator iter = s.listProperties();
            while(iter.hasNext()){
                Statement stmt = iter.next();
                if(stmt.getObject().isLiteral()){
                    
                    //check if predicate has label
                    String p = this.propLabels.get(stmt.getPredicate().toString());
                    if(p == null){
                        p = stmt.getPredicate().toString();
                    }
                    
                    String o = stmt.getObject().toString();
//                    System.out.println("cell: " + this.colNamesPositionInv.get(p) + " "
//                        + p + " " + o);
                    Integer colPosition = this.colNamesPositionInv.get(p);
                    Dictionary tempDict = dictionary.get(colPosition);
//
                    //if string is not present in the dictionary
                    if (tempDict.containsString(o) == false){
                        tempDict.putIdToString(stringCount, o);
                        tempDict.putStringToId(o, stringCount);
                        dictionary.put(colPosition, tempDict);
                        data[row][colPosition] = stringCount;
                        stringCount++;
                    }
                    else{
                        //if string is present in the dictionary, get its id
                        int stringId = tempDict.getStringToId(o);
                        data[row][colPosition] = stringId;
                    }
                }
            }
            setNULL(data[row]);
            row++;
        }
    }
    
    
    /**
     * Reads dataset from file (preprocessing and load)
     */
    @Override
    public void readDataset() {
        preprocessing();
        save();
    }
    
    /**
     * Gets dictionary for the specified column
     * @param column the number of the column
     * @return the dictionary for the column
     */
    @Override
    public Dictionary getDictionary(Integer column){
        return this.dictionary.get(column);
    }
    
    /**
     * Gets column types
     * @return a map with the column types
     */
    @Override
    public Map<Integer, String> getColumnsTypes() {
        return colNamesType;
    }
    
    /**
     * Gets column names
     * @return a map with the column names by position
     */
    @Override
    public Map<Integer, String> getColumnsPosition() {
        return colNamesPosition;
    }
    
    /**
     * Gets all dictionaries
     * @return a map with with the column dictionaries by position
     */
    @Override
    public Map<Integer, Dictionary> getDictionary() {
        return dictionary;
    }
    
    /**
     * Finds the column number of the column name specified
     * @param column the column name to search for
     * @return the column number of the given column
     */
    @Override
    public int getColumnByName(String column){
        String columnName = getLabelFromURI(column);
        Integer columnNum = this.colNamesPositionInv.get(columnName);
        
        if(columnNum != null)
            return columnNum;
        
        return -1;
    }
    
    /**
     * Sets a new dictionary for a specific column
     * @param column the column number
     * @param dict the new dictionary
     */
    @Override
    public void setDictionary(Integer column, Dictionary dict) {
        this.dictionary.put(column, dict);
    }
    
    /**
     * Replaces the dictionary of a column with a new one. Updates values in this
     * column with those taken from the new dictionary
     * @param column the column number
     * @param dict the new dictionary
     */
    @Override
    public void replaceColumnDictionary(Integer column, Dictionary dict) {
        Dictionary curDict = this.dictionary.get(column);
        
        for (double[] row : data) {
            
            //retrieve actual value from dictionary
            String columnValue = curDict.getIdToString((int)row[column]);
            
            //replace with value from new dictionary
            row[column] = dict.getStringToId(columnValue);
            
        }
        
        //set given dictionary as the new one
        setDictionary(column, dict);
    }
    
    /**
     * Find RDF classes present in a file
     * @param file file to parse
     * @return a set of classes found
     */
    public static Set<String> findClasses(String file){
        Set<String> classes = new HashSet<>();
        try {
            FileInputStream fstream = null;
            
            fstream = new FileInputStream(file);
            
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            
            String strLine;
            
            while ((strLine = br.readLine()) != null){
                String[] tokens = strLine.split(" ");
                if(tokens[1].equals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>")
                        && !tokens[2].equals("<http://www.w3.org/2000/01/rdf-schema#Class>")){
                    classes.add(tokens[2]);
                }
            }
            
            br.close();
        } catch (IOException ex) {
            Logger.getLogger(RDFData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return classes;
    }
    
    /**
     * Show dialog to choose main class to be used
     * @param classes all available classes found in file
     * @return the main class chosen
     */
    public static String showChooseClassDialog(Set<String> classes){
        
        ChooseMainRDFClassPanel panel = new ChooseMainRDFClassPanel(classes);
        int result = JOptionPane.showConfirmDialog(null, panel,
                "Main RDF Class", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return panel.getSelectedClass();
        }
        else{
            return null;
        }
    }
    
    /**
     * Gets URI from label
     * @param label a label
     * @return the URI of the label
     */

    private String getURIFromLabel(String label){
        String uri = this.propLabelsInv.get(label);
        if(uri != null){
            return uri;
        }
        return label;
    }
    
    /**
     * Gets label (if present) from a URI
     * @param uri the URI
     * @return the URI's label
     */
    private String getLabelFromURI(String uri){
        String label = this.propLabels.get(uri);
        if(label != null){
            return label;
        }
        return uri;
    }
    
    /**
     * Gets an array and sets null to the respective dictionaries
     * @param array the array to be used
     */
    private void setNULL(double[] array){
        for(int i=0; i<array.length; i++){
            
            //value must be set to null
            if(array[i] == 0){
                Dictionary tempDict = dictionary.get(i);
                tempDict.putIdToString(0, "NULL");
                tempDict.putStringToId("NULL", 0);
            }
        }
    }
    
    @Override
    public void export(String file, JTable initialTable, JTable anonymizedTable, Set<Integer> qids) {
        //create anonymization rules
        AnonymizationRules rules = new AnonymizationRules();
        for(Integer qid : qids){
            int column = qid + 1;
            String columnName = initialTable.getColumnName(column);
            
            Map<String, String> map = AnonymizationRules.createRules(column, initialTable.getModel(),
                    anonymizedTable.getModel());
            
            rules.setColumnRules(getURIFromLabel(columnName), map);
        }
        
        
        //set with the anonymized properties
        Set<String> anonymizedProperties = rules.getKeyset();
        
        //create new model with anonymized triples
        Model newModel = ModelFactory.createDefaultModel();
        
        Statement stmt = null;
        StmtIterator iter = model.listStatements();
        while (iter.hasNext())
        {
            stmt = iter.next();
            
            //check if predicate is anonymized
            if(anonymizedProperties.contains(stmt.getPredicate().toString()) &&
                    this.subjects.contains(stmt.getSubject())){
                
                String anonObject = rules.get(stmt.getPredicate().toString(),
                        stmt.getObject().toString());
//                    System.out.println(stmt.getSubject().toString() + " " +
//                            stmt.getPredicate().toString() + " " + anonObject);
                Statement newStmt = ResourceFactory.createStatement(stmt.getSubject(),
                        stmt.getPredicate(),  ResourceFactory.createPlainLiteral(anonObject));
                newModel.add(newStmt);
            }
            else{
                newModel.add(stmt);
            }
        }
        
        //write model to file as triples
        FileWriter out = null;
        try {
            out = new FileWriter(file);
            newModel.write( out, "N-Triples" );
        } catch (IOException ex) {
            Logger.getLogger(AnonymizedDatasetPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                out.close();
            }
            catch (IOException closeException) {
                // ignore
            }
        }
        
    }

    @Override
    public String getColumnByPosition(Integer columnIndex) {
        String columnLabel = this.colNamesPosition.get(columnIndex);
        if(columnLabel != null){
            return getURIFromLabel(columnLabel);
        }
        return null;
    }
}
